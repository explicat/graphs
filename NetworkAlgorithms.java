import java.util.*;

/**
 * Created by explicat on 08.12.2014.
 */
public class NetworkAlgorithms {


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
        network.unsetParents();

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
                        to.setParent(node); // Set current node as new node's parent to enable backward navigation
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
        while(null != sink.getParent()) {
            Edge edgeOnPath = network.getEdge(sink.getParent(), current);
            path.add(edgeOnPath);
            current = sink.getParent();
        }
        Collections.reverse(path);
        return path;
    }



    public static int fordFulkerson(final Network network, Node source, Node sink) {
        Network residual = new Network(network);
        // Find source in residual network
        for (Node node : residual.getNodes()) {
            if (node.getValue().equals(source.getValue())) {
                source = node;
            } else if (node.getValue().equals(sink.getValue())) {
                sink = node;
            }
        }

        // Also create inverse edges with capacity 0
        // Clone list of edges since we're modifying the original list while iterating
        List<Edge> forwardEdges = new ArrayList<>(residual.getEdges());
        for (Edge forwardEdge : forwardEdges) {
            Edge backwardEdge = new Edge(forwardEdge.getTo(), forwardEdge.getFrom());
            backwardEdge.setCapacity(0);
            backwardEdge.setFlow(0);
            backwardEdge.setCost(-forwardEdge.getCost());

            forwardEdge.setInverseEdge(backwardEdge);
            backwardEdge.setInverseEdge(forwardEdge);

            residual.addEdge(backwardEdge);
        }

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

        return maxFlow;
    }
}
