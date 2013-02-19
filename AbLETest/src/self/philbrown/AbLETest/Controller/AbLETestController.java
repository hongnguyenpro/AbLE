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

package self.philbrown.AbLETest.Controller;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLE.ActivityListener;
import self.philbrown.AbLETest.R;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * AbLE Test Controller
 * @author Phil Brown
 *
 */
public class AbLETestController implements ActivityListener, View.OnClickListener
{

	private AbLEActivity context;
	
	private Button button;
	
	public AbLETestController(AbLEActivity context)
	{
		this.context = context;
		AbLEActivity.addListener(this);
	}
	
	public void takeControl(View contentView)
	{
		button = (Button) context.findViewById(R.id.button);
		//you can also use context.recursivelyFindViewById(int, View) to find views in uninflated viewgroups.
		button.setText("Show Toast");
		
		
	}
	
	/**
	 * Notified by the Activity when the device has been rotated, passing in the new orientation
	 * @param orientation the new orientation. May be one of
	 * {@link android.content.res.Configuration#ORIENTATION_LANDSCAPE ORIENTATION_LANDSCAPE}
	 * {@link android.content.res.Configuration#ORIENTATION_PORTRAIT ORIENTATION_PORTRAIT}
	 * {@link android.content.res.Configuration#ORIENTATION_SQUARE ORIENTATION_SQUARE}.
	 */
	public void onOrientationChanged(int orientation)
	{
		//force landscape mode!
		switch(orientation)
		{
		case android.content.res.Configuration.ORIENTATION_LANDSCAPE :
			break;
		case android.content.res.Configuration.ORIENTATION_PORTRAIT :
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			break;
		case android.content.res.Configuration.ORIENTATION_SQUARE :
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			break;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		context.hideStatusbar();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		//If you are accessing the controller statically in a layout file, you will need to destroy
		//the app process here. This is generally bad practice, but it can work very well in a single-
		//Activity application (moves a lot of code to the layout file that would be in this controller
		//file).
		context.killProcess();
	}

	@Override
	public void onKeyboardShown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyboardHidden() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if (v == button)
		{
			Toast.makeText(context, "AbLE Toast!", Toast.LENGTH_SHORT).show();
		}
	}
}
