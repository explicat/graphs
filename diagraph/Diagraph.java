package diagraph;

import model.Edge;

import java.util.*;

/**
 * Created by explicat on 21.01.2015.
 */
public class Diagraph {

    private final Map<Integer, Set<DirectedEdge>> edges;

    public Diagraph() {
        this.edges = new HashMap<>();
    }

    public Diagraph(int[][] capacities) {
        this(capacities, null);
    }

    public Diagraph(int[][] capacities, int[][] costs) {
        if (null != costs && capacities.length != costs.length) {
            throw new IllegalArgumentException("capacities and costs must have same size");
        }

        this.edges = new HashMap<>();
        for (int u=0; u<capacities.length; u++) {
            for (int v=0; v<capacities[u].length; v++) {
                if (0 != capacities[u][v]) {    // If an edge exists
                    if (null == costs) {    // Create an edge without costs of the costs matrix is null
                        DirectedEdge edge = new DirectedEdge(u, v, capacities[u][v]);
                        addEdge(edge);
                    } else {
                        DirectedEdge edge = new DirectedEdge(u, v, capacities[u][v], costs[u][v]);
                        addEdge(edge);
                    }
                }
            }
        }
    }

    /** Clone constructor **/
    public Diagraph(Diagraph d) {
        this.edges = new HashMap<>();
        for (DirectedEdge e : d.edges()) {
            this.addEdge(new DirectedEdge(e));
        }
    }


    public static Diagraph createResidual(Diagraph d) {
        Diagraph residual = new Diagraph(d);    // Clone the diagraph first
        // For each edge, create an inverse edge
        List<DirectedEdge> inverseEdges = new ArrayList<>();

        for (DirectedEdge e : residual.edges()) {
            DirectedEdge i = new DirectedEdge(e.to(), e.from(), e.flow(), -e.costs());
            i.inverseEdge(e);
            e.inverseEdge(i);
            inverseEdges.add(i);
        }

        for (DirectedEdge i : inverseEdges) {
            residual.addEdge(i);
        }

        return residual;
    }


    /** Getters **/

    public Set<DirectedEdge> edges() {
        HashSet<DirectedEdge> allEdges = new HashSet<>();
        for (Map.Entry<Integer, Set<DirectedEdge>> edgesByNode : edges.entrySet()) {
            allEdges.addAll(edgesByNode.getValue());
        }
        return allEdges;
    }

    public Set<DirectedEdge> neighbors(int of) {
        if (!edges.containsKey(of)) {
            return new HashSet<>();
        }

        return edges.get(of);
    }

    public Set<DirectedEdge> edges(final int from, final int to) {
        if (!edges.containsKey(from)) {
            return new HashSet<>();
        }

        Set<DirectedEdge> edgesFrom = edges.get(from);
        Set<DirectedEdge> edgesFromTo = new HashSet<>();
        for (DirectedEdge e : edgesFrom) {
            if (to == e.to()) {
                edgesFromTo.add(e);
            }
        }
        return edgesFromTo;
    }

    public int nodes() {
        return edges.size();
    }

    /** Modifying methods **/
    public void addEdge(DirectedEdge edge) {
        int from  = edge.from();
        if (!edges.containsKey(from)) {
            edges.put(from, new HashSet<>());
        }
        Set<DirectedEdge> outgoingEdges = edges.get(from);
        outgoingEdges.add(edge);
    }
}
