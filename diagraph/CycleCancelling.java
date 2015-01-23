package diagraph;

import java.util.*;

/**
 * Created by explicat on 21.01.2015.
 */
public class CycleCancelling {

    /**
     * Implementation according to http://ww1.ucmss.com/books/LFS/CSREA2006/FCS4906.pdf
     * @param diagraph
     * @param source
     * @return
     */
    public static List<DirectedEdge> bellmannFord(Diagraph diagraph, final int source) {
        Set<Integer> nodes = diagraph.nodes();

        // Initialize distance vector
        Map<Integer, Integer> distances = new HashMap<>(nodes.size());
        Map<Integer, Integer> predecessors = new HashMap<>(nodes.size());
        for (Integer node : nodes) {
            if (source == node) {
                distances.put(node, 0);
            } else {
                distances.put(node, Integer.MAX_VALUE);
            }
            predecessors.put(node, -1);
        }

        // Relaxate |nodes|-1 times
        for (int i=0; i<nodes.size()-1; i++) {
            for (DirectedEdge e : diagraph.edges()) {
                // Only consider edges where some capacity is remaining, as fully exploited edges are replaced by their inverse edge
                if (e.capacity() - e.flow() <= 0) {
                    continue;
                }

                if (distances.get(e.to()) > distances.get(e.from()) + e.costs()) {
                    distances.put(e.to(), distances.get(e.from()) + e.costs()); // update distance label
                    predecessors.put(e.to(), e.from()); // update predecessor
                }
            }
        }

        // Check whether there is an each (u, v) such that d(u) + W(u, v) < d(v)
        for (DirectedEdge e : diagraph.edges()) {
            // Only consider edges where some capacity is remaining, as fully exploited edges are replaced by their inverse edge
            if (e.capacity() - e.flow() <= 0) {
                continue;
            }

            if (distances.get(e.to()) > distances.get(e.from()) + e.costs()) {
                // Negative cycle detected. Go backward from v along the predecessor chain, until a cycle is found
                List<Integer> nodesInChain = new LinkedList<>();
                List<DirectedEdge> edgesAlongCycle = new LinkedList<>();

                int current = e.to();
                int predecessor = e.from();
                DirectedEdge edge = e;
                while (!nodesInChain.contains(current)) {
                    nodesInChain.add(current);
                    edgesAlongCycle.add(edge);

                    current = predecessor;
                    predecessor = predecessors.get(current);
                    int lowestCost = Integer.MAX_VALUE;
                    Set<DirectedEdge> possibleEdges = diagraph.edges(predecessor, current);
                    for (DirectedEdge possibleEdge : possibleEdges) {
                        // Take the one that has capacity remaining of course and the lowest costs
                        if (possibleEdge.capacity() - possibleEdge.flow() <= 0) {
                            continue;
                        }

                        if (possibleEdge.costs() < lowestCost) {
                            edge = possibleEdge;
                            lowestCost = possibleEdge.costs();
                        }
                    }

                    if (Integer.MAX_VALUE == lowestCost) {
                        throw new IllegalArgumentException("No edge found");
                    }
                }

                // Cycle completely found

                // Let's see where the cycle has started
                int startIndex = nodesInChain.indexOf(current);
                // And throw out the earlier edges which are not part of the circle
                for (int i=0; i<startIndex; i++) {
                    edgesAlongCycle.remove(0);
                }

                // Return the negative cycle
                return edgesAlongCycle;
            }
        }

        // No negative-weight cycles found
        return null;
    }


    public static int minCostFlow(Diagraph diagraph, final int source, final int sink, final int maxFlow) {
        Diagraph residual = FordFulkerson.fordFulkerson(diagraph, source, sink, maxFlow);

        // Cancel out negative cycles
        BellmanFord bellmanFord = new BellmanFord(residual, source);
        while (bellmanFord.hasNegativeCycle()) {
            Iterable<DirectedEdge> negativeCyle = bellmanFord.negativeCycle();
            // Find the minimum flow which we can shift
            int pathFlow = Integer.MAX_VALUE;
            for (DirectedEdge edge : negativeCyle) {
                pathFlow = Math.min(pathFlow, (edge.capacity() - edge.flow()));
            }

            // Augment the flow along the path
            for (DirectedEdge edge : negativeCyle) {
                if (edge.isResidualEdge()) {
                    // backward edge
                    edge.capacity(edge.capacity() - pathFlow);
                    edge.inverseEdge().flow(edge.inverseEdge().flow() - pathFlow);
                } else {
                    // forward edge
                    edge.flow(edge.flow() + pathFlow);
                    edge.inverseEdge().capacity(edge.inverseEdge().capacity() + pathFlow);  // Update capacity of residual edge
                }
            }

            bellmanFord = new BellmanFord(residual, source);
        }

        // Gather costs
        int costs = 0;
        for (DirectedEdge edge : residual.edges()) {
            if (!edge.isResidualEdge()) {
                costs += edge.flow() * edge.costs();
            }
        }
        return costs;
    }


    public static void main(String[] args) {

        int[][] capacities = {
                { 0, 4, 3, 0, 0, 0 },
                { 0, 0, 1, 3, 3, 0 },
                { 0, 2, 0, 1, 2, 0 },
                { 0, 0, 0, 0, 0, 3 },
                { 0, 0, 0, 1, 0, 2 },
                { 0, 0, 0, 0, 0, 0 }
        };

        int[][] costs = {
                { 0, 5, 7, 0, 0, 0 },
                { 0, 0, 5, 4, 2, 0 },
                { 0, -4, 0, 6, 4, 0},
                { 0, 0, 0, 0, 0, 7 },
                { 0, 0, 0, -3, 0, -2},
                { 0, 0, 0, 0, 0, 0}
        };

        int source = 0;
        int sink = 5;

        Diagraph diagraph = new Diagraph(capacities, costs);
        int minCostFlow = minCostFlow(diagraph, source, sink, 4);
        System.out.println(minCostFlow);
    }
}
