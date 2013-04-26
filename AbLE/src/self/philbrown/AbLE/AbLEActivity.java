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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import self.philbrown.AbLE.annotations.AnnotatedLayoutInflater;
import self.philbrown.AbLE.view.AbLE_Annotation;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

/**
 * This activity uses AbLE to create layouts from annotated java code.
 * @author Phil Brown
 *
 */
public class AbLEActivity extends Activity {

	/** 
	 * A singleton of this class is kept at the Activity level. For performance reasons, this instance
	 * can only be obtained through weak references using the {@link #obtain()} method. 
	 */
	private static AbLEActivity self;
	
	/**
	 * {@code true} if the user has specified a custom content size. This is accessed by screen-size
	 * related methods that are used for screen sizing and scaling
	 */
	private static boolean customContentSize = false;
	/**
	 * The width specified by the user, in pixels
	 * @see #customContentSize
	 */
	private static int customContentWidth = 0;
	/** 
	 * The height specified by the user, in pixels
	 * @see #customContentSize
	 */
	private static int customContentHeight = 0;
	
	private int backgroundResource = 0;
	private int backgroundColor = Color.BLACK;
	
	/**
	 * Keeps track of whether or not application-level meta data has already been handled.
	 */
	private static boolean applicationMetaDataUsed = false;
	
	/** The view in which all content is displayed */
	protected static View contentView;
	
	/** Keeps track of the current visible state of the keyboard. True if it is visible. Otherwise false. */
	private boolean isKeyboardVisible = false;
	
	/** Estimated keyboard height */
	private int keyboardHeight = 0;
	
	/** 
	 * Maintains a list of listeners that should receive callbacks for lifecycle events.
	 * This is static to allow it to remain unaltered across mulitple activities.
	 */
	protected static volatile List<ActivityListener> listeners = new ArrayList<ActivityListener>();
	
