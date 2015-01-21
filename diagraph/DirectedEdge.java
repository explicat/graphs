package diagraph;

/**
 * Created by explicat on 21.01.2015.
 */
public class DirectedEdge {
    private final int from;
    private final int to;
    private int capacity;
    private final int costs;
    private int flow;
    private DirectedEdge inverseEdge;

    public DirectedEdge(int from, int to, int capacity, int costs) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.costs = costs;
        this.flow = 0;
    }

    public DirectedEdge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.costs = 0;
        this.flow = 0;
    }

    public DirectedEdge(DirectedEdge e) {
        this.from = e.from;
        this.to = e.to;
        this.capacity = e.capacity;
        this.costs = e.costs;
        this.flow = e.flow;
    }

    public int from() {
        return this.from;
    }

    public int to() {
        return this.to;
    }

    public int capacity() {
        return this.capacity;
    }

    public void capacity(int capacity) {
        this.capacity = capacity;
    }

    public int costs() {
        return this.costs;
    }

    public int flow() {
        return this.flow;
    }

    public void flow(int newFlow) {
        this.flow = newFlow;
    }

    public DirectedEdge inverseEdge() {
        return this.inverseEdge;
    }

    public void inverseEdge(DirectedEdge inverseEdge) {
        this.inverseEdge = inverseEdge;
    }
}
