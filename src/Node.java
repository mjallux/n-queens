import java.util.ArrayList;
import java.util.BitSet;

public class Node<T> {
    ArrayList<Node<T>> children;
    Node<T> parent;
    T data;
    int depth;

    // todo: split into class extends
    BitSet moves;

    public Node(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public void addChild(Node<T> child) {
        child.parent = this;
        child.depth = this.depth + 1;
        children.add(child);
    }

    public int getNextMove() {
        return moves.nextClearBit(0);
    }

    public void removeChild(Node<T> child) {
        this.children.remove(child);
    }

    public ArrayList<Node<T>> getChildren() {
        return children;
    }
}
