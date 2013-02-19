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

/**
 * Used to add an invisible element into an AbLE layout
 * @author Phil Brown
 *
 */
public class InvisibleInflater extends ClassAnnotationInflater 
{

	private AbstractInvisible ghost;
	private AbLEActivity context;
	
	public InvisibleInflater(AbLEActivity context, ClassAnnotationInflater parent) {
		super(context, parent);
		this.context = context;
	}

	@Override
	public View inflate(Class<?> layout) {
		Invisible invisible = layout.getAnnotation(Invisible.class);
		create(invisible);
		handleFields(layout);
		handleInstanceMethods(ghost.getClass().getName());
		handleBindings();
		performSetters(ghost.getClass().getName());
		onLayoutComplete(layout, ghost);
		
		return null;
	}
	
	/**
	 * Creates the Invisible Object.
	 * @param invisible
	 */
	private void create(Invisible invisible)
	{
		try
		{
			Class<?> _class = Class.forName(invisible.invisibleClass());
			Constructor<?> _constructor = _class.getConstructor(new Class<?>[]{AbLEActivity.class});
			ghost = (AbstractInvisible) _constructor.newInstance(context);
			ghost.create();
			
		} catch (Throwable t)
		{
			AbLEUtil.err("Could not create adapter %s", 
					invisible.invisibleClass() == null ? "null" : invisible.invisibleClass());
			t.printStackTrace();
		}
	}
	
	/**
	 * Redirects setter methods to {@link #ghost}
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
				m.invoke(ghost, params);
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
					Object returnVal = m.invoke(ghost, params);
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
