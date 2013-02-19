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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import android.view.View;

/**
 * Layout Inflater to be used with the Annotated Layout Framework.
 * @author Phil Brown
 *
 */
public class AnnotatedLayoutInflater 
{
	/** 
	 * This context is passed to the inflater, and in turn is passed to all children inflaters.
	 * It is publicly available during layout, after which it is set to null. This allows layout
	 * classes access to the activity during inflation, which can be used in a static block. 
	 */
	public static AbLEActivity context;
	/** This provides non-static access to the class name */
	private static AnnotatedLayoutInflater self;
	
	/** 
	 * Constructor. {@link #self} is created using this constructor, but classes should only use the static 
	 * methods that this class provides.
	 */
	private AnnotatedLayoutInflater()
	{
		//cannot instantiate this class
	}

	/**
	 * This method is called to inflate a View from an annotated class in a layout file. It maintains
	 * a record of parent classes so children classes can access variables and other information from
	 * super classes in the layout. This method simply delegates the layout inflation to the class
	 * with the name of the annotation, followed by "Inflater". The inflater methods generally re-call
	 * this method to inflate children views, creating a recursive layout process.
	 * @param _context used to create views or access resources
	 * @param layout the class that is declared in the layout file
	 * @param parent the previous Inflater that created a view. Use <em>null</em> to denote the
	 * root of the hierarchy (generally the <em>Activity</em>), or a break in the hierarchy.
	 * @return
	 */
	public static View inflate(AbLEActivity _context, Class<?> layout, ClassAnnotationInflater parent)
	{
		try
		{
			context = _context;
			if (self == null)
				self = new AnnotatedLayoutInflater();
			Annotation[] annotations = layout.getAnnotations();
			Annotation root = null;
			for (Annotation a : annotations)
			{
				//NPE.info("Annotation Found: %s", a.annotationType().getName());
				if (a.annotationType().getPackage() == self.getClass().getPackage())
				{
					root = a;
					break;
				}
			}
			if (root == null)
				throw new MissingAnnotationException("Could not find a valid annotation for this class");
			
			//root is an annotation from this package. Use reflection to get the name of the class
			//that handles its inflation.
			String clazz = AbLEUtil.buildString(root.annotationType().getName(), "Inflater");
			//NPE.info("Looking for class: %s", clazz);
			Class<?> rootInflater = Class.forName(clazz);//, true, ClassLoader.getSystemClassLoader());
			Constructor<?> constructor = rootInflater.getConstructor(new Class<?>[]{AbLEActivity.class, ClassAnnotationInflater.class});
			ClassAnnotationInflater inflater = (ClassAnnotationInflater) constructor.newInstance(context, parent);
			
			//TODO create tree hierarchy of ClassAnnotationInflaters so that variables and bindings
			//in the proper scope can be handed to all created Abstract objects. We are receiving
			//a hierarchy as it is - we just need to make something happen, and keeping the scope
			//would be a huge plus.
			
			//inflate, then return, the view
			View v = inflater.inflate(layout);
			
			
			//TODO set binding variables and allow access to them from Activity to handle on...methods
			
			context = null;
			return v;
		} catch (Throwable t)
		{
			t.printStackTrace();
			context = null;
			return null;
		}
		
	}
	
	/** 
	 * Capitalizes the first letter of the given string.
	 * @param string the string whose first letter should be capitalized
	 * @return the given string with its first letter capitalized
	 * @throws NullPointerException if the string is null or empty
	 */
	public static String capitalize(String string)
	{
		if (string == null || string.isEmpty())
			throw new NullPointerException("Cannot handle null or empty string");
		
		StringBuilder strBuilder = new StringBuilder(string);
		strBuilder.setCharAt(0, Character.toUpperCase(strBuilder.charAt(0)));
		return strBuilder.toString();
	}
	
}
