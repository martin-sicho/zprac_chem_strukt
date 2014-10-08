import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Třída reprezentující obecný neorientovaný graf.
 *
 * Created by Martin Šícho on 3.10.14.
 */
public class Graph {

    // members

    /**
     * vrcholy obsažené v tomto grafu
     */
    private Set<Node> nodeSet;
    /**
     * název grafu
     */
    private String label;

    // constructors

    /**
     * Vytvoří prázdný graf.
     */
    Graph() {
        nodeSet = new HashSet<>();
        this.label = "";
    }

    /**
     * Vytvoří prázdný graf s daným názvem.
     */
    Graph(String label) {
        nodeSet = new HashSet<>();
        this.label = label;
    }

    /**
     * Vytvoří graf z množiny vrcholů
     *
     * @param nodeSet vrcholy nového grafu
     */
    Graph(Set<Node> nodeSet) {
        this();
        for (Node node : nodeSet) {
            node.addToGraph(this);
        }
        this.nodeSet = new HashSet<>(nodeSet);
    }

    /**
     * Vytvoří nový graf z jiného grafu.
     *
     * @param graph graf, ze kterého se má vytvořit tento nový graf
     */
    Graph(Graph graph) {
        this();
        this.nodeSet = new HashSet<>(graph.nodeSet);
    }

    // static methods

    /**
     * Tovární metoda pro načtení grafu ze streamu ve formátu:
     *
     * {@code počet_vrcholů;konektivity},
     *
     * kde konektivity jsou ukončené čárkou a ve formátu: {@code číslo_vrcholu-číslo_vrcholu}.
     *
     * (např.: 6;1-2,2-3,3-4,4-5,5-6,6-1,).
     *
     * @param reader instance {@link java.io.Reader}
     * @return instance třídy {@link Graph}
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    public static Graph readGraph(Reader reader) throws java.io.IOException, java.text.ParseException {
        StringWriter input_writer = new StringWriter();

        int data = reader.read();
        while (data != -1) {
            input_writer.append((char) data);
            data = reader.read();
        }

        input_writer.flush();
        String input = input_writer.toString();

        Pattern pattern = Pattern.compile("^[0-9]+;([0-9]+-[0-9]+,)+$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new ParseException(String.format("'%s' is not a valid input", input), 0);
        }


        matcher = Pattern.compile("^([0-9]+);").matcher(input);
        matcher.find();
        int node_count = Integer.parseInt(matcher.group(1));
        Map<String, Node> node_map = new HashMap<>();
        for (int i = 1; i <= node_count; i++) {
            Node new_node = new Node(i);
            node_map.put(new_node.getLabel(), new_node);
        }
        matcher = Pattern.compile("([0-9])+-([0-9])+").matcher(input);

        while (matcher.find()) {
            String node1_label = matcher.group(1);
            String node2_label = matcher.group(2);
            node_map.get(node1_label).addNode(node_map.get(node2_label));
        }

        System.out.println( "Successfully read input: '" + input + "'");

        return new Graph(new HashSet<>(node_map.values()));
    }

    // non-static methods

    /**
     * Zapíše tento graf do instance {@link java.io.Writer} ve stejném formátu
     * jako používá {@link Graph#readGraph(java.io.Reader reader)} pro načtení grafu.
     *
     * @param writer instance {@link java.io.Writer}
     * @throws java.io.IOException
     */
    public void writeGraph(Writer writer) throws java.io.IOException {
        // TODO: vytvorit
    }

    /**
     * Přidá vrchol do tohoto grafu. Jeden vrchol může patřit do více grafů.
     * Jednotlivé grafy, ve kterých se vrchol vyskytuje, je možné získat pomocí
     * {@link Node#getAssociatedGraphs()}. Počet grafů lze získat zavoláním
     * {@link Node#getGraphCount()}.
     *
     * @param node instance třídy {@link Node} reprezentující přidávaný vrchol
     */
    public void addNode(Node node) {
        if (!node.isInGraph(this)) {
            node.addToGraph(this);
        }
        nodeSet.add(node);
    }

    /**
     * Odebere konkrétní vrchol z tohoto grafu.
     *
     * @param node vrchol, který chceme odstranit jako {@link Node}.
     */
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

    /**
     * Spojí tyto dva vrcholy hranou. Pokud obě nebo jedna z hran nepatří do tohoto grafu,
     * jsou do něj automaticky přidány.
     *
     * @param node1 vrchol1
     * @param node2 vrchol2
     */
    public void connectNodes(Node node1, Node node2) {
        node1.addToGraph(this);
        node2.addToGraph(this);
        node1.addNode(node2);
    }

    /**
     * Vrátí {@code true/false} podle toho, zda vrchol patří do tohoto grafu nebo ne.
     *
     * @param node vrchol
     * @return pravdivostní hodnota
     */
    public boolean hasNode(Node node) {
        return nodeSet.contains(node);
    }

    /**
     * Vrátí počet vrcholů pro tento graf.
     *
     * @return počet vrcholů
     */
    public long getNodeCount() {
        return nodeSet.size();
    }

    /**
     * Vrátí počet hran.
     *
     * @return počet hran
     */
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

    /**
     * Vrátí textovou reprezentaci grafu jako:
     * {@code Graph label[nodes:počet,edges:počet]}.
     *
     * @return stringová reprezentace grafu.
     */
    @Override
    public String toString() {
        if (label.equals("")) {
            return String.format("Graph[nodes:%d,edges:%d]", getNodeCount(), getEdgeCount());
        } else {
            return String.format("Graph %s[nodes:%d,edges:%d]", getLabel(), getNodeCount(), getEdgeCount());
        }
    }

    // getters & setters

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
