package org.insa.algo.algo_tests;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.insa.algo.ArcInspector;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;

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
	
	private static Path testChemin(Graph map, Node origin, Node destination, String cout)
	{
		Path path;
		ArcInspector arc;
		ShortestPathData data = new ShortestPathData(map, origin, destination, arc );
		
		
		return path;
	}
	
}
