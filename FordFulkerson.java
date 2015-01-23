import java.util.*;

/**
 * Created by explicat on 21.01.2015.
 */
public class FordFulkerson {


    /**
     * Determines the edges which one has to walk to get from source to sink by breath first search
     * @param G digraph. Edges which have no capacity remaining are ignored.
     * @param source number of source vertex
     * @param target number of target vertex
     * @return list of edges which one has to walk from source to sink, or null if there is no path
     */
    public static List<DirectedEdge> bfs(final Digraph G, final int source, final int target) {
        int nodes = G.numberNodes();
        HashSet<Integer> visited = new HashSet<>(nodes);
        HashMap<Integer, Integer> parent = new HashMap<>(); // <current node, parent node>
        Queue<Integer> queue = new LinkedList<>();

        boolean foundTarget = false;
        queue.add(source);
        while(!queue.isEmpty() && !foundTarget) {
            int u = queue.remove();
            visited.add(u);

            // Get edge starting at vertex u
            for (DirectedEdge e : G.neighbors(u)) {
                if (e.capacity() - e.flow() <= 0) { // Ignore edges which have no capacity remaining
                    continue;
                }

                int v = e.to();
                if (target == v) {  // Did we find the target?
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


        // Reconstruct path by waling along the predecessor chain
        List<DirectedEdge> path = new LinkedList<>();
        int current = target;
        int predecessor = parent.get(target);
        while (predecessor >= 0) {
            Set<DirectedEdge> possibleEdges = G.edges(predecessor, current);

            // Take the edge with the highest remaining capacity
            int maxRemainingCapacity = 0;
            DirectedEdge bestEdge = null;
            for (DirectedEdge e : possibleEdges) {
                // Again, ignore edges which have no capacity remaining
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


    /**
     * Determine the maximum flow which can be send from source to sink using the standard Ford Fulkerson algorithm
     * @param G acyclic directed graph
     * @param source number of source vertex
     * @param sink number of sink vertex
     * @return maximum flow, can be 0 if there is no path from source to sink at all
     */
    public static int fordFulkerson(final Digraph G, final int source, final int sink) {
        Digraph residual = G.createResidual();
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
     * Find a valid routing for a given flow to transport. Especially useful for min-cost-flow problems.
     * @param G directed acyclic graph
     * @param source number of source vertex which has demand excess
     * @param sink number of sink vertex which has demand demand
     * @param demand flow to transport from source to sink
     * @return residual graph modified by the algorithm
     * @throws java.lang.IllegalArgumentException if all paths in G from source to sink do not offer enough capacity to send demand flow
     */
    public static Digraph fordFulkerson(final Digraph G, final int source, final int sink, int demand) {
        Digraph residual = G.createResidual();
        int curFlow = 0;

        List<DirectedEdge> path;
        while (null != (path = bfs(residual, source, sink)) && (curFlow < demand)) {
            // Find maximum remaining capacity along edges. We cannot move more flow than the minimum available remaining capacity
            int pathFlow = Integer.MAX_VALUE;
            for (DirectedEdge e : path) {
                pathFlow = Math.min(pathFlow, (e.capacity() - e.flow()));
            }
            pathFlow = Math.min(demand - curFlow, pathFlow);

            // Augment flow and update graph
            for (DirectedEdge e : path) {
                e.flow(e.flow() + pathFlow);
                e.inverseEdge().capacity(e.inverseEdge().capacity() + pathFlow);
            }

            curFlow += pathFlow;
        }

        if (curFlow < demand) {
            throw new IllegalArgumentException("Not enough capacity to send " + demand + " flow from source to sink");
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
        Digraph digraph = new Digraph(adj);
        int maxFlow = fordFulkerson(digraph, source, sink);
        System.out.println(maxFlow);
    }
}
