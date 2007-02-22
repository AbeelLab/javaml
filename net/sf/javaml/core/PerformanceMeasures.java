package net.sf.javaml.core;



public class PerformanceMeasures {

    public double numTruePositives;

    public double numFalsePositives;

    public double numTrueNegatives;

    public double numFalseNegatives;

    //private double numInstances;
   
    public double getCC(){
        
        return (numTruePositives*numTrueNegatives-numFalsePositives*numFalseNegatives)/ Math.sqrt((numTruePositives+numFalsePositives)*(numTruePositives+numFalseNegatives)*(numTrueNegatives+numFalsePositives)*(numTrueNegatives+numFalseNegatives));
    }
//    public double getCCBajic(){
//        return (numTruePositives*numTruePositives-numFalsePositives*numFalseNegatives)/ Math.sqrt((numTruePositives+numFalsePositives)*(numTruePositives+numFalseNegatives)*(numTrueNegatives+numFalsePositives)*(numTrueNegatives+numFalseNegatives));
//    }
    
    public double getCost(){
        return numFalsePositives/numTruePositives;
    }
    public PerformanceMeasures(double tp,double tn,double fp,double fn){
        //System.out.println(tp+" "+tn+" "+fp+" "+fn);
        this.numTruePositives=tp;
        this.numTrueNegatives=tn;
        this.numFalsePositives=fp;
        this.numFalseNegatives=fn;
        //this.numInstances=tp+tn+fp+fn;
        
    }
    
    public PerformanceMeasures() {
       this(0,0,0,0);
    }
    public double getTPRate() {
        return this.numTruePositives / (this.numTruePositives+this.numFalseNegatives);
    }

    public double getTNRate(){
        return this.numTrueNegatives/(this.numTrueNegatives+this.numFalsePositives);
    }
    public double getFNRate(){
        return this.numFalseNegatives/(this.numTruePositives+this.numFalseNegatives);
    }
    
    public double getFPRate(){
        return this.numFalsePositives/(this.numFalsePositives+this.numTrueNegatives);
    }
    public double getErrorRate() {
        return (this.numFalsePositives + this.numFalseNegatives) / this.getNumInstances();
    }

    public double getAccuracy() {
        return (this.numTruePositives + this.numTrueNegatives) / this.getNumInstances();
    }

    public double getRecall(){
        return this.numTruePositives/(this.numTruePositives+this.numFalseNegatives);
    }
    
    public double getPrecision(){
        return this.numTruePositives/(this.numTruePositives+this.numFalsePositives);
    }
    public double getCorrelation(){
        return (this.numTruePositives*this.numTrueNegatives+this.numFalsePositives*this.numFalseNegatives)/Math.sqrt((this.numTrueNegatives+this.numFalseNegatives)*(this.numTruePositives+this.numFalsePositives)*(this.numTrueNegatives+this.numFalsePositives)*(this.numFalseNegatives+this.numTruePositives));
    }
    
    public double getFMeasure(){
        return getFMeasure(1);
    }
    public double getFMeasure(int beta){
        return ((beta*beta+1)*this.getPrecision()*this.getRecall())/(beta*beta*this.getPrecision()+this.getRecall());
    }
    
    public double getQ9(){
        if(this.numTruePositives+this.numFalseNegatives==0){
            return (this.numTrueNegatives-this.numFalsePositives)/(this.numTrueNegatives+this.numFalsePositives);
        }else if(this.numTrueNegatives+this.numFalsePositives==0){
            return (this.numTruePositives-this.numFalseNegatives)/(this.numTruePositives+this.numFalseNegatives);
        }else
            return 1-Math.sqrt(2)*Math.sqrt(Math.pow(this.numFalseNegatives/(this.numTruePositives+this.numFalseNegatives),2)+Math.pow(this.numFalsePositives/(this.numTrueNegatives+this.numFalsePositives),2));
            
    }
    
    
    
    public String toString(){
        return "TP="+this.numTruePositives+", FP="+this.numFalsePositives+", TN="+this.numTrueNegatives+", FN="+this.numFalseNegatives+", COST="+(this.numFalsePositives/this.numTruePositives);
    }


    public double getNumInstances() {
        return numFalseNegatives+numFalsePositives+numTrueNegatives+numTruePositives;
    }

    
}
