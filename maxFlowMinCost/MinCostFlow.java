package maxFlowMinCost;

import model.Edge;
import model.Network;
import model.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by explicat on 03.12.2014.
 */
public class MinCostFlow {


    public static int minCostFlow(final Network network, Node source, Node sink, int maxFlow) {
        // Determine max flow
        Network residual = NetworkAlgorithms.fordFulkerson(network, source, sink, maxFlow);
        Node residualSource = residual.getNodeByValue(source.getValue());
        Node residualSink = residual.getNodeByValue(sink.getValue());

        // Find negative cycles
        List<Edge> negativeCycle;
        while (null != (negativeCycle = NetworkAlgorithms.bellmanFord(residual, residualSource))) {

            // Get min flow of residual graph along cycles edges
            int minFlow = Integer.MAX_VALUE;
            for (int i=0; i<negativeCycle.size(); i++) {
                Edge edge = negativeCycle.get(i);
                minFlow = Math.min(minFlow, edge.getRemainingCapacity());
            }

            for (int i=0; i<negativeCycle.size(); i++) {

            }

            // Push this min flow in opposite direction along cycle. Update residual graph
            for (int i=0; i<negativeCycle.size(); i++) {
                Edge edge = negativeCycle.get(i);
                edge.setFlow(edge.getFlow() - minFlow);
                edge.getInverseEdge().setFlow(edge.getFlow() + minFlow);
            }
        }

        // Sum up the total costs by backwards traversal
        int cost = 0;
        for (Node node : residual.getNodes()) {
            node.setMark(0);
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(residualSink);
        while (!queue.isEmpty()) {
            Node v = queue.remove();
            v.setMark(1);

            for (Edge edge : network.getEdgesByNode().get(v)) {
                if (edge.isResidual()) {
                    cost += edge.getFlow() * edge.getCost() * (-1);
                }

                // Check whether the node from the other side has already been visited
                Node u = edge.getTo();
                if (u.getMark() != 1) {
                    queue.add(u);
                }
            }
        }

        return cost;

    }




    /**
     * Cycle-cancelling algorithm
     * @param capacities
     * @param costsPerUnit
     * @param source
     * @param sink
     * @return
     */
    public static int minCostFlow(int[][] capacities, int[][] costsPerUnit, int source, int sink, int maxFlow) {
        Network network = new Network(capacities, costsPerUnit);
        Node sourceNode = network.getNodeByValue(source);
        Node sinkNode = network.getNodeByValue(sink);
        return minCostFlow(network, sourceNode, sinkNode, maxFlow);
    }



    public static void main(String[] args) {

        int[][] graph = {
                { 0, 4, 3, 0, 0, 0 },
                { 0, 0, 1, 3, 3, 0 },
                { 0, 2, 0, 1, 2, 0 },
                { 0, 0, 0, 0, 0, 3 },
                { 0, 0, 0, 1, 0, 2 },
                { 0, 0, 0, 0, 0, 0 }
        };

        int[][] costPerUnit = {
                { 0, 5, 7, 0, 0, 0 },
                { 0, 0, 5, 4, 2, 0 },
                { 0, -4, 0, 6, 4, 0},
                { 0, 0, 0, 0, 0, 7 },
                { 0, 0, 0, -3, 0, -2},
                { 0, 0, 0, 0, 0, 0}
        };

        int source = 0;
        int sink = 5;

        int minCostFlow = minCostFlow(graph, costPerUnit, source, sink, 4);
        System.out.println(minCostFlow);
    }
}
