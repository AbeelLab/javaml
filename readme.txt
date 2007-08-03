The Java Machine Learning Library readme documentation. This document covers the 
basic documentation of the library. The Java Machine Learning Library is 
licensed under GNU-GPL.

More elaborate documentation can be found on the documentation website for 
the project:
http://java-ml.sourceforge.net/ or http://www.abeel.be/

or in the API documentation http://www.abeel.be/java-ml/api/

+-----------------+
| 0. Releases     |
| 1. Algorithms   |
| 2. Dependencies |
| 3. Requirements |
| 4. Contact      |
+-----------------+

0. Releases
===========
version ~ date ~ revision
0.0.1 ~ 2007-03-21 ~ 323
0.0.2 ~ 2007-03-28 ~ 371
0.0.3 ~ 2007-04-04 ~ 412
0.0.4 ~ 2007-04-11 ~ 457
0.0.5 ~ 2007-04-18 ~ 525
0.0.6 ~ 2007-04-27 ~ 581
0.0.7 ~ 2007-05-02 ~ 601
0.0.8 ~ 2007-05-09 ~ 656
0.0.9 ~ 2007-05-16 ~ 660
0.0.10 ~ 2007-05-30 ~ 681
0.0.11 ~ 2007-07-11 ~ ...

1. Algorithms
=============
There are three levels of implementation in the library: first there are the 
preliminary implementation that do not necesaryly compile or work, second there 
are the algorithms that are compliled and do work in a simple test scenario, 
and third there are the mature algorithms that have been thoroughly tested.

The algorithms here only cover the 'true' machine learning algorithms. Helper 
classes and other functions are not described here.

1.1 First draft implementation. (may or may not work)
-----------------------------------------------------
net.sf.clustering.CAST
net.sf.classification.Logistic

1.2. Working implementation (work on a small test)
--------------------------------------------------
net.sf.classification.evaluation.CrossValidation
net.sf.classification.svm.BinaryLinearSMO
net.sf.classification.svm.BinarySMO


net.sf.clustering.mcl.MCL
net.sf.clustering.AQBC
net.sf.clustering.Ant
net.sf.clustering.Cobweb
net.sf.clustering.DensityBasedSpatialClustering
net.sf.clustering.EMClustering
net.sf.clustering.FarthestFirst
net.sf.clustering.IterativeEMClustering
net.sf.clustering.IterativeFarthestFirst
net.sf.clustering.IterativeKMeans
net.sf.clustering.IterativeMultiKMeans
net.sf.clustering.KMeans
net.sf.clustering.KMedoids
net.sf.clustering.MultiKMeans
net.sf.clustering.OPTICS
net.sf.clustering.SOM
net.sf.clustering.XMeans

1.3 Mature algorithms (thoroughly tested) 
-----------------------------------------
none yet

2. Dependencies
===============
* Jakarta commons-math
----------------------
The library requires the commons-math library of the Jakarta project which is 
bundled with the library.

* JUnit 4.0
-----------
For the SVN download you will also need JUnit 4.0 to compile the unit tests.

* JAMA 1.0.2
------------
Since version 0.0.10 we have incorporated the JAMA version 1.0.2 into the library.



3. Requirements
===============
The library is coded for Java 1.5.0 so you will need a JDK that is at least
version 1.5.0.

4. Contact
==========
You can contact us by using the Sourceforge contact page:
http://sourceforge.net/users/thomasabeel/
or send an email to the mailinglist:
java-ml-development@lists.sourceforge.net

