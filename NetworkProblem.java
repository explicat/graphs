/**
 * Created by explicat on 08.12.2014.
 */
public class NetworkProblem {

    public static void main(String[] args) {
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

        int maxFlow = NetworkAlgorithms.fordFulkerson(network, source, sink);
        System.out.println(maxFlow);
    }
}
