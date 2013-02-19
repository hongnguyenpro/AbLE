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

package self.philbrown.AbLETest;

import self.philbrown.AbLE.AbLEActivity;
import self.philbrown.AbLETest.Controller.AbLETestController;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Test Activity
 * @author Phil Brown
 *
 */
public class AbLETestActivity extends AbLEActivity
{
	/**
	 * The controller is a way to delegate the bulk of the work away from the Android activity. This
	 * is useful in several ways. The first way is that it closely follows the MVC architecture, which
	 * allows for much simpler-structured applications. Secondly, when you publish your application,
	 * you cannot obfuscate an Activity using ProGuard. You can, however, obfuscate the controller code,
	 * where the bulk of the work will take place.
	 */
	private AbLETestController controller;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//by setting the controller first, a developer can optionally access the controller
		//using the getController() method, from within the layout file. But use this cautiously,
		//as the VM is kept active after the app has stopped - and these static variables are set to
		//null even for the next time the app is launched. This technique can be really handy for
		//single-Activity applications, and context.killProcess() should be called in the onDestroy if
		//you choose to go this route.
		controller = new AbLETestController(this);
		super.onCreate(savedInstanceState);
		controller.takeControl(contentView);
	}
	
	@Override
	public void onConfigurationChanged (Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		controller.onOrientationChanged(newConfig.orientation);
	}
	
	/**
	 * Returns the controller used for this layout. This can be used to make calls directly from
	 * the layout file.
	 * @return
	 */
	public AbLETestController getController()
	{
		return controller;
	}
}
