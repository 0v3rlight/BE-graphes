package org.insa.algo.algo_tests;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Djikstra_tests {

	public static void main(String[] args) throws IOException
	{
		enregistrer_test("bonjour", "bonjour", 2);
		
	}

	
	private static void enregistrer_test(String nomCarte, String cout, int nombrePaires) throws IOException
	{
	    String fileContent = "Hello Learner !! Welcome to howtodoinjava.com.";
	     
	    BufferedWriter writer = new BufferedWriter(new FileWriter("c:/temp/samplefile1.txt"));
	    writer.write(fileContent);
	    writer.close();
	}
	
	
}
