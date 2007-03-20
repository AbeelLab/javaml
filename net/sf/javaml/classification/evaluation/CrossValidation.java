/**
 * CrossValidation.java, 17-jan-2007
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.classification.evaluation;

import java.util.Random;
import java.util.Vector;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

public class CrossValidation {

    private Dataset data;

    private Classifier classifier;

    public CrossValidation(Classifier classifier) {
        this.classifier = classifier;
  }

    //TODO make really random
    private Random rg=new Random(1);
    private int positiveClassValue=-1;
    public PerformanceMeasure crossValidation(Dataset data, int positiveClassValue, int folds) {
        this.data = data;
        this.positiveClassValue=positiveClassValue;
        // split data in positive and negative samples
        Vector<Integer> positiveSamples = new Vector<Integer>();
        Vector<Integer> negativeSamples = new Vector<Integer>();
        for (int i = 0; i < data.size(); i++) {
            Instance tmp = data.getInstance(i);
            if (tmp.isClassSet() && tmp.getClassValue() == positiveClassValue) {
                positiveSamples.add(i);
            } else {
                negativeSamples.add(i);
            }

        }
        if (folds > positiveSamples.size() || folds > negativeSamples.size()) {
            throw new RuntimeException("There are not enough samples to perform " + folds
                    + "-fold cross validation on the dataset");
        }

        Vector<Vector<Integer>>positiveSets=new Vector<Vector<Integer>>();
        Vector<Vector<Integer>>negativeSets=new Vector<Vector<Integer>>();
        for(int i=0;i<folds;i++){
            positiveSets.add(new Vector<Integer>());
            negativeSets.add(new Vector<Integer>());
        }
        int index=0;
        while(positiveSamples.size()>0){
            positiveSets.get(index).add(positiveSamples.remove(rg.nextInt(positiveSamples.size())));
            index++;
            index%=positiveSets.size();
        }
        index=0;
        while(positiveSamples.size()>0){
            positiveSets.get(index).add(positiveSamples.remove(rg.nextInt(positiveSamples.size())));
            index++;
            index%=positiveSets.size();
        }
        index=0;
        while(negativeSamples.size()>0){
            negativeSets.get(index).add(negativeSamples.remove(rg.nextInt(negativeSamples.size())));
            index++;
            index%=negativeSets.size();
        }
        int tp=0,tn=0,fp=0,fn=0;
        for(int i=0;i<folds;i++){
            PerformanceMeasure pm=singleFold(positiveSets,negativeSets,i);
            //System.out.println("Fold "+i+":\t"+pm);
            tp+=pm.truePositives;
            tn+=pm.trueNegatives;
            fp+=pm.falsePositives;
            fn+=pm.falseNegatives;
        }
        
        return new PerformanceMeasure(tp,tn,fp,fn);
    }
    
    private PerformanceMeasure singleFold(Vector<Vector<Integer>> positiveSets, Vector<Vector<Integer>> negativeSets, int fold) {
        Dataset training=new SimpleDataset();
        for(int i=0;i<positiveSets.size();i++){
            if(i!=fold){
                Vector<Integer>tmpPos=positiveSets.get(i);
                for(int j=0;j<tmpPos.size();j++){
                    training.addInstance(data.getInstance(tmpPos.get(j)));
                }
                
                Vector<Integer>tmpNeg=negativeSets.get(i);
                for(int j=0;j<tmpNeg.size();j++){
                    training.addInstance(data.getInstance(tmpNeg.get(j)));
                }
            }
        }
       classifier.buildClassifier(training);
        //System.out.println("Threshold="+((SMOPlatt)classifier).getThreshold());
        //System.out.println("Support vectors="+((SMOPlatt)classifier).getNumSupportVectors());
        
        Vector<Integer> valPos=positiveSets.get(fold);
        int tp=0,fn=0;
        for(int i=0;i<valPos.size();i++){
            Instance tmp=data.getInstance(valPos.get(i));
            int value=classifier.classifyInstance(tmp);
            //System.out.println("POS CLASS: "+value);
            if(value==1&&tmp.getClassValue()==this.positiveClassValue)
                tp++;
            else{
                fn++;
            }
        }
        int tn=0,fp=0;
        Vector<Integer> valNeg=negativeSets.get(fold);
        for(int i=0;i<valNeg.size();i++){
            Instance tmp=data.getInstance(valNeg.get(i));
            int value=classifier.classifyInstance(tmp);
            //System.out.println("NEG CLASS: "+value);
            if(value==0&&tmp.getClassValue()!=this.positiveClassValue){
                tn++;
            }
            else{
                fp++;
            }
        }
        //System.out.println("Validation on "+(valNeg.size()+valPos.size())+" samples");
        return new PerformanceMeasure(tp,tn,fp,fn);
    }

   

}
