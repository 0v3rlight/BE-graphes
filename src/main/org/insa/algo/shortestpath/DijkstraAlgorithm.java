package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Label;
import org.insa.graph.Node;
import org.insa.graph.Path;
import org.insa.graph.Point;

public class DijkstraAlgorithm extends ShortestPathAlgorithm
{
	private BinaryHeap<Label> heap;
	private Label[] labels ;
	
	private ShortestPathData data;
	private ShortestPathSolution solution;

	private Graph graph;
	private List<Node> nodes;
	private int nbNodes;
	
	private int originId ;
	private int destId;

	public DijkstraAlgorithm(ShortestPathData data)
	{
		super(data);
	}

	private void creerLabels()
	{
		heap = new BinaryHeap<Label>();
		labels = new Label[nbNodes];
	}
	
	@Override
	protected ShortestPathSolution doRun()
	{
		data = getInputData();
		solution = null;
        notifyOriginProcessed(data.getOrigin());

		graph = data.getGraph();
		nodes = graph.getNodes();
		nbNodes = graph.size();

		
		originId = data.getOrigin().getId();
		destId = data.getDestination().getId();
		
		System.out.println(originId);
		System.out.println(destId);

		creerLabels();
		
		for (int i = 0; i < nbNodes; i++)
		{
			labels[i] = new Label();
			labels[i].sommetCourant = nodes.get(i);
			labels[i].marque = false;
			labels[i].pere = null;
			labels[i].cout = nodes.get(i).getId() == originId ? 0 : Double.POSITIVE_INFINITY;
			heap.insert(labels[i]);
		}
		
		Arc[] prevArcs = new Arc[nbNodes];
		boolean finish = false;
		
		while (!heap.isEmpty() && !finish)
		{
			Label minLabel = heap.deleteMin();
			minLabel.marque = true;
			//System.out.println(nodeString(minLabel.sommetCourant));
			
			for (Arc a : minLabel.sommetCourant.getSuccessors())
			{
				if(minLabel.sommetCourant.getId() == destId)
				{
					finish = true;
					break;
				}
				
				Label label = labels[a.getDestination().getId()];
				
				if (label.marque)
				{
					continue;
				}
				
				double oldCost = label.getTotalCost();
				double newCost = minLabel.getTotalCost() + data.getCost(a);
				
				if (newCost < oldCost)
				{
					prevArcs[a.getDestination().getId()] = a;
					heap.remove(label);
					label.cout = minLabel.cout + data.getCost(a);
					label.pere = minLabel.sommetCourant;
					heap.insert(label);
					
                    if(Double.isFinite(newCost) && Double.isInfinite(oldCost))
                    {
                    	notifyNodeReached(a.getDestination());
                    }
				}
			}
		}
		
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		Node n = nodes.get(destId);
		
		while(n != null)
		{
			Arc a = prevArcs[n.getId()];
			if(a != null)
			{
				arcs.add(a);
			}
			n = labels[n.getId()].pere;
		}
		
		Collections.reverse(arcs);
		
		if(arcs.isEmpty())
		{
			solution = new ShortestPathSolution(data, Status.INFEASIBLE);
		}
		
		else
		{
	        solution = new ShortestPathSolution(data, Status.OPTIMAL, new Path(graph, arcs));
            notifyDestinationReached(data.getDestination());
		}
		return solution;
	}
	
	//debug pour la map carré
	private String nodeString(Node n)
	{
		Point p = n.getPoint();
		int x = (int)(p.getLongitude() * 10 - 0.5f) / 2;
		int y = (int)((1 - p.getLatitude()) * 10 - 0.5f) / 2;
		return String.format("Point %d (%d, %d)", n.getId(), x, y);
	}

}
