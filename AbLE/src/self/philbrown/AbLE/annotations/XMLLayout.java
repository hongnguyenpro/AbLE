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
 * Defines a layout that is inflated from XML
 * @author Phil Brown
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XMLLayout 
{

	/**
	 * Set this to the name of the layout in res/layout. For example for res/layout/main.xml, use "main".
	 * @return
	 */
	public String resourceID();
	
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
	 * Set the orientation. This is only used for top-level layouts.
	 * <br>To match Android defaults, all orientations are enabled by default 
	 * @return
	 */
	public ORIENTATION[] orientation() default {ORIENTATION.portrait, 
		 										ORIENTATION.portraitUpsideDown,
											    ORIENTATION.landscapeLeft,
											    ORIENTATION.landscapeRight};

}
