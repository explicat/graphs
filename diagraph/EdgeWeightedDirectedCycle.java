package diagraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Customized version of http://algs4.cs.princeton.edu/44sp/
 * Created by explicat on 23.01.2015.
 */
public class EdgeWeightedDirectedCycle {
    private Set<Integer> marked;            // marked[v] = has vertex v been marked?
    private HashMap<Integer, DirectedEdge> edgeTo;        // edgeTo[v] = previous edge on path to v
    private Set<Integer> onStack;            // onStack[v] = is vertex on the stack?
    private Stack<DirectedEdge> cycle;    // directed cycle (or null if no such cycle)

    /**
     * Determines whether the edge-weighted digraph <tt>G</tt> has a directed cycle and,
     * if so, finds such a cycle.
     *
     * @param digraph the edge-weighted digraph
     */
    public EdgeWeightedDirectedCycle(Digraph digraph) {
        marked = new HashSet<>(digraph.numberNodes());
        onStack = new HashSet<>(digraph.numberNodes());
        edgeTo = new HashMap<>(digraph.numberNodes());

        for (Integer v : digraph.nodes()) {
            if (!marked.contains(v)) {
                dfs(digraph, v);
            }
        }

        // check that digraph has a cycle
        assert check(digraph);
    }

    // check that algorithm computes either the topological order or finds a directed cycle
    private void dfs(Digraph digraph, int v) {
        onStack.add(v);
        marked.add(v);

        for (DirectedEdge e : digraph.neighbors(v)) {
            if (e.capacity() - e.flow() <= 0) {
                continue;   // Ignore edges which have no capacity left
            }

            int w = e.to();

            // short circuit if directed cycle found
            if (cycle != null) return;

                //found new vertex, so recur
            else if (!marked.contains(w)) {
                edgeTo.put(w, e);
                dfs(digraph, w);
            }

            // trace back directed cycle
            else if (onStack.contains(w)) {
                cycle = new Stack<>();
                while (e.from() != w) {
                    cycle.push(e);
                    e = edgeTo.get(e.from());
                }
                cycle.push(e);
            }
        }

        onStack.remove(v);
    }

    /**
     * Does the edge-weighted digraph have a directed cycle?
     *
     * @return <tt>true</tt> if the edge-weighted digraph has a directed cycle,
     * <tt>false</tt> otherwise
     */
    public boolean hasCycle() {
        return cycle != null;
    }

    /**
     * Returns a directed cycle if the edge-weighted digraph has a directed cycle,
     * and <tt>null</tt> otherwise.
     *
     * @return a directed cycle (as an iterable) if the edge-weighted digraph
     * has a directed cycle, and <tt>null</tt> otherwise
     */
    public Iterable<DirectedEdge> cycle() {
        return cycle;
    }


    // certify that digraph is either acyclic or has a directed cycle
    private boolean check(Digraph digraph) {

        // edge-weighted digraph is cyclic
        if (hasCycle()) {
            // verify cycle
            DirectedEdge first = null, last = null;
            for (DirectedEdge e : cycle()) {
                if (first == null) first = e;
                if (last != null) {
                    if (last.to() != e.from()) {
                        System.err.printf("cycle edges %s and %s not incident\n", last, e);
                        return false;
                    }
                }
                last = e;
            }

            if (last.to() != first.from()) {
                System.err.printf("cycle edges %s and %s not incident\n", last, first);
                return false;
            }
        }


        return true;
    }
}
