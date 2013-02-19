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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import android.view.View;
import android.view.ViewGroup;

/**
 * Inflates a view from a class in a layout file that uses the LayoutAdapter annotation
 * @author Phil Brown
 *
 */
public class LayoutAdapterInflater extends ClassAnnotationInflater {
	
	private AbstractLayoutAdapter layout;
	
	private Class<?> _layout;
	
	/**
	 * Constructor
	 * @param context used to create views and access resources. This is automatically saved
	 * to the {@link ClassAnnotationInflater#context context} variable in the super class.
	 * @param parent the parent {@code ClassAnnotationInflater} in this view hierarchy
	 */
	public LayoutAdapterInflater(AbLEActivity context, ClassAnnotationInflater parent) {
		super(context, parent);
	}

	@Override
	public View inflate(Class<?> layout) {
		
		LayoutAdapter adapter = layout.getAnnotation(LayoutAdapter.class);
		
		_layout = layout;
		
		createView(adapter);
		if (view == null)
			return null;
		
		if (!adapter.handleSettersFirst())
		{
			handleFields(layout);
			handleGetters(this.layout.getClass().getName());
			handleBindings();
			performSetters(this.layout.getClass().getName());
		}
		
		handleChildViews(layout);
		onLayoutComplete(layout, view);
	 	return view;
		
	}
	
	/**
	 * Creates the view by asking a new instance of the class specified by 
	 * {@link LayoutAdapter#adapterClass()} for a View. The class must be an instance
	 * of {@link AbstractLayoutAdapter}, or this will fail.
	 * @param adapter
	 */
	private void createView(LayoutAdapter adapter)
	{
		try
		{
			Class<?> _class = Class.forName(adapter.adapterClass());
			Constructor<?> _constructor = _class.getConstructor(new Class<?>[]{AbLEActivity.class});
			layout = (AbstractLayoutAdapter) _constructor.newInstance(context);
			
			if (adapter.handleSettersFirst())
			{
				handleFields(_layout);
				handleGetters(this.layout.getClass().getName());
				handleBindings();
				performSetters(this.layout.getClass().getName());
			}
			
			view = layout.getView();
			

			float[] origin = adapter.origin();
			view.setX(origin[0]);
			view.setY(origin[1]);
			
			//Layout Params
			String[] params = adapter.params();
			int width, height;
			if (params[0].equals("match_parent"))
			{
				width = ViewGroup.LayoutParams.MATCH_PARENT;
			}
			else if (params[0].equals("fill_parent"))
			{
				width = ViewGroup.LayoutParams.FILL_PARENT;
			}
			else if (params[0].equals("wrap_content"))
			{
				width = ViewGroup.LayoutParams.WRAP_CONTENT;
			}
			else
			{
				width = (int) Float.parseFloat(params[0]);
			}
			

			if (params[1].equals("match_parent"))
			{
				height = ViewGroup.LayoutParams.MATCH_PARENT;
			}
			else if (params[1].equals("fill_parent"))
			{
				height = ViewGroup.LayoutParams.FILL_PARENT;
			}
			else if (params[1].equals("wrap_content"))
			{
				height = ViewGroup.LayoutParams.WRAP_CONTENT;
			}
			else
			{
				height = (int) Float.parseFloat(params[1]);
			}
						
			view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
			
		} catch (Throwable t)
		{
			AbLEUtil.err("Could not create adapter %s", 
					adapter.adapterClass() == null ? "null" : adapter.adapterClass());
			t.printStackTrace();
		}
	}
	
	/**
	 * Redirects the setters to {@link #layout}
	 */
	@Override
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
				m.invoke(layout, params);
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
	
	@Override
	protected void handleGetters(String viewClass)
	{
		
		for (Map.Entry<Field, Map<String,Map<List<Class<?>>,List<Object>>>> method : getters.entrySet())
		{
			Field f = method.getKey();
			Map<String, Map<List<Class<?>>, List<Object>>> _info = method.getValue();
			for (Entry<String, Map<List<Class<?>>, List<Object>>> entry : _info.entrySet())
			{
				String methodName = entry.getKey();
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
					Object returnVal = m.invoke(layout, params);
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

}
