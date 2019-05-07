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
		int comparaison = Double.compare(this.getTotalCost(),o.getTotalCost());
		//return x tq:
		// si this=o : x=0
		// si this<o : x<0
		// si this>o : x>0
		if(comparaison == 0)
		{
			return Double.compare(o.cout, this.cout);
		}
		return comparaison;
	}
	
	public double getTotalCost()
	{
		return cout;
	}
}
