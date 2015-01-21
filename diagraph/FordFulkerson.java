package diagraph;


import java.util.*;

/**
 * Created by explicat on 21.01.2015.
 */
public class FordFulkerson {


    public static List<DirectedEdge> bfs(final Diagraph diagraph, final int source, final int target) {
        int nodes = diagraph.numberNodes();
        HashSet<Integer> visited = new HashSet<>(nodes);
        HashMap<Integer, Integer> parent = new HashMap<>(); // <current node, parent node>
        Queue<Integer> queue = new LinkedList<>();

        boolean foundTarget = false;
        queue.add(source);
        while(!queue.isEmpty() && !foundTarget) {
            int u = queue.remove();
            visited.add(u);

            // Get neighbors of u
            Set<DirectedEdge> edges = diagraph.neighbors(u);

            for (DirectedEdge e : edges) {
                if (e.capacity() - e.flow() <= 0) {
                    continue;
                }

                int v = e.to();
                if (target == v) {
                    parent.put(v, u);
                    foundTarget = true;
                } else if (!visited.contains(v)) {
                    queue.add(v);
                    parent.put(v, u);
                }
            }
        }

        if (!foundTarget) {
            return null;
        }


        // Reconstruct path
        List<DirectedEdge> path = new LinkedList<>();
        int current = target;
        int predecessor = parent.get(target);
        while (predecessor >= 0) {
            Set<DirectedEdge> possibleEdges = diagraph.edges(predecessor, current);

            // Take the edge with the highest remaining capacity
            int maxRemainingCapacity = 0;
            DirectedEdge bestEdge = null;
            for (DirectedEdge e : possibleEdges) {
                if (e.capacity() - e.flow() <= 0) {
                    continue;
                }

                if (e.capacity() - e.flow() > maxRemainingCapacity) {
                    maxRemainingCapacity = e.capacity() - e.flow();
                    bestEdge = e;
                }
            }

            if (null == bestEdge) {
                throw new NullPointerException("Could not find any edge from " + predecessor + " to " + current);
            }

            path.add(bestEdge);
            current = predecessor;
            predecessor = (parent.containsKey(predecessor)) ? parent.get(predecessor) : -1; // No more predecessors
        }

        // Finally reverse the path as we want to run from the source to the target
        Collections.reverse(path);
        return path;
    }


    public static int fordFulkerson(final Diagraph diagraph, final int source, final int sink) {
        Diagraph residual = Diagraph.createResidual(diagraph);
        int maxFlow = 0;

        List<DirectedEdge> path;
        while (null != (path = bfs(residual, source, sink))) {
            // Find maximum remaining capacity along edges. We cannot move more flow than the minimum available remaining capacity
            int pathFlow = Integer.MAX_VALUE;
            for (DirectedEdge e : path) {
                pathFlow = Math.min(pathFlow, (e.capacity() - e.flow()));
            }

            // Augment flow and update graph
            for (DirectedEdge e : path) {
                e.flow(e.flow() + pathFlow);
                e.inverseEdge().capacity(e.inverseEdge().capacity() + pathFlow);
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }


    /**
     * Find a valid routing for a given flow to transport
     * @param diagraph
     * @param source
     * @param sink
     * @param maxFlow
     * @return the residual graph
     */
    public static Diagraph fordFulkerson(final Diagraph diagraph, final int source, final int sink, int maxFlow) {
        Diagraph residual = Diagraph.createResidual(diagraph);
        int curFlow = 0;

        List<DirectedEdge> path;
        while (null != (path = bfs(residual, source, sink)) && (curFlow < maxFlow)) {
            // Find maximum remaining capacity along edges. We cannot move more flow than the minimum available remaining capacity
            int pathFlow = Integer.MAX_VALUE;
            for (DirectedEdge e : path) {
                pathFlow = Math.min(pathFlow, (e.capacity() - e.flow()));
            }
            pathFlow = Math.min(maxFlow - curFlow, pathFlow);

            // Augment flow and update graph
            for (DirectedEdge e : path) {
                e.flow(e.flow() + pathFlow);
                e.inverseEdge().capacity(e.inverseEdge().capacity() + pathFlow);
            }

            curFlow += pathFlow;
        }

        return residual;
    }


    public static void main(String[] args) {
        // Example
        int[][] adj = {
                {   0, 5, 5, 2, 0, 0, 0 },
                {   0, 0, 0, 0, 1, 0, 2 },
                {   0, 0, 0, 1, 5, 0, 0 },
                {   0, 0, 0, 0, 0, 4, 0 },
                {   0, 0, 0, 0, 0, 2, 3 },
                {   0, 0, 0, 0, 0, 0, 6 },
                {   0, 0, 0, 0, 0, 0, 0 }    };

        int source = 0;
        int sink = 6;
        Diagraph diagraph = new Diagraph(adj);
        int maxFlow = fordFulkerson(diagraph, source, sink);

        System.out.println(maxFlow);
    }
}
