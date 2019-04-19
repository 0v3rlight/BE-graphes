package org.insa.graph;

public class Label implements Comparable<Label>
{
	public Node sommetCourant;
	public boolean marque;
	public double cout;
	public Node pere;

	@Override
	public int compareTo(Label o)
	{
		return Double.compare(cout, o.cout);
	}
	
}
