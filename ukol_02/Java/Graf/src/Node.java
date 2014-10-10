import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Třída reprezentující vrchol obecného neorientovaného grafu.
 *
 * Created by Martin Šícho on 3.10.14.
 */
public class Node {

    // members

    /**
     * všechny vrcholy spojené hranou s tímto vrcholem
     */
    private Set<Node> connectedNodes;
    /**
     * všechny grafy, které obsahují tento vrchol, včetně číselného
     * označení tohoto vrcholu v jednotlivých grafech
     */
    private Map<Graph, Integer> associatedGraphs;
    /**
     * jednoznačný identifikátor vrcholu
     */
    private int id;
    /**
     * označení volitelné uživatelem (nemusí být unikátní)
     */
    private String name;
    private static int nextId = Integer.MIN_VALUE;

    // constructors

    /**
     * Pokud vytvoříme vrchol bez popisku, defaultně se mu přiřadí
     * jeho id jako popisek.
     *
     * @throws RuntimeException v případě chyby vyhodí výjimku
     */
    public Node() throws RuntimeException {
        this( nextId );
    }

    /**
     * Vytvoření vrcholu s daným popiskem.
     *
     * @param name popisek
     * @throws RuntimeException v případě chyby vyhodí výjimku
     */
    public Node(String name) throws RuntimeException {
        createFromLabel(name);
    }

    /**
     * Vytvoření vrcholu s daným číselným popiskem.
     *
     * @param label popisek
     * @throws RuntimeException v případě chyby vyhodí výjimku
     */
    public Node(int label) throws RuntimeException {
        this( ((Integer) label).toString() );
    }

    // non-static methods

    /**
     * Metoda, která obaluje vytvoření nové instance z popisku.
     *
     * @param label popisek
     * @throws RuntimeException v případě chyby vyhodí výjimku
     */
    private void createFromLabel(String label) throws RuntimeException {
        if (nextId + 1 == Integer.MIN_VALUE) {
            throw new RuntimeException("Counter overflow. Cannot create any new Nodes.");
        }
        this.id = nextId++;
        this.name = label;
        this.connectedNodes = new HashSet<>();
        this.associatedGraphs = new HashMap<>();
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
            associatedGraphs.put(graph, Integer.MAX_VALUE);
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
        return associatedGraphs.containsKey(graph);
    }

    public int getGraphCount() {
        return associatedGraphs.size();
    }

    /**
     * Vrátí číselný popisek tohoto vrcholu v konkrétním grafu.
     * Defaultně je tento popisek nastaven na {@link java.lang.Integer#MAX_VALUE}.
     *
     * @param graph graf
     * @return číselný popisek tohoto vrcholu v grafu
     */
    public int getLabelInGraph(Graph graph) {
        graph.addNode(this);
        return associatedGraphs.get(graph);
    }

    /**
     * Vrátí číselný popisek tohoto vrcholu v konkrétním grafu.
     * Defaultně je tento popisek nastaven na {@link java.lang.Integer#MAX_VALUE}.
     *
     * Pokud se vrchol nenachází v daném grafu, je do něj automaticky přiřazen.
     *
     * @param graph graf
     */
    public void setLabelInGraph(Graph graph, int label) {
        associatedGraphs.put(graph, label);
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
        return String.format("Node[order:%d,name:%s]", connectedNodes.size(), getName());
    }

    // getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Graph> getAssociatedGraphs() {
        return associatedGraphs.keySet();
    }

    // setters

    public void setName(String name) {
        this.name = name;
    }
}
