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

package self.philbrown.AbLE.view;

import java.lang.ref.WeakReference;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import self.philbrown.AbLE.R;
import self.philbrown.AbLE.annotations.AnnotatedLayoutInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * This class provides a way for AbLE layouts to be enclosed within an XML file.
 * @author Phil Brown
 */
public class AbLE_Annotation extends FrameLayout
{
	/** The {@link View} that is created, and where child views are added */
	private View AbLE_View;

	/**
	 * Constructor
	 * @param context
	 */
	public AbLE_Annotation(Context context) {
		this(context, null);
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 */
	public AbLE_Annotation(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	/**
	 * Constructor. This is also where the attributes are unpacked, and the AbLE layout is added as a
	 * child view.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AbLE_Annotation(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AbLE);
		String clazz = a.getString(R.styleable.AbLE_AbLEclass);
		
		Drawable preview = a.getDrawable(R.styleable.AbLE_AbLEpreview);
		
		a.recycle();
		
		if (clazz != null)
		{
			if (this.isInEditMode())
			{
				//handle what to show in the layout editor.
				if (preview == null)
				{
					//default to a White background
					this.setBackgroundColor(Color.WHITE);
				}
				else
				{
					//set the preview to what the user specified
					this.setBackgroundDrawable(preview);
				}
			}
			else
			{
				try {
					Class<?> _class = Class.forName(clazz);
					if (context instanceof AbLEActivity)
					{
						AbLE_View = AnnotatedLayoutInflater.inflate((AbLEActivity) context, _class, null);
					}
					else
					{
						WeakReference<AbLEActivity> ref = AbLEActivity.obtain();
						AbLE_View = AnnotatedLayoutInflater.inflate(ref.get(), _class, null);
						ref = null;
					}
					ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
					AbLE_View.setLayoutParams(params);
					super.addView(AbLE_View);
				} catch (Throwable t) {
					AbLEUtil.err("Could not instantiate class %s.", clazz);
				}
			}
			
		}
		else
		{
			AbLEUtil.err("AbLE class must be specified!");
		}
	}

}
