/**
 * Created by explicat on 03.12.2014.
 */
public class Node<T> {

    private T value;
    private int mark;

    public Node(T value) {
        this.value = value;
        this.mark = 0;
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

    public int getMark(int mark) {
        return this.mark;
    }
}