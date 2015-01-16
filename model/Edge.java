package model;

/**
 * Created by explicat on 03.12.2014.
 */
public class Edge {

    private Node from;
    private Node to;
    private int capacity;
    private int cost;
    private int flow;
    private Edge inverseEdge;
    private boolean isResidual = false;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        this.capacity = Integer.MAX_VALUE;
        this.cost = 0;
        this.flow = 0;
    }

    public Edge(Node from, Node to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.cost = 0;
        this.flow = 0;
    }

    public Edge(Node from, Node to, int capacity, int cost) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.cost = cost;
        this.flow = 0;
    }


    /**
     * Clone a given edge e
     * @param e edge to clone
     */
    public Edge(Edge e) {
        this.from = e.from;
        this.to = e.to;
        this.capacity = e.capacity;
        this.cost = e.cost;
        this.flow = e.flow;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getRemainingCapacity() {
        return this.capacity - this.flow;
    }

    public Edge getInverseEdge() {
        return inverseEdge;
    }

    public void setInverseEdge(Edge inverseEdge) {
        this.inverseEdge = inverseEdge;
    }

    public boolean isResidual() {
        return isResidual;
    }

    public void setResidual(boolean isResidual) {
        this.isResidual = isResidual;
    }
}
