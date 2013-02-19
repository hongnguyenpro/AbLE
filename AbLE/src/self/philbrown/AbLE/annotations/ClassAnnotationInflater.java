/*
 * Copyright 2013 Phil Brown

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package self.philbrown.AbLE.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import android.view.View;
import android.view.ViewGroup;

/**
 * Superclass to all annotation-based view inflaters. Provides several methods to simplify the 
 * inflation process, including handlers for field and inner classes (child views).
 * @author Phil Brown
 */
public abstract class ClassAnnotationInflater 
{
	/** Used by child classes to create their {@link View}s and access resources. */
	protected AbLEActivity context;
	
	/** Contains the fields discovered with {@link Variable} annotations within the class. */
	public Map<String, Object> variables;
	/** Contains the Annotated children discovered within this class */
	public Map<Class<?>, Object> children;
	/** Contains the fields discovered with the {@link Binding} annotations.*/
	public List<Field> bindings;
	/** 
	 * Contains a Collection of setter methods to call once the class is loaded. These have been
	 * discovered with the annotation {@link Setter}, and will usually contain more than one parameter.
	 * Stored as:<pre>
	 * &lt;Method Name, &lt;&lt;parameter classes&gt;, &lt;parameters&gt;&gt;&gt;. 
	 * </pre>
	 */
	public Map<String, Map<List<Class<?>>, List<Object>>> setters;
	/** 
	 * Contains a Collection of getter methods to call once the class is loaded. These have been
	 * discovered with the annotation {@link Getter}, and will usually contain more than one parameter.
	 * Stored as:<pre>
	 * &lt;Field &lt;Method Name, &lt;&lt;parameter classes&gt;, &lt;parameters&gt;&gt;&gt;&gt;. 
	 * </pre>
	 */
	public Map<Field, Map<String, Map<List<Class<?>>, List<Object>>>> getters;
	/** The view that this class is creating */
	public View view;
	
	/**
	 * Provides access to the parent view and the fields declared in the parent layout. This
	 * allows binded values to look for variables is parent classes, maintaining the expected
	 * variables hierarchy.
	 */
	public ClassAnnotationInflater parent;
	
	/**
	 * Constructor
	 * @param context used by child views to create the view and access resources
	 * @param parent used to allow access to parent variables and classes, so bound values can use them. The root parent is null.
	 */
	public ClassAnnotationInflater(AbLEActivity context, ClassAnnotationInflater parent)
	{
		variables = new HashMap<String, Object>();
		setters = new HashMap<String, Map<List<Class<?>>, List<Object>>>();
		children = new HashMap<Class<?>, Object>();
		bindings = new ArrayList<Field>();
		getters = new HashMap<Field, Map<String, Map<List<Class<?>>, List<Object>>>>();
		this.parent = parent;
		this.context = context;
	}
	
	/**
	 * Inflate the layout. This should follow a general pattern of creating the view (if this
	 * is a view-based annotation), then handling fields, bindings, setters, child views (if
	 * applicable), then returning the view. For example:<pre>
	 * public View inflate(Class<?> layout)
	 * {
	 * 	MyAnnotation annotation = layout.getAnnotation(MyAnnotation.class);
	 * 	
	 *	//Handle annotation attributes here
	 *	view = new View(context);//etc
	 *	if (view == null)
	 *		return null;
	 *	
	 *	handleFields(layout);
	 *	handleInstanceMethods(view);
	 *	handleBindings();
	 *	performSetters(view);
	 *	handleChildViews(layout);
	 *  onLayoutComplete(layout, view);
	 * 	return view;
	 * }
	 * </pre>
	 * @param layout the class to layout as a view, controller, or other object
	 * @return the view that was inflated, or null if no view is used.
	 */
	public abstract View inflate(Class<?> layout);
	
	/**
	 * @return the {@code ClassAnnotationInflater} that contains this 
	 * {@code ClassAnnotationInflater}, or {@code null} if none exists.
	 */
	public ClassAnnotationInflater getParent()
	{
		return parent;
	}
	
