import java.util.HashSet;
import java.util.Set;

/**
 * Třída reprezentující vrchol obecného neorientovaného grafu.
 *
 * Created by Martin Šícho on 3.10.14.
 */
public class Node {

    // members

    private Set<Node> connectedNodes;
    private Set<Graph> associatedGraphs;
    private int id;
    private int label;
    private static int nextId = Integer.MIN_VALUE;

    // constructors

    /**
     * Pokud vytvoříme vrchol bez popisku, defaultně se mu přiřadí popisek 0.
     *
     * @throws RuntimeException
     */
    public Node() throws RuntimeException {
        createFromLabel(nextId);
    }

    /**
     * Vytvoření vrcholu s daným popiskem.
     *
     * @param label popisek
     * @throws RuntimeException
     */
    public Node(int label) throws RuntimeException {
        createFromLabel(label);
    }

    // non-static methods

    /**
     * Metoda, která obaluje vytvoření nové instance z popisku.
     *
     * @param label popisek
     * @throws RuntimeException
     */
    private void createFromLabel(int label) throws RuntimeException {
        if (nextId + 1 == Integer.MIN_VALUE) {
            throw new RuntimeException("Counter overflow. Cannot create any new Nodes.");
        }
        this.id = nextId++;
        this.label = label;
        this.connectedNodes = new HashSet<>();
        this.associatedGraphs = new HashSet<>();
    }

    /**
     * Propojení dvou vrcholů. Přidání se automaticky projeví v obou vrcholech, protože
     * se předpokládá použití pouze v neorientovaném grafu.
     *
     * @param node vrchol, který chceme spojit s touto instancí
     */
    public void addNode(Node node) {
        if (!node.isConnectedTo(this)) {
            connectedNodes.add(node);
            node.addNode(this);
            return;
        }
        connectedNodes.add(node);
    }

    /**
     * Rozpojení dvou vrcholů. Změna se automaticky projeví v obou vrcholech, protože
     * se předpokládá použití pouze v neorientovaném grafu.
     *
     * @param node vrchol, který chceme odpojit
     */
    public void removeNode(Node node) {
        if (node.isConnectedTo(this)) {
            connectedNodes.remove(node);
            node.removeNode(this);
            return;
        }
        connectedNodes.remove(node);
    }

    /**
     * Vrátí počet vrcholů spojených s tímto vrcholem.
     *
     * @return počet vrcholů spojených s touto instancí
     */
    public int getNodeCount() {
        return connectedNodes.size();
    }

    /**
     * Vrátí všechny vrcholy spojené s tímto vrcholem jako {@link java.util.Set}.
     *
     * @return vrcholy propojené s touto instancí jako {@link java.util.Set}
     */
    public Set<Node> getConnectedNodes() {
        return new HashSet<>(connectedNodes);
    }

    /**
     * Zjistí, zda je tento vrchol spojen hranou s jiným vrcholem.
     *
     * @param node jiný vrchol grafu jako {@link Node}.
     * @return spojen/nespojen
     */
    public boolean isConnectedTo(Node node) {
        return connectedNodes.contains(node);
    }

    public void addToGraph(Graph graph) {
        if (!this.isInGraph(graph)) {
            associatedGraphs.add(graph);
            graph.addNode(this);
        }
    }

    public void removeFromGraph(Graph graph) {
        if (this.isInGraph(graph)) {
            associatedGraphs.remove(graph);
            graph.removeNode(this);
        }
    }

    public boolean isInGraph(Graph graph) {
        return associatedGraphs.contains(graph);
    }

    public int getGraphCount() {
        return associatedGraphs.size();
    }

    // overrides

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return this.id == node.getId();
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Node[order:%d]", connectedNodes.size());
    }

    // getters

    public int getId() {
        return id;
    }

    public int getLabel() {
        return label;
    }

    public Set<Graph> getAssociatedGraphs() {
        return associatedGraphs;
    }

    // setters

    public void setLabel(int label) {
        this.label = label;
    }
}