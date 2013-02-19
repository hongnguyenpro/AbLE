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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class documentation for defining a layout programmatically
 * <br>
 * Benefits of doing layout this way:
 * 	Use Code, which is more comfortable and more customizable
 * 	Create Objects that are not views
 * 	Create AdapterViews easily
 * 
 * @author Phil Brown
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Layout 
{

	/**
	 * Set to the x and y coordinates of the inflated {@link android.view.View View}, respectively.
	 * @return
	 */
	public float[] origin() default {0,0};
	
	/**
	 * Layout Parameters. Can be any of "fill_parent", "wrap_content", "match_parent", or a pixel value.
	 * @return
	 * @see {@link android.view.ViewGroup.LayoutParams LayoutParams}
	 */
	public String[] params() default {"wrap_content", "wrap_content"};
	
	/**
	 * Sets the view padding. MUST be either 1 value to specify padding on all sides, 
	 * or 4 values to specify sides individually, in the following order: {Left, Top, Right, Bottom}
	 * @return
	 */
	public int[] padding() default { 0,0,0,0 };
	
	/**
	 * View class that is used. Must be a ViewGroup
	 * @return
	 */
	public String viewClass() default "android.widget.FrameLayout";
	
	/**
	 * Set the orientation. This is only used for top-level layouts.
	 * <br>To match Android defaults, all orientations are enabled by default 
	 * @return
	 */
	public ORIENTATION[] orientation() default {ORIENTATION.portrait, 
		 										ORIENTATION.portraitUpsideDown,
											    ORIENTATION.landscapeLeft,
											    ORIENTATION.landscapeRight};
	
}
