package net.sf.javaml.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class DatasetReformer {
	
	public static void main(String[] args)throws IOException{
	
    BufferedReader in = new BufferedReader(new FileReader("cancer.txt"));
    String line = in.readLine();
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("out.data")));
    
    while (line != null) {
        String[] arr = line.split(",");
        int[] values = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
        	try {
            	values[i] = Integer.parseInt(arr[i]); 
            } catch (NumberFormatException e) {
                values[i] = 0;
            }
        }
        String tmp = "";
        for (int i=0; i< arr.length; i++){
        	tmp += String.valueOf(values[i])+"\t";
        	
        }
        System.out.println(tmp);
        out.write(tmp);
        line = in.readLine();
    }
    in.close();
    out.close();
    }
}
