import java.util.*;

/**
 * Created by explicat on 03.12.2014.
 */
public class Network {

    private List<Node> nodes;
    private List<Edge> edges;

    private Map<Node, List<Edge>> edgesByNode;


    public Network(int[][] capacities) {
        int nodes = capacities.length;

        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>();
        this.edgesByNode = new HashMap<>(nodes);

        Map<Integer, Node> created = new HashMap<>();
        for (int u=0; u<nodes; u++) {
            Node<Integer> n;
            if (created.containsKey(u)) {
                n = created.get(u);
            } else {
                n = new Node<>(u);
                created.put(u, n);
            }
            List<Edge> edges = new LinkedList<>();

            this.nodes.add(n);

            // Iterate over edges
            for (int v=0; v<nodes; v++) {
                if (capacities[u][v] > 0) {
                    Node m;
                    if (created.containsKey(v)) {
                        m = created.get(v);
                    } else {
                        m = new Node<>(v);
                        created.put(v, m);
                    }

                    Edge edge = new Edge(n, m, capacities[u][v]);
                    this.edges.add(edge);    // Add to network's list of edges
                    edges.add(edge);            // Add to node's list of edges
                }
            }

            this.edgesByNode.put(n, edges);
        }
    }

    public Network(int[][] capacities, int[][] costs) {
        this(capacities);
        for (Edge e : edges) {
            Node<Integer> from = e.getFrom();
            Node<Integer> to = e.getTo();
            int cost = costs[from.getValue()][to.getValue()];
            e.setCost(cost);
        }
    }


    /**
     * Clone a given network
     * @param network
     */
    public Network(Network network) {
        this.nodes = new ArrayList<>(network.getNodes().size());
        this.edges = new ArrayList<>(network.getEdges().size());
        this.edgesByNode = new HashMap<>(network.getNodes().size());

        Map<Object, Node> nodeMap = new HashMap<>(network.getNodes().size());
        for (Node oldNode : network.getNodes()) {
            Node newNode = new Node(oldNode);
            this.nodes.add(newNode);
            this.edgesByNode.put(newNode, new LinkedList<Edge>());
            nodeMap.put(newNode.getValue(), newNode);
        }

        for (Edge oldEdge : network.getEdges()) {
            Edge newEdge = new Edge(oldEdge);
            newEdge.setFrom(nodeMap.get(oldEdge.getFrom().getValue()));
            newEdge.setTo(nodeMap.get(oldEdge.getTo().getValue()));
            this.edges.add(newEdge);
            this.edgesByNode.get(newEdge.getFrom()).add(newEdge);
        }
    }


    public void unmarkAllNodes() {
        for (Node n : nodes) {
            n.setMark(0);
        }
    }


    public List<Node> getNodes() {
        return this.nodes;
    }

    public List<Edge> getEdges() {
        return this.edges;
    }
}