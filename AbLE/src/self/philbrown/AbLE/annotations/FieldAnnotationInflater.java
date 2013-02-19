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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;

/**
 * This class handles declared fields during layout inflation.
 * @author Phil Brown
 *
 */
public class FieldAnnotationInflater 
{
	
	/**
	 * Instantiate the inflater
	 * @param context
	 */
	public FieldAnnotationInflater(AbLEActivity context)
	{
		
	}
	
	/**
	 * This method handles fields in the layout. To do this it first looks for annotations that 
	 * are relevant to the layout system. If a {@link Variable} or a {@link Binding} is found, the parent is notified (and 
	 * it is used once the View has been created). If no annotation is found, then the field is translated into a
	 * setter method and handed to the parent for later use.
	 * @param field the <br>static</b> field that is found. 
	 * @param parent the Class that is currently being handled by the layout inflater
	 * @throws Exception if the field is not declared as <b>public static</b>.
	 */
	public void inflate(Field field, ClassAnnotationInflater parent) throws Exception
	{
		if (field.isAnnotationPresent(Getter.class))
		{
			Object value = field.get(null);
			
			Map<List<Class<?>>, List<Object>> getter = new HashMap<List<Class<?>>, List<Object>>();
			List<Class<?>> classes = new ArrayList<Class<?>>();
			List<Object> parameters = new ArrayList<Object>();
			Object[] obj = (Object[]) value;
			for (Object o : obj)
			{
				Class<?> clazz = o.getClass();
				if (clazz.equals(Byte.class) || clazz.equals(Byte.TYPE))
					classes.add(byte.class);
				else if (clazz.equals(Double.class) || clazz.equals(Double.TYPE))
					classes.add(double.class);
				else if (clazz.equals(Float.class) || clazz.equals(Float.TYPE))
					classes.add(float.class);
				else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE))
					classes.add(int.class);
				else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE))
					classes.add(long.class);
				else if (clazz.equals(Short.class) || clazz.equals(Short.TYPE))
					classes.add(short.class);
				else if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE))
					classes.add(boolean.class);
				else
					classes.add(clazz);
				
				parameters.add(o);
			}
			getter.put(classes, parameters);
			String method = field.getAnnotation(Getter.class).methodName();
			if (method.equals(""))
				method = AbLEUtil.buildString("get", AnnotatedLayoutInflater.capitalize(field.getName()));
			Map<String, Map<List<Class<?>>, List<Object>>> m = parent.getters.get(field);//.put(method, getter);
			if (m == null)
				m = new HashMap<String, Map<List<Class<?>>, List<Object>>>();
			m.put(method, getter);
			parent.getters.put(field, m);
			
		}
		else if (field.isAnnotationPresent(Variable.class))
		{
			String varName = field.getName();
			parent.variables.put(varName, field.get(null));
		}
		else if (field.isAnnotationPresent(Setter.class))
		{
			Object value = field.get(null);
			if (value instanceof Object[])
			{
				Map<List<Class<?>>, List<Object>> setter = new HashMap<List<Class<?>>, List<Object>>();
				List<Class<?>> classes = new ArrayList<Class<?>>();
				List<Object> parameters = new ArrayList<Object>();
				Object[] obj = (Object[]) value;
				for (Object o : obj)
				{
					Class<?> clazz = o.getClass();
					if (clazz.equals(Byte.class) || clazz.equals(Byte.TYPE))
						classes.add(byte.class);
					else if (clazz.equals(Double.class) || clazz.equals(Double.TYPE))
						classes.add(double.class);
					else if (clazz.equals(Float.class) || clazz.equals(Float.TYPE))
						classes.add(float.class);
					else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE))
						classes.add(int.class);
					else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE))
						classes.add(long.class);
					else if (clazz.equals(Short.class) || clazz.equals(Short.TYPE))
						classes.add(short.class);
					else if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE))
						classes.add(boolean.class);
					else
						classes.add(clazz);
					
					parameters.add(o);
				}
				setter.put(classes, parameters);
				String method = AbLEUtil.buildString("set", AnnotatedLayoutInflater.capitalize(field.getName()));
				
				parent.setters.put(method, setter);
			}
			else
			{
				AbLEUtil.warn("Invalid Setter type. Must be Object[].");
			}
			
		}
		else if (field.isAnnotationPresent(Binding.class))
		{
			parent.bindings.add(field);
		}
		else
		{
			//No relevant annotation found, so add the setter method info to the parent for inflation
			Map<List<Class<?>>, List<Object>> setter = new HashMap<List<Class<?>>, List<Object>>();
			Class<?> type = field.getType();
			List<Class<?>> classes = new ArrayList<Class<?>>();
			classes.add(type);
			Object value = field.get(null);
			List<Object> objects = new ArrayList<Object>();
			objects.add(value);
			setter.put(classes, objects);
			String method = AbLEUtil.buildString("set", AnnotatedLayoutInflater.capitalize(field.getName()));
			parent.setters.put(method, setter);
		}
	}
	
}
