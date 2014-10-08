import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class NodeTest {

    private List<Node> someNodes;

    @Before
    public void setUp() {
        someNodes = new ArrayList<>();
        for (int i = 10; i <= 20; i++) {
            someNodes.add(new Node(i));
        }
    }

    @Test
    public void testAddNode() throws Exception {
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        node1.addNode(node2);
        assertTrue(node1.getConnectedNodes().size() == node2.getConnectedNodes().size());
        assertTrue(node1.getConnectedNodes().size() == 1);
        node1.addNode(node1);
        assertTrue(node1.isConnectedTo(node1));

        node1.addNode(node2);
        node1.addNode(node1);
        node1.addNode(node2);
        assertTrue(node1.getNodeCount() == 2);
    }

    @Test
    public void testRemoveNode() {
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);
        node1.addNode(node2);
        node1.addNode(node3);
        node2.addNode(node3);

        node1.removeNode(node3);

        assertTrue(!node1.isConnectedTo(node3));
        assertTrue(!node3.isConnectedTo(node1));
        assertTrue(node2.isConnectedTo(node3));

        node1.addNode(node1);
        assertTrue(node1.isConnectedTo(node1));
        node1.removeNode(node1);
        assertTrue(!node1.isConnectedTo(node1));
    }

    @Test
    public void testGetNodeCount() {
        Node node1 = someNodes.get(0);
        for (Node node : someNodes) {
            node1.addNode(node);
        }
        assertTrue(node1.getNodeCount() == someNodes.size());
    }

    @Test
    public void testHashCode() throws Exception {
        Node node1 = new Node(1);
        Node node2 = new Node(1);
        assertTrue(node1.hashCode() != node2.hashCode());
    }

    @Test
    public void testEquals() throws Exception {
        Node node1 = new Node(1);
        Node node2 = new Node(1);
        assertTrue(!node1.equals(node2));
        node1 = someNodes.get(0);
        assertEquals(node1, someNodes.get(0));
    }

    @Test
    public void testToString() throws Exception {
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);
        node1.setLabel("A");
        node2.setLabel("B");
        node3.setLabel("C");

        node1.addNode(node2);
        node1.addNode(node3);
        node2.addNode(node3);

        assertTrue(node1.toString().equals("Node[order:2,label:A]"));
        assertTrue(node2.toString().equals("Node[order:2,label:B]"));
        assertTrue(node3.toString().equals("Node[order:2,label:C]"));
    }

    @Test
    public void testGetId() throws Exception {
        Node node = someNodes.get(0);
        assertTrue(node.getId() == node.hashCode());
    }

    @Test
    public void testGetLabel() throws Exception {
        Node node = new Node();
        assertTrue(node.getLabel().equals( ((Integer) node.hashCode()).toString() ));
        node = new Node(5);
        assertTrue( node.getLabel().equals("5"));
    }

    @Test
    public void testSetLabel() throws Exception {
        Node node = new Node();
        node.setLabel("5");
        assertTrue(node.getLabel().equals("5"));
    }

    @Test
    public void testGetConnectedNodes() throws Exception {
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);
        node1.addNode(node2);
        node1.addNode(node3);

        Set<Node> nodes = node1.getConnectedNodes();

        assertTrue(nodes.contains(node2));
        assertTrue(nodes.contains(node3));
        assertTrue(nodes.size() == 2);

        Node sneaky = new Node();
        nodes.add(sneaky);
        assertTrue(!node1.getConnectedNodes().contains(sneaky));
    }

    @Test
    public void testAddToGraph() {
        Graph graph1 = new Graph();
        Graph graph2 = new Graph();
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);

        assertTrue(graph1.getNodeCount() == 0);
        assertTrue(graph2.getEdgeCount() == 0);

        node1.addToGraph(graph1);
        node2.addToGraph(graph2);
        assertTrue(node1.getGraphCount() == 1
                && node1.getAssociatedGraphs().contains(graph1)
                && !node1.getAssociatedGraphs().contains(graph2)
        );
    }

    @Test
    public void removeFromGraph() {
        Graph graph1 = new Graph();
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);

        graph1.addNode(node1);
        graph1.addNode(node2);
        graph1.connectNodes(node1, node2);

        node3.addNode(node2);

        assertTrue(graph1.getNodeCount() == 2 && graph1.getEdgeCount() == 1);
        node3.removeFromGraph(graph1);
        assertTrue(graph1.getNodeCount() == 2 && graph1.getEdgeCount() == 1);
        node3.addToGraph(graph1);
        assertTrue(graph1.getNodeCount() == 3 && graph1.getEdgeCount() == 2);
        node2.removeFromGraph(graph1);
        assertTrue(graph1.getNodeCount() == 2 && graph1.getEdgeCount() == 0);
    }

}