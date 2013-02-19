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
import self.philbrown.AbLE.annotations.AnnotatedLayoutInflater;
import self.philbrown.AbLE.annotations.Layout;
import self.philbrown.AbLE.annotations.ORIENTATION;
import self.philbrown.AbLE.annotations.Setter;
import self.philbrown.AbLE.annotations.Variable;
import self.philbrown.AbLE.annotations.XMLLayout;
import self.philbrown.AbLETest.AbLETestActivity;
import self.philbrown.AbLETest.R;
import self.philbrown.AbLETest.Controller.AbLETestController;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * This is the main layout for AbLETest. It is specified in the Manifest meta-data, and processed
 * by the AbLEActivity.
 * @author Phil Brown
 *
 */
@XMLLayout(resourceID = "main", orientation={ORIENTATION.landscapeLeft, ORIENTATION.landscapeRight})
public class AbLETestLayout 
{
	@Layout
	public static class InnerView
	{
		//context is not accessible from the top-most layout, but it will be available here.
		@Variable
		public static AbLETestActivity context = (AbLETestActivity) AnnotatedLayoutInflater.context;
		
		//these attributes will be set to null when the app is destroyed, and requires the VM to be
		//killed using context.killProcess() in the onDestroy() method. Do not use this technique for
		//multi-activity applications
		@Variable
		public static AbLETestController controller = context.getController();
		
		@Layout(viewClass="android.widget.Button")
		public static class MyButton
		{
			/** Sets the unique id for this button. */
			public static int id = R.id.button;
			
			/**
			 * Sets the buttons click listener
			 * @see android.widget.Button#setOnClickListener(android.view.View.OnClickListener)
			 */
			public static View.OnClickListener onClickListener = controller;
			
			@Variable
			public static LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			static
			{
				params.bottomMargin = (int) AbLEActivity.convertDipToPixels(15f);
				params.topMargin = (int) AbLEActivity.convertDipToPixels(15f);
				params.leftMargin = (int) AbLEActivity.convertDipToPixels(15f);
				params.rightMargin = (int) AbLEActivity.convertDipToPixels(15f);
			}
			
			/**
			 * Sets the layout params to those specified above
			 * @see android.widget.Button#setLayoutParams(android.view.ViewGroup.LayoutParams)
			 */
			public static ViewGroup.LayoutParams layoutParams = params;
			
			@Variable
			public static int px = (int) AbLEActivity.convertDipToPixels(15f);
			
			@Setter
			public static Object padding = new Object[]{px, px, px, px};
			
		}
	}
	
}
