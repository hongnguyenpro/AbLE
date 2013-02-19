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

import self.philbrown.AbLE.AbLEActivity;
import android.view.View;

/**
 * Embeds a layout file into the current layout
 * @author Phil Brown
 *
 */
public class EmbedInflater extends ClassAnnotationInflater
{
	/**
	 * Constructor
	 * @param context
	 * @param parent
	 */
	public EmbedInflater(AbLEActivity context, ClassAnnotationInflater parent) {
		super(context, parent);
	}

	@Override
	public View inflate(Class<?> layout) {
		
		Embed embed = layout.getAnnotation(Embed.class);
		createView(embed);
		if (view == null)
			return null;
		handleFields(layout);
		handleInstanceMethods(view);
		handleBindings();
		performSetters(view);
		handleChildViews(layout);
		onLayoutComplete(layout, view);
		
		return view;
	}
	
	/**
	 * Adds the view for layout class {@link Embed#layout()} at the given location in the layout
	 * @param embed
	 */
	private void createView(Embed embed)
	{
		try {
			Class<?> _class = Class.forName(embed.layout());
			view = AnnotatedLayoutInflater.inflate(context, _class, this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