	/**
	 * Overrides the default constructor
	 */
	public AbLEActivity()
	{
		super();
		self = this;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		try {
			if (!applicationMetaDataUsed)
			{
				ApplicationInfo app = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
				Bundle metaData = app.metaData;
				if (metaData != null)
				{
					applicationMetaDataUsed = true;
					//meta-data for screen size can be added in the form WxH, where x must be lower case,
		        	//and W and H refer to the width and height in pixels, respectively
		        	String contentSize = metaData.getString("contentSize");
		        	if (contentSize != null)
		        	{
		        		String[] split = contentSize.split("x");
		        		customContentWidth = Integer.parseInt(split[0]);
		        		customContentHeight = Integer.parseInt(split[1]);
		        		
		        		customContentSize = true;
		        		
		        	}
		        	
		        	int resourceID = metaData.getInt("background", -1);
		        	if (resourceID != -1)
		        	{
		        		backgroundResource = resourceID;
		        	}
		        	else
		        	{
		        		String background = metaData.getString("background");
		        		if (background != null)
		        		{
		        			try
		        			{
		        				backgroundColor = Color.parseColor(background);
		        				AbLEUtil.info("Setting color to %s", background);
		        			}
		        			catch (IllegalArgumentException e)
		        			{
		        				AbLEUtil.err("Invalid color %s!", background);
		        			}
		        		}
		        		
		        	}
				}
			}
			
			ActivityInfo app = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_ACTIVITIES|PackageManager.GET_META_DATA);
			Bundle metaData = app.metaData; 
	        if (metaData != null)
	        {
	        	String layoutFile = metaData.getString("layout");
	        	AbLEUtil.info("Inflating View at file %s", layoutFile);
	        	Class<?> layout = Class.forName(layoutFile);
	        	contentView = AnnotatedLayoutInflater.inflate(this, layout, null);
	        	contentView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
	    		{
	    			
	    			public void onGlobalLayout() {
	    			    Rect r = new Rect();
	    			    //r will be populated with the coordinates of your view that area still visible.
	    			    contentView.getWindowVisibleDisplayFrame(r);

	    			    int heightDiff = contentView.getRootView().getHeight() - (r.bottom - r.top);
	    			    if (heightDiff > 100) 
	    			    { // if more than 100 pixels, its probably a keyboard...
	    			    	keyboardHeight = heightDiff;
	    			        if (!isKeyboardVisible)
	    			        	setKeyboardVisible(true);
	    			    }
	    			    else
	    			    {
	    			    	if (isKeyboardVisible)
	    			    		setKeyboardVisible(false);
	    			    }
	    			 }
	    		});
	        	
	        	if (customContentSize)
	        	{
	        		RelativeLayout rl = new RelativeLayout(this);
	        		rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	        		
	        		if (backgroundResource > 0)
	        		{
	        			rl.setBackgroundResource(backgroundResource);
	        		}
	        		else//use background color
	        		{
	        			rl.setBackgroundColor(backgroundColor);
	        		}
	        		
	        		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(customContentWidth, customContentHeight);
	        		params.addRule(RelativeLayout.CENTER_IN_PARENT);
	        		contentView.setLayoutParams(params);
	        		
	        		rl.addView(contentView);
	        		setContentView(rl);
	        	}
	        	else
	        	{
	        		setContentView(contentView);
	        	}
	        }
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		
		for (ActivityListener listener : listeners)
		{
			listener.onCreate();
		}
	}
	
	/**
	 * Returns the location of the contentView within the screen. This is different from the screen size
	 * when the developer specifies a custom screen size in the application meta-data
	 * @param out the array in which to place the location x and y coordinates, respectively.
	 */
	public static void getContentLocationOnScreen(int[] out)
	{
		contentView.getLocationOnScreen(out);
	}
	
	/**
	 * @return {@code true} if the developer has specified a custom screen size in the application 
	 * meta-data
	 */
	public static boolean isScreenSizeCustomized()
	{
		return customContentSize;
	}
	
	/**
	 * @return the developer-customized screen width, or 0 if not specified.
	 */
	public static int getCustomWidth()
	{
		return customContentWidth;
	}
	
	/**
	 * @return the developer-customized screen height, or 0 if not specified.
	 */
	public static int getCustomHeight()
	{
		return customContentHeight;
	}
	
	/**
	 * Sets the visible state of the keyboard and notifies any listeners of the change.
	 * @param state new visible state of the keyboard
	 */
	private void setKeyboardVisible(boolean state)
	{
		isKeyboardVisible = state;
		if (state)
		{
			onKeyboardShown();
		}
		else
		{
			onKeyboardHidden();
		}
	}
	
	public boolean isKeyboardVisible()
	{
		return isKeyboardVisible;
	}
	
	/** Hides the keyboard if it is open */
	public void hideKeyboard()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isKeyboardVisible)
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	/** Shows the keyboard if it is closed */
	public void showKeyboard()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (!isKeyboardVisible)
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	/**
	 * 
	 * @return the estimated height of the keyboard, or 0 if unknown.
	 */
	public int getKeyboardHeight()
	{
		return keyboardHeight;
	}
	
	/**
	 * Recursively iterates through all views and view children in the layout to look for 
	 * a view with the given id.
	 * @param id the id of the view in question
	 * @param v the view whose id to check, or whose children id's to check
	 * @return the view in the layout with the given id, or null if it was not found.
	 */
	public View recursivelyFindViewById(int id, View v)
	{
		if (v.getId() == id)
		{
			return v;
		}
		else
		{
			if (v instanceof AbLE_Annotation)
			{
				for (int i = 0; i < ((AbLE_Annotation) v).getAbLEChildCount(); i++)
				{
					View _v = recursivelyFindViewById(id, ((AbLE_Annotation) v).getAbLEChildAt(i));
					if (_v != null)
						return _v;
				}
			}
			else if (v instanceof ViewGroup)
			{
				for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++)
				{
					View _v = recursivelyFindViewById(id, ((ViewGroup) v).getChildAt(i));
					if (_v != null)
						return _v;
				}
			}
		}
		return null;
	}
	
	/**
	 * This is a replacement for {@link Activity#findViewById(int)}, that handles the id query
	 * based on the annotations-based layout scheme.
	 * @param id the view id for which to query
	 * @return the view with the given id, or null if no view with that id is found in the layout.
	 */
	@Override
	public View findViewById(int id)
	{
		return recursivelyFindViewById(id, contentView);
	}
	
	/**
	 * Adds a listener to {@link #listeners}. 
	 * This does not check for multiple instances of listeners.
	 * @param listener
	 */
	public static synchronized void addListener(ActivityListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Removes the listener. Good idea to call this before a listener is nullified.
	 * @param listener
	 */
	public static synchronized void removeListener(ActivityListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Checks to see if the given listener is already registered to receive callbacks
	 * @param listener the listener to check
	 * @return true if {@code listener} is registered. Otherwise, false.
	 */
	public static boolean isListenerRegistered(ActivityListener listener)
	{
		if (listeners.contains(listener))
			return true;
		return false;
	}
	
	/**
	 * Called when the keyboard is shown on screen.
	 */
	public void onKeyboardShown()
	{
		for (ActivityListener listener : listeners)
		{
			listener.onKeyboardShown();
		}
	}
	
	/**
	 * Called when the keyboard is hidden from the screen.
	 */
	public void onKeyboardHidden()
	{
		for (ActivityListener listener : listeners)
		{
			listener.onKeyboardShown();
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();	
		for (ActivityListener listener : listeners)
		{
			listener.onStart();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();	
		for (ActivityListener listener : listeners)
		{
			listener.onResume();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();	
		for (ActivityListener listener : listeners)
		{
			listener.onPause();
		}
	}
	
	@Override
	public void onStop()
	{
		super.onStop();	
		for (ActivityListener listener : listeners)
		{
			listener.onStop();
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();	
		for (ActivityListener listener : listeners)
		{
			listener.onDestroy();
		}
		//this ensures the static class layout files are properly unloaded
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * Dims the status bar.
	 */
	public void hideStatusbar()
	{
		contentView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
		contentView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}
	
	/**
	 * Kill the application. This can be used for cases where Objects in static layout files reference
	 * variables that will be nullified when the application closes - but are not reset by the VM.
	 */
	public void killProcess()
	{
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * This method convets dp unit to equivalent device specific value in pixels. 
	 * 
	 * @param dp A value in dp(Device independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent Pixels equivalent to dp according to device
	 */
	public float convertDpToPixel(float dp)
	{
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    float px = dp * (metrics.density/160f);
	    return px;
	}
	/**
	 * This method converts raw pixel value to device specific pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public float convertPixelsToDp(float px){
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    float dp = px / (metrics.density / 160f);
	    return dp;
	}
	
	/**
	 * Converts device raw pixels to device independent pixels
	 * @param dip
	 * @return
	 */
	public static float convertDipToPixels(float dip)
	{
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, self.getResources().getDisplayMetrics());
	}
	
	/**
	 * @return a weak reference to this Activity
	 */
	public static WeakReference<AbLEActivity> obtain()
	{
		return new WeakReference<AbLEActivity>(self);
	}
}
