import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Třída reprezentující obecný neorientovaný graf, kde
 * všechny hrany mají jednotkovou délku.
 *
 * Created by Martin Šícho on 3.10.14.
 */
public class Graph implements Comparator<Node> {

    // members

    /**
     * vrcholy obsažené v tomto grafu
     */
    private Set<Node> nodeSet;
    /**
     * název grafu
     */
    private String name;

    // constructors

    /**
     * Vytvoří prázdný graf.
     */
    Graph() {
        nodeSet = new HashSet<>();
        this.name = "";
    }

    /**
     * Vytvoří prázdný graf s daným názvem.
     */
    Graph(String name) {
        nodeSet = new HashSet<>();
        this.name = name;
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
            this.nodeSet.add(node);
        }
    }

    /**
     * Vytvoří nový graf z jiného grafu.
     *
     * @param graph graf, ze kterého se má vytvořit tento nový graf
     */
    Graph(Graph graph) {
        this();
        this.name = graph.getName() + "_clone";
        for (Node node : graph.getNodeSet()) {
            node.addToGraph(this);
            nodeSet.add(node);
        }
    }

    // static methods

    /**
     * Tovární metoda pro načtení grafu ze streamu ve formátu:
     *
     * {@code počet_vrcholů;konektivity},
     *
     * kde konektivity jsou ukončené čárkou a ve formátu: {@code číslo_vrcholu-číslo_vrcholu}
     * (např.: 6;1-2,2-3,3-4,4-5,5-6,6-1,).
     *
     * Čísla vrcholů jsou automaticky použitá jako jejich jména.
     *
     * @param reader instance {@link java.io.Reader}
     * @return instance třídy {@link Graph}
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    public static Graph readGraph(Reader reader) throws java.io.IOException, java.text.ParseException {
        StringWriter input_writer = new StringWriter();

        int data = reader.read();
        int last_data = data;
        while (data != -1) {
            input_writer.append((char) data);
            last_data = data;
            data = reader.read();
        }

        if ( (char) last_data != ',') {
            input_writer.append(',');
        }

        input_writer.flush();
        String input = input_writer.toString();
        input_writer.close();

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
            node_map.put(new_node.getName(), new_node);
        }
        matcher = Pattern.compile("([0-9]+)-([0-9]+)").matcher(input);

        while (matcher.find()) {
            String node1_label = matcher.group(1);
            String node2_label = matcher.group(2);
            if (!node_map.containsKey(node1_label) || !node_map.containsKey(node2_label)) {
                throw new ParseException(String.format("Label of node '%s' and/or '%s' is invalid.", node1_label, node2_label), 0);
            }
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
     * <br />
     * <em>Poznámka:</em> čísla jednotlivých vrcholů se generují automaticky, takže pokud byl
     * graf načten pomocí {@link Graph#readGraph(java.io.Reader reader)}, budou se
     * čísla vrcholů ve výstupu z této metody pravděpodobně lišit od těch původních.
     * Topologie grafu samozřejmě zůstává zachována.
     *
     * @param writer instance {@link java.io.Writer}
     * @throws java.io.IOException
     */
    public void writeGraph(Writer writer) throws java.io.IOException {
        Map<Node, Integer> node_label = new HashMap<>();
        int label_counter = 0;
        for (Node node : nodeSet) {
            node_label.put(node, ++label_counter);
        }

        writer.write(String.format("%d;", this.getNodeCount()));
        Set<Node> visited = new HashSet<>();
        for (Node node : nodeSet) {
            int label = node_label.get(node);
            visited.add(node);
            for (Node neighbor : this.getNeighbors(node)) {
                if (!visited.contains(neighbor)) {
                    int neigbor_label = node_label.get(neighbor);
                    writer.write(String.format("%d-%d,", label, neigbor_label));
                }
            }
        }
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
            for (Node neighbor : this.getNeighbors(node)) {
                if (!neighbor.equals(node)) {
                    ++edge_count;
                } else {
                    ++self_connected;
                }
            }
        }
        return edge_count / 2 + self_connected;
    }

    /**
     * Vrátí {@link java.util.Set} vrcholů, které jsou sousedy
     * daného vrcholu v tomto grafu.
     *
     * Pokud se daný vrchol nenachází v tomto grafu, vyhodí program výjimku
     * {@link java.lang.IllegalArgumentException}.
     *
     * @param node vstupní vrchol tohoto grafu
     * @return {@link java.util.Set} vrcholů, které jsou sousedy vstupn9ho vrcholu
     * @throws IllegalArgumentException indikuje, že vstupní vrchol se nenachází v tomto grafu
     */
    public Set<Node> getNeighbors(Node node) throws IllegalArgumentException {
        if (this.hasNode(node)) {
            Set<Node> neigbors = new HashSet<>();
            for (Node neighbor : node.getConnectedNodes()) {
                if (neighbor.isInGraph(this)) {
                    neigbors.add(neighbor);
                }
            }
            return neigbors;
        } else {
            throw new IllegalArgumentException( String.format("Node '%s' is not present in graph '%s'.", node.getName(), this.toString()) );
        }
    }

    /**
     * Vrátí celou komponentu tohoto grafu, která obsahuje {@code startNode},
     * jako novou instanci třídy {@link Graph}.
     *
     * @param startNode vrchol tohoto grafu, který má být obsažen v dané komponentě.
     * @return komponenta se {@code startNode} jako {@link Graph}
     * @throws IllegalArgumentException
     */
    public Graph getConnectedComponent(Node startNode) throws IllegalArgumentException {
        if (this.hasNode(startNode)) {
            Node current_node = startNode;
            Queue<Node> queue = new LinkedList<>();
            Set<Node> visited = new HashSet<>();

            queue.add(current_node);
            while (!queue.isEmpty()) {
                current_node = queue.poll();
                visited.add(current_node);
                Set<Node> neighbors = this.getNeighbors(current_node);
                for (Node neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }

            return new Graph(visited);
        } else {
            throw new IllegalArgumentException( String.format("Node '%s' is not present in graph '%s'.", startNode.getName(), this.toString()) );
        }
    }

    /**
     * Metoda vrací všechny komponenty obsažené v tomto grafu
     * jako {@link java.util.Set}.
     *
     * @return včechny komponenty grafu
     */
    public Set<Graph> getConnectedComponents() {
        long all_nodes_count = this.getNodeCount();
        long component_nodes_count = 0;
        Set<Graph> found_components = new HashSet<>();
        Set<Node> not_visited = new HashSet<>(nodeSet);

        while (all_nodes_count != component_nodes_count) {
            Graph component = getConnectedComponent(not_visited.iterator().next());
            not_visited.removeAll(component.getNodeSet());
            found_components.add(component);
            component_nodes_count += component.getNodeCount();
        }
        return found_components;
    }

    /**
     * Metoda vrací počet všech komponent obsažených v tomto grafu.
     *
     * @return počet komponent grafu
     */
    public int getConnectedComponentsCount() {
        return getConnectedComponents().size();
    }

    /**
     * Vrátí minimální vzdálenosti všech vrcholů od {@code startNode} pro tento graf.
     *
     * @param startNode počáteční vrchol, ze kterého budu počítat vzdálenosti
     * @return {@link java.util.Map} vrcholů a jejich minimální vzdálenosti od {@code startNode}
     */
    public Map<Node,Integer> labelByDistanceFrom(Node startNode) {
        if (this.hasNode(startNode)) {
            Map<Node, Integer> node_distance = new HashMap<>();
            PriorityQueue<Node> to_visit = new PriorityQueue<>(1, this);
            Set<Node> visited = new HashSet<>();

            for (Node node : nodeSet) {
                node.setLabelInGraph(this, Integer.MAX_VALUE);
            }

            startNode.setLabelInGraph(this, 0);
            node_distance.put(startNode, 0);
            to_visit.add(startNode);
            while (!to_visit.isEmpty()) {
                Node current = to_visit.poll();
                visited.add(current);

                for (Node neighbor : this.getNeighbors(current)) {
                    int g = current.getLabelInGraph(this) + 1;
                    if (g < neighbor.getLabelInGraph(this) && !visited.contains(neighbor)) {
                        neighbor.setLabelInGraph(this, g);
                        node_distance.put(neighbor, g);
                        to_visit.add(neighbor);
                    }
                }
            }

            return node_distance;
        } else {
            throw new IllegalArgumentException( String.format("Node '%s' is not present in graph '%s'.", startNode.getName(), this.toString()) );
        }
    }

    /**
     * Vrátí minimální vzdálenost mezi dvěma vrcholy grafu.
     *
     * Pokud tyto vrcholy nejsou spojené hranou
     * vrátí hodnotu {@link java.lang.Integer#MAX_VALUE}.
     *
     * @param node1 první vrchol
     * @param node2 druhý vrchol
     * @return vzdálenost prvního a druhého vrcholu
     */
    public int getNodeDistance(Node node1, Node node2) {
        Map<Node, Integer> labeled = this.labelByDistanceFrom(node1);
        if (labeled.containsKey(node2)) {
            return labeled.get(node2);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Vrací počet cyklů (chordless cycles) v tomto grafu.
     *
     * @return počet cyklů (chordless cycles) v tomto grafu
     */
    public int getNumberOfCycles() {
        int cycles_count = 0;
        for (Graph component : this.getConnectedComponents()) {
            Queue<Node> to_visit = new LinkedList<>();
            Set<Node> white_nodes = new HashSet<>(component.getNodeSet());
            Set<Node> grey_nodes = new HashSet<>();
            Map<Node,Node> node_parent = new HashMap<>();

            to_visit.add(white_nodes.iterator().next());
            while (!to_visit.isEmpty()) {
                Node current = to_visit.poll();
                for (Node neighbor : component.getNeighbors(current)) {
                    if (white_nodes.contains(neighbor)) {
                        node_parent.put(neighbor, current);
                        white_nodes.remove(neighbor);
                        grey_nodes.add(neighbor);
                        to_visit.add(neighbor);
                    } else if (grey_nodes.contains(neighbor) && node_parent.get(neighbor) != current) {
                        ++cycles_count;
                    }
                }
                white_nodes.remove(current); // If the node is neither white nor grey, it is black.
                grey_nodes.remove(current);
            }
        }

        return cycles_count;
    }

    // overrides

    /**
     * Vrcholy se porovnávají na základě jejich číselného popisku v grafu.
     * Defaultně je tento popisek nastaven na {@link java.lang.Integer#MAX_VALUE}.
     *
     * @param node1 první vrchol
     * @param node2 druhý vrchol
     * @return kladné číslo, pokud popisek druhého vrcholu je větší, než hodnota prvního;
     * nula, pokud jsou oba stejné; záporné číslo, pokud první popisek je menší, než druhý
     */
    @Override
    public int compare(Node node1, Node node2) {
        if (this.hasNode(node1) && this.hasNode(node2)) {
            Integer label1 = node1.getLabelInGraph(this);
            Integer label2 = node2.getLabelInGraph(this);
            return label1.compareTo(label2);
        } else {
            throw new IllegalArgumentException( String.format("Node '%s' or '%s' is not present in graph '%s'.", node1.getName(), node2.getName(), this.toString()) );
        }
    }

    /**
     * Vrátí textovou reprezentaci grafu jako:
     * {@code Graph name[nodes:počet,edges:počet]}.
     *
     * @return stringová reprezentace grafu.
     */
    @Override
    public String toString() {
        if (name.equals("")) {
            return String.format("Graph[nodes:%d,edges:%d]", getNodeCount(), getEdgeCount());
        } else {
            return String.format("Graph %s[nodes:%d,edges:%d]", getName(), getNodeCount(), getEdgeCount());
        }
    }

    // getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Vrací všechny vrcholy grafu jako {@link java.util.Set}.
     *
     * @return kompletní množina všech vrcholů této instance
     */
    public Set<Node> getNodeSet() {
        return new HashSet<>(nodeSet);
    }
}
