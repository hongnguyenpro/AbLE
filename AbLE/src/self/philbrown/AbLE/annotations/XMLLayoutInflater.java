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

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;

/**
 * Creates a View from an Android Layout XML file.
 * @author Phil Brown
 *
 */
public class XMLLayoutInflater extends ClassAnnotationInflater {

	/**
	 * Constructor
	 * @param context used to create views and access resources. This is automatically saved
	 * to the {@link ClassAnnotationInflater#context context} variable in the super class.
	 * @param parent the parent {@code ClassAnnotationInflater} in this view hierarchy
	 */
	public XMLLayoutInflater(AbLEActivity context, ClassAnnotationInflater parent) {
		super(context, parent);
	}

	/**
	 * Creates the view from XML, then handles any fields or child views.
	 */
	@Override
	public View inflate(Class<?> layout) {
		XMLLayout xml = layout.getAnnotation(XMLLayout.class);
		String file = xml.resourceID();
		if (file.contains("R.layout"))
		{
			String[] array = file.split(".");
			file = array[array.length-1];
		}
		android.view.LayoutInflater inflater = (android.view.LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(context.getResources().getIdentifier(file, "layout", context.getPackageName()), null);
		
		createView(xml);
		if (view == null)
		{
			AbLEUtil.err("Could not create layout for resource R.layout.%s", file);
			return null;
		}
		
		handleFields(layout);
		handleGetters(view);
		handleBindings();
		performSetters(view);
		handleChildViews(layout);
		onLayoutComplete(layout, view);
		
		return view;
	}
	
	/**
	 * Creates the view based on the attributes in the given annotation
	 * @param xml an {@link XMLLayout} annotation
	 */
	private void createView(XMLLayout xml)
	{

		if (parent == null)
		{
			//if this is the top-level layout, then force the layout orientation
			ORIENTATION[] allowedOrientations = xml.orientation();
			boolean portrait = false, 
					portraitUpsideDown = false, 
					landscapeRight = false, 
					landscapeLeft = false;
			for (ORIENTATION o : allowedOrientations)
			{
				if (o == ORIENTATION.portrait)
					portrait = true;
				else if (o == ORIENTATION.portraitUpsideDown)
					portraitUpsideDown = true;
				else if (o == ORIENTATION.landscapeRight)
					landscapeRight = true;
				else if (o == ORIENTATION.landscapeLeft)
					landscapeLeft = true;
			}
			if(portrait && portraitUpsideDown && landscapeRight && landscapeLeft)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			else if(landscapeRight && landscapeLeft)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			else if(portrait && portraitUpsideDown)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			else if(portrait)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			else if(portraitUpsideDown)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			else if(landscapeRight)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else if(landscapeLeft)
				context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			else
			{
				throw new UnsupportedOperationException("Invalid Layout Orientation parameters!");
			}
		}
		
		float[] origin = xml.origin();
		view.setX(origin[0]);
		view.setY(origin[1]);
		
		//Layout Params
		String[] params = xml.params();
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
		
	}

}
