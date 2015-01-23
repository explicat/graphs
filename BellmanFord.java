import java.util.*;

/**
 * Customized version of http://algs4.cs.princeton.edu/44sp/
 * Created by explicat on 23.01.2015.
 */
public class BellmanFord {

    private HashMap<Integer, Integer> distTo;               // distTo[v] = distance  of shortest s->v path
    private HashMap<Integer, DirectedEdge> edgeTo;         // edgeTo[v] = last edge on shortest s->v path
    private Set<Integer> onQueue;             // onQueue[v] = is v currently on the queue?
    private Queue<Integer> queue;          // queue of vertices to relax
    private int relaxCount;                      // number of calls to relax()
    private Iterable<DirectedEdge> cycle;  // negative cycle (or null if no such cycle)

    /**
     * Computes a shortest paths tree from <tt>s</tt> to every other vertex in
     * the edge-weighted digraph <tt>G</tt>.
     * @param G the acyclic digraph
     * @param s the source vertex
     * @throws IllegalArgumentException unless 0 &le; <tt>s</tt> &le; <tt>V</tt> - 1
     */
    public BellmanFord(Digraph G, int s) {
        distTo  = new HashMap<>();
        edgeTo  = new HashMap<>();
        onQueue = new HashSet<>();

        // Initialize all distances to infinity, whereas the start vertex is assigned distance zero
        for (int node : G.nodes()) {
            distTo.put(node, Integer.MAX_VALUE);
        }
        distTo.put(s, 0);

        // Bellman-Ford algorithm
        queue = new LinkedList<>();
        queue.add(s);
        onQueue.add(s);
        while (!queue.isEmpty() && !hasNegativeCycle()) {
            int v = queue.remove();
            onQueue.remove(v);
            relax(G, v);
        }

        assert check(G, s);
    }

    // relax vertex v and put other endpoints on queue if changed
    private void relax(Digraph G, int v) {
        for (DirectedEdge e : G.neighbors(v)) {
            if (e.capacity() - e.flow() <= 0) {
                continue;   // Ignore edges which have no capacity left
            }
            int w = e.to();
            if (distTo.get(w) > distTo.get(v) + e.costs()) {
                distTo.put(w, distTo.get(v) + e.costs());
                edgeTo.put(w, e);
                if (!onQueue.contains(w)) {
                    queue.offer(w);
                    onQueue.add(w);
                }
                if (relaxCount++ % G.numberNodes() == 0) {
                    findNegativeCycle();
                }
            }
        }
    }

    /**
     * Is there a negative cycle reachable from the source vertex <tt>s</tt>?
     * @return <tt>true</tt> if there is a negative cycle reachable from the
     *    source vertex <tt>s</tt>, and <tt>false</tt> otherwise
     */
    public boolean hasNegativeCycle() {
        return cycle != null;
    }

    /**
     * Returns a negative cycle reachable from the source vertex <tt>s</tt>, or <tt>null</tt>
     * if there is no such cycle.
     * @return a negative cycle reachable from the soruce vertex <tt>s</tt>
     *    as an iterable of edges, and <tt>null</tt> if there is no such cycle
     */
    public Iterable<DirectedEdge> negativeCycle() {
        return cycle;
    }

    // by finding a cycle in predecessor graph
    private void findNegativeCycle() {
        Digraph spt = new Digraph();
        for (DirectedEdge e : edgeTo.values()) {
            spt.addEdge(e);
        }

        EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
        cycle = finder.cycle();
    }

    /**
     * Returns the length of a shortest path from the source vertex <tt>s</tt> to vertex <tt>v</tt>.
     * @param v the destination vertex
     * @return the length of a shortest path from the source vertex <tt>s</tt> to vertex <tt>v</tt>;
     *    <tt>Double.POSITIVE_INFINITY</tt> if no such path
     * @throws UnsupportedOperationException if there is a negative relaxCount cycle reachable
     *    from the source vertex <tt>s</tt>
     */
    public double distTo(int v) {
        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");
        return distTo.get(v);
    }

    /**
     * Is there a path from the source <tt>s</tt> to vertex <tt>v</tt>?
     * @param v the destination vertex
     * @return <tt>true</tt> if there is a path from the source vertex
     *    <tt>s</tt> to vertex <tt>v</tt>, and <tt>false</tt> otherwise
     */
    public boolean hasPathTo(int v) {
        return distTo.get(v) < Integer.MAX_VALUE;
    }

    /**
     * Returns a shortest path from the source <tt>s</tt> to vertex <tt>v</tt>.
     * @param v the destination vertex
     * @return a shortest path from the source <tt>s</tt> to vertex <tt>v</tt>
     *    as an iterable of edges, and <tt>null</tt> if no such path
     * @throws UnsupportedOperationException if there is a negative relaxCount cycle reachable
     *    from the source vertex <tt>s</tt>
     */
    public Iterable<DirectedEdge> pathTo(int v) {
        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");
        if (!hasPathTo(v)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo.get(v); e != null; e = edgeTo.get(e.from())) {
            path.push(e);
        }
        return path;
    }

    // check optimality conditions: either
    // (i) there exists a negative cycle reacheable from s
    //     or
    // (ii)  for all edges e = v->w:            distTo[w] <= distTo[v] + e.weight()
    // (ii') for all edges e = v->w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(Digraph G, int s) {

        // has a negative cycle
        if (hasNegativeCycle()) {
            int costs = 0;
            for (DirectedEdge e : negativeCycle()) {
                costs += e.costs();
            }
            if (costs >= 0) {
                System.err.println("error: costs of negative cycle = " + costs);
                return false;
            }
        }

        // no negative cycle reachable from source
        else {

            // check that distTo[v] and edgeTo[v] are consistent
            if (distTo.get(s) != 0 || edgeTo.get(s) != null) {
                System.err.println("distanceTo[s] and edgeTo[s] inconsistent");
                return false;
            }

            for (Integer v : G.nodes()) {
                if (v == s) {
                    continue;
                }

                if (edgeTo.get(v) == null && distTo.get(v) != Integer.MAX_VALUE) {
                    System.err.println("distTo[] and edgeTo[] inconsistent");
                    return false;
                }
            }

            // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
            for (Integer v : G.nodes()) {
                for (DirectedEdge e : G.neighbors(v)) {
                    if (e.capacity() - e.flow() <= 0) {
                        continue;   // Ignore edges which have no capacity left
                    }

                    int w = e.to();
                    if (distTo.get(v) + e.costs() < distTo.get(w)) {
                        System.err.println("edge " + e + " not relaxed");
                        return false;
                    }
                }
            }

            // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
            for (Integer w : G.nodes()) {
                if (edgeTo.get(w) == null) {
                    continue;
                }

                DirectedEdge e = edgeTo.get(w);
                int v = e.from();
                if (w != e.to()) {
                    return false;
                }
                if (distTo.get(v) + e.costs() != distTo.get(w)) {
                    System.err.println("edge " + e + " on shortest path not tight");
                    return false;
                }
            }
        }

        System.out.println("Satisfies optimality conditions");
        System.out.println();
        return true;
    }

}


