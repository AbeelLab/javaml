# javaml
Java Machine Learning Library

This document covers the very basic documentation of the library. 

The Java Machine Learning Library is licensed under GNU-GPL.

updates: 
-- 2016/03/03 moved to github.com 
-- Still working to split GPL code from the core which will be less restrictive, I appreciate pull requests

More elaborate documentation can be found in the old_documentation.zip package.

1. Overview
=============
Java-ML in a nutshell:

    * A collection of machine learning algorithms
    * Common interface for each type of algorithms
    * Library aimed at software engineers and programmers, so no GUI, but clear interfaces
    * Reference implementations for algorithms described in the scientific literature.
    * Well documented source code.
    * Plenty of code samples and tutorials.

2. How to get started
=====================
When you are reading this, you most probably already downloaded the library. 
To use it, include the javaml-<version>.jar in your classpath, as well as the 
jars that are available in lib/.  

How to get started, code samples, tutorials on various tasks can be found
in the old_documentation.zip package.


3. Requirements
===============
Java 6

4. Dependencies
===============
Required libraries:
- Apache Commons Math: used in some algorithms, version 1.2 is included
	Apache Commons Math is distributed under Apache License 2.0
	http://commons.apache.org/math/

- Abeel Java Toolkit: used in some classes, version 2.9 is included
	AJT is distributed under GNU LGPL 2 or later
	http://sourceforge.net/projects/ajt/
	
- Jama: used in some algorithms, version 1.0.2 is included
	Jama is distributed as public domain software 
	http://math.nist.gov/javanumerics/jama/
	
Optional libraries:
- Weka: if you like to use algorithms from Weka. Weka 3.6.0 is included in the distribution
	Weka is distributed under GNU GPL 2 or later
	http://www.cs.waikato.ac.nz/ml/weka/

- libsvm: if you like to use the libsvm algoriths. libSVM 2.89 is included in this distribution
	libSVM is distributed under the modified BSD license
	http://www.csie.ntu.edu.tw/~cjlin/libsvm/
	
- JUnit: if you want to run the unit tests. As the unit tests are only available from the SVN 
we did not include a version of JUnit with the distribution. The tests have been written for the 
JUnit 4 platform and may not function for JUnit 3 or earlier

5. Contact
==========
You can contact us by e-mail.
thomas@abeel.be



