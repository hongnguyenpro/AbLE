Annotation-based Layout Engine (AbLE) is a layout engine for the Android platform that allows 
developers to write layouts using Java code and XML interchangeably.

It also provides easy callbacks for modifying a view directly after it has been added to the layout,
and provides many utilities to manipulate them to fit various screen sizes and resolutions.

A test project is included, and further discussion can be found below the license.

Check out the Github Page at http://phil-brown.github.io/AbLE/.

## LICENSE

Copyright 2013 Phil Brown

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
The license text is printed below, and you may obtain a copy of 
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License (online or below) for the specific language governing permissions and
limitations under the License.

----------------------------------

For the redistribution section of the license, there is a Java annotation
available on GitHub (and already within AbLE) that is meant for annotating 
changes to source code.

Feel free to use this for annotating changes to AbLE. You can find this 
code at https://github.com/phil-brown/Modified or in the model package.

----------------------------------

## The basics

There are 9 annotations that are currently used: *Layout*, *LayoutAdapter*, *XMLLayout*,
*Invisible*, *Binding*, *Variable*, *Embed*, *Setter* and *Getter*.

Layout is used to create existing Android View classes in the layout. The LayoutAdapter
works be providing a callback to get a view from a custom class. Invisible defines something 
that is not a view. Variable declares an Object that is ignored by the layout system, but that
non-variables can use. Variables can be handy to things that need to be modified before assigned.
These modifications can happen in a static block. Binding is generally unused, but can be used to
assign an Object to a variable, if it exists. Embed allows a layout class to be a child view. Setter
can be used to set values that either have multiple parameters or none. Getter can be used to get an
Object using any number of parameters during class layout.

There is also a View Object called *AbLE_Annotation*. This can be set in XML and, using custom XML parameters,
can point to an annotated layout file to be handled by AbLE.

The benefits of using a code-based layout system include the ability to declare types and parameters
programmatically, and use complex types (any Object).

There are downsides as well. For example, there is no validation of attributes. It is also not
directly cross-platform compatible, but it requires less set up than an xml-based architecture.

The layout inflater recursively creates all 
the layouts and set their attributes using reflection. Other features that improve speed of coding
include the ability to define the layout file in activity meta-data.

Currently available meta-data tags are as follows:
At the Application level:
* contentSize: Specifies a down-scaled pixel width and height in the format WxH. x MUST be lower-case,
and W and H MUST be integer values.
* background: Specifies the background image or color that should be set behind the shrunken screen.
This is only used if `contentSize` is set. To use a background resource, set the
`android:resource` attribute. Otherwise, if a String is used, use a color, such as "red" or "blue".
A hex color should be preceded with an escape character ("\"). See [Stack Overflow](http://stackoverflow.com/questions/14692335/specify-hex-color-value-in-android-metadata).

At the Activity level:
* layout: Specifies the class that contains the layout file to inflate for the Activity.