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

package self.philbrown.AbLE;

/**
 * Classes that wish to receive callbacks for activity lifecycle events should implement
 * this interface, then call 
 * {@link self.philbrown.AbLE.AbLEActivity#addListener(ActivityListener)
 * AbLEActivity.addListener(ActivityListener)} to add itself
 * as a callback receiver.
 * @author Phil Brown
 *
 */
public interface ActivityListener 
{
	/** This is called after the Activity has been created, or after configuration changes. */
	public void onCreate();

	/** 
	 * This is called when the application is started after is was created - such as if the home
	 * button is pressed, and then the app is resumed. 
	 */
	public void onStart();
	
	/**
	 * This is called when the application resumes, either after a call to {@link #onStart()}, or
	 * after this application has regained the foreground if, for example, another app had been 
	 * opened on top.
	 */
	public void onResume();
	
	/**
	 * Called when this app is no longer in the foreground.
	 */
	public void onPause();
	
	/**
	 * Called when this app is no longer running, such as if the home button is pressed, or before
	 * the app is destroyed.
	 */
	public void onStop();
	
	/**
	 * Called when this app is destroyed.
	 */
	public void onDestroy();
	
	/**
	 * Called when the keyboard is shown on screen.
	 */
	public void onKeyboardShown();
	
	/**
	 * Called when the keyboard is hidden from the screen.
	 */
	public void onKeyboardHidden();
	
}
