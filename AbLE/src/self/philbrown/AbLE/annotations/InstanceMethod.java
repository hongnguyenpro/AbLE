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
 * Defines an Object that is retrieved by calling the getter method.<br>
 * For example, the below will call the method {@code public Object getTag()} on the Object
 * represented by the parent layout:<br>
 * <pre>
 * {@code @InstanceMethod}
 * public static Object tag;
 * </pre>
 * 
 * @author Phil Brown
 * @deprecated use {@link Getter}
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InstanceMethod 
{
	/**
	 * Sets the method name. This is optional, as generally the variable name is used to get the method name
	 * by prepending "get", however for methods with other names (such as "findViewById"), this is a nice
	 * attribute to have.
	 * @return the name of the method to call.
	 */
	public String methodName() default "";
}
