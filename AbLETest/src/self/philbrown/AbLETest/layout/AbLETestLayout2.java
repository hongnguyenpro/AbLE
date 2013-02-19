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

package self.philbrown.AbLETest.layout;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.AbLEUtil;
import self.philbrown.AbLE.annotations.Layout;
import self.philbrown.AbLETest.R;
import android.graphics.Color;
import android.view.View;

/**
 * This layout file is loaded from XML in main.xml. 
 * The layout preview will only show a blue rectangle for now.
 * @author Phil Brown
 *
 */
@Layout(viewClass="android.widget.TextView")
public class AbLETestLayout2 
{
	/**
	 * The best way to set unique ids for all AbLE elements is to create the IDs in res/values/ids.xml.
	 * Not using this is ok for small applications, but if any ids are used twice, there will be 
	 * bugs that are difficult to trace.
	 */
	public static int id = R.id.text_view;
	
	/**
	 * Sets the text of this TextView.
	 * @see android.widget.TextView#setText(CharSequence)
	 */
	public static CharSequence text = "This is the text!";
	
	/**
	 * Sets the TextView text color to white
	 * @see android.widget.TextView#setTextColor(int)
	 */
	public static int textColor = Color.WHITE;
	
	/**
	 * Sets the view's background color
	 */
	public static int backgroundColor = Color.LTGRAY;
	
	/**
	 * This method is called after the view has inflated
	 * @param context
	 * @param v
	 */
	public static void onLayoutComplete(AbLEActivity context, View v)
	{
		AbLEUtil.info("Inflated custom view");
	}
	
}
