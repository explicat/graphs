package maxFlow;

import maxFlowMinCost.NetworkAlgorithms;
import model.Network;
import model.Node;

/**
 * Created by explicat on 08.12.2014.
 */
public class NetworkProblem {

    public static void main(String[] args) {
        // Same max-flow problem, however here the graph is represented by objects of nodes and edges
        int[][] adj = {
                {   0, 5, 5, 2, 0, 0, 0 },
                {   0, 0, 0, 0, 1, 0, 2 },
                {   0, 0, 0, 1, 5, 0, 0 },
                {   0, 0, 0, 0, 0, 4, 0 },
                {   0, 0, 0, 0, 0, 2, 3 },
                {   0, 0, 0, 0, 0, 0, 6 },
                {   0, 0, 0, 0, 0, 0, 0 }    };

        int sorceVal = 0;
        int sinkVal = 6;

        Network network = new Network(adj);
        Node source = null;
        Node sink = null;
        for (Node<Integer> node : network.getNodes()) {
            if (sorceVal == node.getValue()) {
                source = node;
            } else if (sinkVal == node.getValue()) {
                sink = node;
            }
        }

        Network residual = NetworkAlgorithms.fordFulkerson(network, source, sink);
        int maxFlow = residual.getMaxFlow();
        System.out.println(maxFlow);
    }
}
