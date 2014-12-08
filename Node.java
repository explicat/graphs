/**
 * Created by explicat on 03.12.2014.
 */
public class Node<T> {

    public static final Integer UNMARKED = 0;

    private T value;
    private int mark;
    private Node parent;

    public Node(T value) {
        this.value = value;
        this.mark = UNMARKED;
    }

    /**
     * Clone node n
     * @param n
     */
    public Node(Node<T> n) {
        this.value = n.value;
        this.mark = n.mark;
    }

    public T getValue() {
        return value;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getMark() {
        return this.mark;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}