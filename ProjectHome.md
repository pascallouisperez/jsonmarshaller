JsonMarshaller is a Java 1.5 library that allows marshalling and unmarshalling of JSON objects to and from entities ("Java classes").

### Release of 1.0 ###
After almost three years of feedback (thanks!), the 1.0 release is almost ready. We are hoping to complete it for Q1 or Q2 of 2009. The remaining enhancements are ~~[(r2) dependency on ASM](http://code.google.com/p/jsonmarshaller/issues/detail?id=2)~~, ~~[(r7) registering types](http://code.google.com/p/jsonmarshaller/issues/detail?id=7)~~, [(r20) strategies](http://code.google.com/p/jsonmarshaller/issues/detail?id=20) and critical bugs ~~[r11](http://code.google.com/p/jsonmarshaller/issues/detail?id=11)~~, ~~[r15](http://code.google.com/p/jsonmarshaller/issues/detail?id=15)~~.

### Goals ###
  * simplicity - Using this library should be obvious and require the smallest knowledge about the internals of the marshalling process. It should integrate in any environment perfectly.
  * efficiency - The marshalling and unmarshalling process must be blazingly fast. All the work must be done at creation.
  * test driven development - Correctness of this software is crucial due to its low level nature. In this setting, thourough testing ensures controlled evolution.

### Non-Goals ###
  * xml - No, this library will not be extented to produce XML.

### Contributing ###
We're looking for help to continue the development of this library. It could be as simple as sharing unit tests you have with us to ensure our library is 100% correct (and stays that way!) or as involved as taking the lead of refactorings and improvements. Currently, the priorities are

  * stabilize and refine the APIs for 1.0
  * improve the efficiency of the library
  * ~~remove the dependency on org.json which is crappy~~ - done ([r48](http://code.google.com/p/jsonmarshaller/source/detail?r=48))
  * ~~extend the marshalling API to take `Reader` and `Writer` to stream the input and output~~ - done ([r27](http://code.google.com/p/jsonmarshaller/source/detail?r=27), integrated at [r43](http://code.google.com/p/jsonmarshaller/source/detail?r=43))