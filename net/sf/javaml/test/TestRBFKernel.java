package net.sf.javaml.test;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.RBFKernelSimilarity;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.filter.NormalizeMidrange;

public class TestRBFKernel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RBFKernelSimilarity rbf=new RBFKernelSimilarity(5);
		Instance x=new SimpleInstance(new float[]{0,10});
		Instance y=new SimpleInstance(new float[]{0,0});
		Instance z=new SimpleInstance(new float[]{10,10});
		Instance a=new SimpleInstance(new float[]{10,0});
		
		System.out.println("X-X:"+rbf.calculateDistance(x, x));
		System.out.println("X-Y:"+rbf.calculateDistance(x, y));
		System.out.println("X-Z:"+rbf.calculateDistance(x, z));
		System.out.println("Y-X:"+rbf.calculateDistance(y, x));
		System.out.println("Z-X:"+rbf.calculateDistance(z, x));
		System.out.println("Z-Y:"+rbf.calculateDistance(z, y));
		System.out.println("A-X:"+rbf.calculateDistance(a, x));
		test();
	}
	
	private static void test(){
		System.out.println("Normalized test:");
		
		RBFKernelSimilarity rbf=new RBFKernelSimilarity();
		Instance x=new SimpleInstance(new float[]{0,10});
		Instance y=new SimpleInstance(new float[]{0,0});
		Instance z=new SimpleInstance(new float[]{10,10});
		Instance a=new SimpleInstance(new float[]{10,0});
		Dataset s=new SimpleDataset();
		s.addInstance(x);
		s.addInstance(y);
		s.addInstance(z);
		s.addInstance(a);
		
		Filter f= new NormalizeMidrange(0,2);
		s=f.filterDataset(s);
		System.out.println("Normalized dataset: "+s);
		System.out.println("X-X:"+rbf.calculateDistance(s.getInstance(0), s.getInstance(0)));
		System.out.println("X-Y:"+rbf.calculateDistance(s.getInstance(0), s.getInstance(1)));
		System.out.println("X-Z:"+rbf.calculateDistance(s.getInstance(0), s.getInstance(2)));
		System.out.println("Y-X:"+rbf.calculateDistance(s.getInstance(1), s.getInstance(0)));
		System.out.println("Z-X:"+rbf.calculateDistance(s.getInstance(2), s.getInstance(0)));
		System.out.println("Z-Y:"+rbf.calculateDistance(s.getInstance(2), s.getInstance(1)));
		System.out.println("A-X:"+rbf.calculateDistance(s.getInstance(3), s.getInstance(0)));
	}

}
