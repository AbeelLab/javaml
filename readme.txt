The Java-ML readme documentation. This document covers the basic documentation 
of the library. The Java-ML library is licensed under GNU-GPL.

More elaborate documentation can be found on the documentation website for 
the project:
http://java-ml.sourceforge.net/

+-----------------+
| 0. Releases     |
| 1. Algorithms   |
| 2. Dependencies |
| 3. Contact      |
+-----------------+
0. Releases
===========
We do a weekly release each Wednesday. This document contains the last 
information regarding the status of all algorithms in the release.

version ~ date ~ revision
0.0.1 ~ 2007-03-21 ~ 323

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
net.sf.classification.svm.SMOKeerthi
net.sf.classification.svm.SMOPlatt
net.sf.classification.evaluation.CrossValidation

1.2. Working implementation (work on small test)
------------------------------------------------
net.sf.clustering.mcl.MCL
net.sf.clustering.AdaptiveQualityBasedClustering
net.sf.clustering.Ant
net.sf.clustering.DensityBasedSpatialClustering
net.sf.clustering.IterativeKMeans
net.sf.clustering.IterativeMultiKMeans
net.sf.clustering.MultiKMeans
net.sf.clustering.OPTICS
net.sf.clustering.SimpleKMeans
net.sf.clustering.XMeans

1.3 Mature algorithms (thoroughly tested) 
-----------------------------------------
none yet

2. Dependencies
===============
The library requires the commons-math library of the Jakarta project which is 
bundled with the library.

3. Contact
==========
You can contact me by using the Sourceforge contact page:
http://sourceforge.net/users/thomasabeel/
or send an email to the mailinglist:
java-ml-development@lists.sourceforge.net

