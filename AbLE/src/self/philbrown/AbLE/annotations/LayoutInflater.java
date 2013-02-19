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

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Inflate a {@link View} from a class that declares a {@link Layout} annotation
 * @author Phil Brown
 */
public class LayoutInflater extends ClassAnnotationInflater {

	/** The view's layout parameters */
	private ViewGroup.LayoutParams layoutParams;
	/** The view's x and y coordinates in the parent view, respectively. */
	private PointF origin;
	/** The name of the class of which the inflated view is a child of. */
	private String viewClass;
	
	/**
	 * Constructor
	 * @param context used to create views and access resources. This is automatically saved
	 * to the {@link ClassAnnotationInflater#context context} variable in the super class.
	 * @param parent the parent {@code ClassAnnotationInflater} in this view hierarchy
	 */
	public LayoutInflater(AbLEActivity context, ClassAnnotationInflater parent) {
		super(context, parent);
	}

	@Override
	public View inflate(Class<?> layout) {
		Layout annotation = layout.getAnnotation(Layout.class);
		
		//Unpack Layout attributes
		createView(annotation);
		if (view == null)
			return null;
		
		handleFields(layout);
		handleInstanceMethods(viewClass);
		handleBindings();
		performSetters(viewClass);
		handleChildViews(layout);
		onLayoutComplete(layout, view);
		
		return view;
	}
	
	/**
	 * Creates the view based on the attributes in the given annotation
	 * @param layout
	 */
	private void createView(Layout layout)
	{
		try
		{
			viewClass = layout.viewClass();
			Class<?> _class = Class.forName(viewClass);
			Constructor<?> _constructor = _class.getConstructor(new Class<?>[]{Context.class});
			view = (View) _constructor.newInstance(context);
			
			if (parent == null)
			{
				//if this is the top-level layout, then force the layout orientation
				ORIENTATION[] allowedOrientations = layout.orientation();
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
			
		} catch (Throwable t)
		{
			AbLEUtil.err("Class %s could not be created! Defaulting to FrameLayout.", viewClass);
			view = new FrameLayout(context);
		}
		
		float[] origin = layout.origin();
		this.origin = new PointF((origin[0]), (origin[1]));
		view.setX(this.origin.x);
		view.setY(this.origin.y);
		
		//Layout Params
		String[] params = layout.params();
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
		
		layoutParams = new ViewGroup.LayoutParams(width, height);
		
		view.setLayoutParams(layoutParams);
		
		//view padding
		int[] padding = layout.padding();
		int left, top, right, bottom;
		if (padding.length == 1)
		{
			left = padding[0];
			top = padding[0];
			right = padding[0];
			bottom = padding[0];
		}
		else if (padding.length == 4)
		{
			left = padding[0];
			top = padding[1];
			right = padding[2];
			bottom = padding[3];
		}
		else
		{
			AbLEUtil.warn("Layout padding must contain either 1 or 4 integer values. Current count: %d.", padding.length);
			left = 0;
			top = 0;
			right = 0;
			bottom = 0;
		}
		view.setPadding(left, top, right, bottom);
	}
	
	
}
