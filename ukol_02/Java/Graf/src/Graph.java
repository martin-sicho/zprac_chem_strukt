import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * Třída reprezentující obecný neorientovaný graf.
 *
 * Created by Martin Šícho on 3.10.14.
 */
public class Graph {

    // members

    private Set<Node> nodeSet;

    // constructors

    Graph() {
        nodeSet = new HashSet<>();
    }

    Graph(Graph graph) {
        this.nodeSet = new HashSet<>(graph.nodeSet);
    }

    Graph(String graph) {
        // TODO: implementovat parsovani string reprezentace
    }

    // static methods

    public static Graph readGraph(Reader reader) throws java.io.IOException, java.text.ParseException {
        // TODO: vytvorit
        return new Graph();
    }

    // non-static methods

    public void writeGraph(Writer writer) throws java.io.IOException {
        // TODO: vytvorit
    }

    public void addNode(Node node) {
        if (!node.isInGraph(this)) {
            node.addToGraph(this);
        }
        nodeSet.add(node);
    }

    public void removeNode(Node node) {
        if (node.isInGraph(this)) {
            for (Node neighbor : node.getConnectedNodes()) {
                if (nodeSet.contains(neighbor)
                        && neighbor.isInGraph(this)
                        && neighbor.getGraphCount() == 1
                        ) {
                    neighbor.removeNode(node);
                }
            }
            node.removeFromGraph(this);
        }
        nodeSet.remove(node);
    }

    public void connectNodes(Node node1, Node node2) {
        node1.addToGraph(this);
        node2.addToGraph(this);
        node1.addNode(node2);
    }

    public boolean hasNode(Node node) {
        return nodeSet.contains(node);
    }

    public long getNodeCount() {
        return nodeSet.size();
    }

    public long getEdgeCount() {
        // TODO: udelat to pak trochu efektivneji :)
        long edge_count = 0;
        long self_connected = 0;

        for (Node node : nodeSet) {
            for (Node neighbor : node.getConnectedNodes()) {
                if (neighbor.isInGraph(this)) {
                    if (!neighbor.equals(node)) {
                        ++edge_count;
                    } else {
                        ++self_connected;
                    }
                }
            }
        }
        return edge_count / 2 + self_connected;
    }

    // overrides

    @Override
    public String toString() {
        return String.format("Graph[nodes:%d,edges:%d]", getNodeCount(), getEdgeCount());
    }

}
