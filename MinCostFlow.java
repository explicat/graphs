import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by explicat on 03.12.2014.
 */
public class MinCostFlow {

    public static int minCostFlow(int[][] capacities, int[][] costsPerUnit, int source, int sink) {
        int nodes = capacities.length;

        FordFulkerson fordFulkerson = new FordFulkerson(capacities);
        int maxFlow = fordFulkerson.fordFulkerson(source, sink);
        int[][] residualGraph = fordFulkerson.getResidualGraph();
        System.out.println("maxFlow: " + maxFlow);

        List<Integer> negativeCycle;
        // As long as a negative cycle exists
        // TODO We need to look for negative cycles in the residual graph with respect to the costs, not to the flow
        while (null != (negativeCycle = BellmanFord.containsNegativeCycle(residualGraph))) {
            // Remove negative cycles

            // Get min flow of residual graph along cycles edges
            int minFlow = Integer.MAX_VALUE;
            for (int i=0; i<negativeCycle.size() - 1; i++) {
                int u = negativeCycle.get(i);
                int v = negativeCycle.get(i+1);

                minFlow = Math.min(minFlow, residualGraph[u][v]);
            }

            // Push this min flow in opposite direction along cycle. Update residual graph
            for (int i=0; i<negativeCycle.size() - 1; i++) {
                int u = negativeCycle.get(i);
                int v = negativeCycle.get(i+1);

                residualGraph[u][v] -= minFlow;
                residualGraph[v][u] += minFlow;
            }
        }

        // Sum up costs of final flow
        int cost = 0;
        boolean[] visited = new boolean[nodes];

        // Start with sink and iterate over backedges of residual graph
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(sink);
        while (!queue.isEmpty()) {
            int u = queue.remove();
            visited[u] = true;

            for (int v=0; v<nodes; v++) {
                if (residualGraph[u][v] > 0 && residualGraph[u][v] != capacities[u][v]) {
                    cost += residualGraph[u][v] * costsPerUnit[v][u];
                }

                if (!visited[v]) {
                    queue.add(v);
                }
            }
        }

        return cost;
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
                { 0, 0, 5, 0, 0, 0 },
                { 0, -4, 0, 6, 4, 0},
                { 0, 0, 0, 0, 0, 7 },
                { 0, 0, 0, -3, 0, -2},
                { 0, 0, 0, 0, 0, 0}
        };

        int source = 0;
        int sink = 5;

        int minCostFlow = minCostFlow(graph, costPerUnit, source, sink);
        System.out.println(minCostFlow);
    }
}
