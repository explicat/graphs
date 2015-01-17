package maxFlowMinCost;

import model.Edge;
import model.Network;
import model.Node;

import java.util.*;

/**
 * Created by explicat on 08.12.2014.
 */
public class NetworkAlgorithms {

    /**
     * Negative cycle detection depending on costs
     * @return
     */
    public static List<Edge> bellmanFord(Network network, Node source) {
        // Use node markers as distance labels
        int nodes = network.getNodes().size();

        // Initialize: predecessor of each node should be unset, distance infinity
        for (Node node : network.getNodes()) {
            node.setEdgeFromPredecessor(null);
            node.setMark(Integer.MAX_VALUE);
        }
        source.setMark(0);

        // Loop |nodes|-1 times
        for (int n=0; n<nodes-1; n++) {

            // Loop through edges
            for (Edge edge : network.getEdges()) {
                if (edge.isResidual() && edge.getFlow() == 0) {
                    continue;
                }

                if (edge.getFrom().getMark() + edge.getCost() < edge.getTo().getMark()) {
                    edge.getTo().setMark(edge.getFrom().getMark() + edge.getCost());
                    edge.getTo().setEdgeFromPredecessor(edge);
                }
            }
        }

        // TODO

        // Loop through edges again
        for (Edge edge : network.getEdges()) {
            if (edge.isResidual() && edge.getFlow() == 0) {
                continue;
            }

            if (edge.getFrom().getMark() + edge.getCost() < edge.getTo().getMark()) {
                // There must be a negative cycle

                // Get the negative cycle
                List<Edge> cycle = new LinkedList<>();
                cycle.add(edge);
                Edge edgeToPredeccesor = edge;
                while (null != (edgeToPredeccesor = edgeToPredeccesor.getFrom().getEdgeFromPredecessor()) && !cycle.contains(edgeToPredeccesor)) {
                    cycle.add(edgeToPredeccesor);
                }

                return cycle;
            }
        }

        return null;
    }




    public static List<Edge> bfs(Network network, Node source, Node sink) {
        return bfs(network, source, sink, false);
    }

    /**
     * Find a path consisting of several edges from a given source to a given sink node in a given network
     * @param network
     * @param source node
     * @param sink node
     * @param considerRemainingCapacity flag whether to consider only edges whose capacity is higher than their current flow value
     * @return list of edges to traverse from source to sink, null if there is no path
     */
    public static List<Edge> bfs(Network network, Node source, Node sink, boolean considerRemainingCapacity) {
        assert(network.getNodes().contains(source));
        assert(network.getNodes().contains(sink));

        network.unmarkAllNodes();
        network.unsetPredeccesors();

        boolean foundSink = false;
        Queue<Node> queue = new LinkedList<>();
        queue.add(source);
        while (!queue.isEmpty() && !foundSink) {
            Node node = queue.remove();
            node.setMark(1);
            if (node.equals(sink)) {
                foundSink = true;
                // parent has already been set
            }
            else {

                for (Edge edge : network.getEdgesByNode().get(node)) {
                    if (considerRemainingCapacity && edge.getRemainingCapacity() <= 0) {
                        continue;
                    }

                    Node to = edge.getTo();
                    if (to.getMark() == Node.UNMARKED && !queue.contains(to)) {
                        queue.add(to);
                        to.setEdgeFromPredecessor(edge); // Set current node as new node's parent to enable backward navigation
                    }
                }
            }
        }

        if (!foundSink) {
            // Could not find sink from source
            return null;
        }

        // Reconstruct path
        List<Edge> path = new LinkedList<>();
        Node current = sink;
        while(null != current.getEdgeFromPredecessor()) {
            path.add(current.getEdgeFromPredecessor());
            current = current.getEdgeFromPredecessor().getFrom();
        }
        Collections.reverse(path);
        return path;
    }



    public static Network fordFulkerson(final Network network, Node source, Node sink) {
        Network residual = network.createResidual();
        // Find source and sink nodes in residual network
        source = residual.getNodeByValue(source.getValue());
        sink = residual.getNodeByValue(sink.getValue());

        int maxFlow = 0;
        List<Edge> path;
        while (null != (path = bfs(residual, source, sink, true))) {

            // Find maximum capacity along edges of path
            int pathFlow = Integer.MAX_VALUE;
            for (Edge edge : path) {
                pathFlow = Math.min(pathFlow, edge.getRemainingCapacity());
            }

            // Augment the flow
            for (Edge edge : path) {
                edge.setFlow(edge.getFlow() + pathFlow);
                edge.getInverseEdge().setFlow(edge.getInverseEdge().getFlow() - pathFlow);
            }
            maxFlow += pathFlow;
        }

        residual.setMaxFlow(maxFlow);
        return residual;
    }


    public static Network fordFulkerson(final Network network, Node source, Node sink, int maxFlow) {
        Network residual = network.createResidual();
        // Find source and sink nodes in residual network
        source = residual.getNodeByValue(source.getValue());
        sink = residual.getNodeByValue(sink.getValue());

        int curFlow = 0;
        List<Edge> path;
        while (null != (path = bfs(residual, source, sink, true)) && (0 != maxFlow - curFlow)) {

            // Find maximum capacity along edges of path
            int pathFlow = Integer.MAX_VALUE;
            for (Edge edge : path) {
                pathFlow = Math.min(pathFlow, edge.getRemainingCapacity());
            }

            // We don't need to send more than maxFlow
            pathFlow = Math.min(pathFlow, maxFlow - curFlow);

            // Augment the flow
            for (Edge edge : path) {
                edge.setFlow(edge.getFlow() + pathFlow);
                edge.getInverseEdge().setFlow(edge.getInverseEdge().getFlow() - pathFlow);
            }
            curFlow += pathFlow;
        }

        residual.setMaxFlow(maxFlow);
        return residual;
    }
}
