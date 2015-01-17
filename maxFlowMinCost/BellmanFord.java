package maxFlowMinCost;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation according to http://ww1.ucmss.com/books/LFS/CSREA2006/FCS4906.pdf
 * Created by explicat on 03.12.2014.
 */
public class BellmanFord {

    /**
     * Detectes whether the graph given as adjacency matrix contains a negative cycle
     * @param graph definded by adjacency matrix
     * @return the list of nodes along the negative cycle if there is one, null if the graph does not contain any negative cycles
     */
    public static List<Integer> containsNegativeCycle(int[][] graph, int source) {
        int nodes = graph.length;
        int[] distance = new int[nodes];
        int[] predecessor = new int[nodes];

        // Initialize
        for (int i=0; i<nodes; i++) {
            distance[i] = Integer.MAX_VALUE;
            predecessor[i] = -1;
        }
        distance[source] = 0;

        // Loop |nodes|-1 times
        for (int n=0; n<nodes-1; n++) {

            // Loop through edges
            for (int u=0; u<nodes; u++) {
                for (int v=0; v<nodes; v++) {

                    // If there is an edge from u to v
                    if (graph[u][v] > 0) {
                        if (distance[u] + graph[u][v] < distance[v]) {
                            distance[v] = distance[u] + graph[u][v];
                            predecessor[v] = u;
                        }
                    }

                }
            }
        }

        // Loop through edges again
        for (int u=0; u<nodes; u++) {
            for (int v=0; v<nodes; v++) {

                // If there is an edge from u to v
                if (graph[u][v] > 0) {
                    if (distance[u] + graph[u][v] < distance[v]) {
                        // There must be a negative cycle

                        // Detect negative cycle
                        List<Integer> cycleNodes = new LinkedList<>();
                        cycleNodes.add(v);
                        int parent = v;
                        while (-1 != (parent = predecessor[parent]) && !cycleNodes.contains(parent)) {
                            cycleNodes.add(parent);
                        }
                        cycleNodes.add(v);
                        return cycleNodes;
                    }
                }
            }
        }

        return null;
    }
}
