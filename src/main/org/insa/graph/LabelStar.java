package org.insa.graph;

public class LabelStar extends Label
{
	public double estimatedCost;


	
	public double getTotalCost()
	{
		return this.cout+this.estimatedCost;
	}
	
}