	/**
	 * Loops through the {@link Fields} declared in this class and hands them off a
	 * {@link FieldAnnotationInflater} to be handled as {@link Binding}s, {@link Variable}s,
	 * and setters.
	 * @param layout the class declaration of the view that has been created.
	 */
	protected void handleFields(Class<?> layout)
	{
		FieldAnnotationInflater fi = new FieldAnnotationInflater(context);
		for (Field f : layout.getFields())
		{
			try {
				fi.inflate(f, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * If the class has a static method called "onLayoutComplete", it is invoked after all fields are handled. The first
	 * parameter is of type {@link NPEActivity}, and the second parameter depends on the type of inflater.
	 * Default is {@code View}.
	 * @param layout
	 * @param obj
	 */
	protected void onLayoutComplete(Class<?> layout, Object obj)
	{
		try {
			for (Method m : layout.getMethods())
			{
				if (m.getName().equals("onLayoutComplete"))
				{
					m.invoke(null, context, obj);
					break;
				}
			}
//			Method main = layout.getMethod("main", new Class[]{NPEActivity.class, obj.getClass()});
//			NPE.info("Invoking method main(NPEActivity, %s)", obj.getClass().getSimpleName());
//			main.invoke(null, context, obj);
		} catch (Throwable t) {
			//No Main method.
		}
	}
	
	/**
	 * Sets the fields that use the {@code @Getter} annotation to the values retrieved from
	 * calling the specified getter methods.
	 * @param viewClass the String name of the class whose getters are being accessed
	 */
	protected void handleGetters(String viewClass)
	{
		
		for (Map.Entry<Field, Map<String,Map<List<Class<?>>,List<Object>>>> method : getters.entrySet())
		{
			Field f = method.getKey();
			Map<String, Map<List<Class<?>>, List<Object>>> _info = method.getValue();
			for (Entry<String, Map<List<Class<?>>, List<Object>>> entry : _info.entrySet())
			{
				List<Class<?>> type = null;
				try
				{
					String name = entry.getKey();
					
					List<Object> value = null;
					for (Entry<List<Class<?>>, List<Object>> info : entry.getValue().entrySet())
					{
						//there should only be one entry here, so it is not a big time consumer
						type = info.getKey();
						value = info.getValue();
						break;
					}
					Class<?>[] classes = new Class<?>[type.size()];
					classes = type.toArray(classes);
					Method m = Class.forName(viewClass).getMethod(name, classes);
					Object[] params = new Object[value.size()];
					params = value.toArray(params);
					Object returnVal = m.invoke(view, params);
					f.set(null, returnVal);
				} catch (Throwable t)
				{
					StringBuilder b = new StringBuilder();
					for (int i = 0; i < type.size(); i++)
					{
						b.append(type.get(i).getSimpleName());
						if (i != type.size() - 1)
						{
							b.append(", ");
						}
					}
					AbLEUtil.err("Could not call method %s(%s) on class %s", method.getKey(), b.toString(), viewClass);
				}
			}
		}
		
		
		
	}
	
	/**
	 * Convenience method to calling {@link #handleGetters(String)}. This merely calls 
	 * {@link Class#getName()} on the view and passes it to the base method.
	 * @param v this is not necessarily the view that the getters are performed on. they
	 * are called on {@link #view}.
	 */
	protected void handleGetters(View v)
	{
		handleGetters(v.getClass().getName());
	}
	
	/**
	 * Sets the fields that use the {@code @InstanceMethod} annotation to the values retrieved from
	 * calling the specified getter methods.
	 * @param viewClass the String name of the class whose getters are being accessed
	 * @deprecated use {@link #handleGetters(String)}
	 */
	protected void handleInstanceMethods(String viewClass)
	{
		handleGetters(viewClass);
	}
	
	/**
	 * Convenience method to calling {@link #handleInstanceMethods(String)}. This merely calls 
	 * {@link Class#getName()} on the view and passes it to the base method.
	 * @param v this is not necessarily the view that the getters are performed on. they
	 * are called on {@link #view}.
	 * @deprecated use {@link #handleGetters(View)}
	 */
	protected void handleInstanceMethods(View v)
	{
		handleInstanceMethods(v.getClass().getName());
	}
	
	/**
	 * Convenience method to calling {@link #performSetters(String)}. This merely calls 
	 * {@link Class#getName()} on the view and passes it to the base method.
	 * @param v this is not necessarily the view that the setters are performed on. they
	 * are called on {@link #view}.
	 */
	protected void performSetters(View v)
	{
		performSetters(v.getClass().getName());
	}
	
	/**
	 * Uses the setter information stored in {@link #setters} to set values in {@link #view}
	 * @param viewClass the String name of the class whose setters are being accessed
	 */
	protected void performSetters(String viewClass)
	{
		for (Map.Entry<String,Map<List<Class<?>>,List<Object>>> method : setters.entrySet())
		{
			List<Class<?>> type = null;
			try
			{
				String name = method.getKey();
				
				List<Object> value = null;
				for (Entry<List<Class<?>>, List<Object>> info : method.getValue().entrySet())
				{
					//there should only be one entry here, so it is not a big time consumer
					type = info.getKey();
					value = info.getValue();
					break;
				}
				Class<?>[] classes = new Class<?>[type.size()];
				classes = type.toArray(classes);
				Method m = Class.forName(viewClass).getMethod(name, classes);
				Object[] params = new Object[value.size()];
				params = value.toArray(params);
				m.invoke(view, params);
			} catch (Throwable t)
			{
				StringBuilder b = new StringBuilder();
				for (int i = 0; i < type.size(); i++)
				{
					b.append(type.get(i).getSimpleName());
					if (i != type.size() - 1)
					{
						b.append(", ");
					}
				}
				AbLEUtil.err("Could not call method %s(%s) on class %s", method.getKey(), b.toString(), viewClass);
			}
		}
	}
	
	/**
	 * Uses the bindings/variables information to set bound values. If the variable that the object is bound to was declared in a different, parent
	 * class, it will still be found and used.
	 */
	protected void handleBindings()
	{
		for (Field f : bindings)
		{
			Binding binder = null;
			try
			{
				binder = f.getAnnotation(Binding.class);
				
				ClassAnnotationInflater inflater = this;
				Object newVal = null;
				while (inflater != null && newVal == null)
				{
					Map<String, Object> vars = inflater.variables;
					for (String s : vars.keySet())
					{
						if (binder.observedValue().equals(s))
						{
							newVal = vars.get(s);
							if (newVal != null)
								f.set(null, newVal);
							
							//otherwise, the default value is used. 
						}
						//DON'T Add these to the #setters with the correct value. FIXME this means that the binded value only works upon first inflation.
//						Map<List<Class<?>>, List<Object>> setter = new HashMap<List<Class<?>>, List<Object>>();
//						Class<?> type = f.getType();
//						List<Class<?>> classes = new ArrayList<Class<?>>();
//						classes.add(type);
//						Object value = f.get(null);
//						List<Object> objects = new ArrayList<Object>();
//						objects.add(value);
//						setter.put(classes, objects);
//						String method = NPE.buildString("set", AnnotatedLayoutInflater.capitalize(f.getName()));
//						setters.put(method, setter);
					}
					
					
					inflater = inflater.getParent();
				}
				if (newVal == null)
				{
					//This happens when no variable is found
					Map<List<Class<?>>, List<Object>> setter = new HashMap<List<Class<?>>, List<Object>>();
					Class<?> type = f.getType();
					List<Class<?>> classes = new ArrayList<Class<?>>();
					classes.add(type);
					Object value = f.get(null);
					List<Object> objects = new ArrayList<Object>();
					objects.add(value);
					setter.put(classes, objects);
					String method = AbLEUtil.buildString("set", AnnotatedLayoutInflater.capitalize(f.getName()));
					setters.put(method, setter);
				}
			} catch (Throwable t)
			{
				AbLEUtil.err("Could not bind variable %s to %s!", f.getName(), binder.observedValue());
			}
		}
	}
	
	/**
	 * If {@link #view} is a {@link ViewGroup}, its child views are created, if the given class
	 * contains any inner classes.
	 * @param layout the current layout class
	 */
	protected void handleChildViews(Class<?> layout)
	{
		if (view != null && view instanceof ViewGroup)
		{
			for (Class<?> child : layout.getClasses())
			{
				View v = AnnotatedLayoutInflater.inflate(context, child, this);
				if (v != null)//since it could be an Invisible or a Controller
					((ViewGroup) view).addView(v);
			}
		}
	}
	
}
