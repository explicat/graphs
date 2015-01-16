package maxFlow;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Ford Fulkerson algorithm for simple max-flow problems in directed acyclic graph (diagraph)
 * Created by explicat on 22.11.2014.
 */
public class FordFulkerson {

    int[][] adj;
    int[][] residualGraph;


    public FordFulkerson(int[][] adj) {
        this.adj = adj;
    }


    public int[][] getAdj() {
        return this.adj;
    }


    public int[][] getResidualGraph() {
        return this.residualGraph;
    }


    private List<Integer> bfs(int[][] graph, final int source, final int sink) {
        int numberOfNodes = graph.length;
        boolean[] visited = new boolean[numberOfNodes];
        Integer[] parent = new Integer[numberOfNodes];
        Queue<Integer> queue = new LinkedList<Integer>();

        for (int i=0; i<parent.length; i++) {
            parent[i] = -1;
        }

        boolean foundSink = false;
        queue.add(source);
        while(!queue.isEmpty() && !foundSink) {
            int u = queue.remove();
            visited[u] = true;

            for (int v=0; v<numberOfNodes && !foundSink; v++) {
                if (sink == v && graph[u][v] > 0) {
                    parent[v] = u;
                    foundSink = true;
                }
                if (u != v && !visited[v] && !queue.contains(v) && graph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                }
            }
        }

        if (!foundSink) {
            return null;
        }
        else {

            // Reconstruct path
            List<Integer> path = new LinkedList<Integer>();
            path.add(sink);
            int idx = sink;
            while (parent[idx] >= 0) {
                int father = parent[idx];
                path.add(father);
                idx = father;
            }
            Collections.reverse(path);
            return path;
        }
    }


    public int fordFulkerson(int source, int sink) {

        // Create residual graph
        int[][] residualGraph = new int[adj.length][adj.length];
        for (int u=0; u<residualGraph.length; u++) {
            for (int v=0; v<residualGraph[u].length; v++) {
                residualGraph[u][v] = adj[u][v];
            }
        }

        int maxFlow = 0;
        List<Integer> path;
        while(null != (path = bfs(residualGraph, source, sink))) {

            // Find maximum (remaining) capacity along edges. We cannot move more flow than the minimum available remaining capacity
            int pathFlow = Integer.MAX_VALUE;
            for (int i=0; i<path.size()-1; i++) {
                int u = path.get(i);
                int v = path.get(i+1);
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            // Move flow and update residual graph
            for (int i=0; i<path.size()-1; i++) {
                int u = path.get(i);
                int v = path.get(i+1);
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        printMatrix(residualGraph);
        this.residualGraph = residualGraph;
        return maxFlow;
    }



    public static void printMatrix(int[][] matrix) {
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
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
        FordFulkerson f = new FordFulkerson(adj);
        int maxFlow = f.fordFulkerson(source, sink);

        System.out.println(maxFlow);
    }

}
