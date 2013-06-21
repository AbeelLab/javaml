The Java Machine Learning Library readme documentation. 

This document covers the very basic documentation of the library. 

The Java Machine Learning Library is licensed under GNU-GPL.
-- Thomas, May 18, 2013: Work is under way to split the GPL code from the core which 
-- I will relicense under a less restrictive license. 

More elaborate documentation can be found on the web site
http://java-ml.sourceforge.net/

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
at http://java-ml.sourceforge.net

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
You can contact us by using the Sourceforge contact page:
http://sourceforge.net/users/thomasabeel/
or send an email to me
thomas@abeel.be


