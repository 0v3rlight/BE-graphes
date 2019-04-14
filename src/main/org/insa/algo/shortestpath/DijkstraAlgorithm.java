package org.insa.algo.shortestpath;

import org.insa.graph.Graph;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) { 
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;

        Graph graph = data.getGraph();
        Node nodes = graph.getNodes();
        int nbNodes = graph.size();
        
        Label[] labels = new Label[nbNodes];
        for(int i = 0; i<nbNodes; i+=1 )
        {
        	labels[i].sommetCourant = nodes[i];
            labels[i].marque = 0;
        	labels[i].cout = Double.POSITIVE_INFINITY;
        	labels[i].pere = null;
        }
        
               
        
        return solution;
    }

}
