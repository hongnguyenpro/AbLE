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

/**
 * This package lays out an Android Layout System based on Java Annotations (dubbed Annotation-based
 * Layout Engine, or AbLE for short)
 * <p>
 * The basics are as follows:<br>
 * There are 10 annotations that are used: {@link Layout}, {@link LayoutAdapter}, {@link XMLLayout},
 * {@link Invisible}, {@link Binding}, {@link Variable}, {@link Embed}, 
 * {@link Setter} and {@link Getter}.
 * <p>
 * Layout is used to create existing Android View classes in the layout. The LayoutAdapter
 * works be providing a callback to get a view from a custom class. Controller acts as the view
 * controller in the MVC architecture. Invisible defines something that is not a view. Variable
 * declares a variable that other views can bind to, which is the purpose of Binding.
 * <p>
 * The benefits to using a code-based layout system include the ability to declare types and parameters
 * programmatically, and use complex types (any Object).
 * <p>
 * There are downsides as well. For example, there is no validation of attributes. It is also not
 * directly cross-platform compatible, but it requires less set up than an xml-based architecture.
 * <p>
 * The layout inflater recursively creates all 
 * the layouts and set their attributes using reflection. Other aspects that improve speed of coding
 * include the ability to define the layout file in activity meta-data.
 * <p>
 * Currently available meta-data tags are as follows:<br>
 * At the Application level:<br>
 * <ul>
 * 	<li>contentSize: Specifies a down-scaled pixel width and height in the format WxH. x MUST be lower-case,
 * and W and H MUST be integer values.
 * 	<li>background: Specifies the background image or color that should be set behind the shrunken screen.
 * This is only used if {@code contentSize} is set. To use a background resource, set the
 * {@code android:resource} attribute. Otherwise, if a String is used, use a color, such as "red" or "blue".
 * A hex color should be preceded with an escape character ("\"). See <a href="http://stackoverflow.com/questions/14692335/specify-hex-color-value-in-android-metadata">Stack Overflow</a>
 * </ul><br>
 * At the Activity level:<br>
 * <ul>
 * 	<li>layout: Specifies the class that contains the layout file to inflate for the Activity.
 * </ul>
 * <p><p>
 */