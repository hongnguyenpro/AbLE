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

/**
 * Superclass required for loading Invisible Objects using the Invisible annotation
 * @author Phil Brown
 *
 */
public abstract class AbstractInvisible extends AbstractAnnotation
{
	/**
	 * Constructor
	 * @param context
	 */
	public AbstractInvisible(AbLEActivity context) {
		super(context);
	}
	
	/**
	 * Create the invisible object
	 */
	public abstract void create();

}
