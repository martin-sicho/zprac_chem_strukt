import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GraphTest {

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
        Graph graph1 = new Graph();
        Graph graph2 = new Graph(graph1);
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);

        assertTrue(graph1.getNodeCount() == 0);
        assertTrue(graph2.getEdgeCount() == 0);

        graph1.addNode(node1);
        graph2.addNode(node2);
        assertTrue(node1.getGraphCount() == 1
                        && node1.getAssociatedGraphs().contains(graph1)
                        && !node1.getAssociatedGraphs().contains(graph2)
        );

        graph1.addNode(node3);
        assertTrue(graph1.getNodeCount() == 2);
        graph1.connectNodes(node1, node3);
        assertTrue(graph1.getEdgeCount() == 1);
        graph1.connectNodes(node2, node3);
        assertTrue(node1.getGraphCount() == 1);
        assertTrue(node2.getGraphCount() == 2);
        assertTrue(node3.getGraphCount() == 1);
        assertTrue(graph1.getEdgeCount() == 2);
        node1.addNode(node1);
        graph1.connectNodes(node1, node2);
        assertTrue(graph1.getEdgeCount() == 4);
    }

    @Test
    public void testRemoveNode() throws Exception {
        Graph graph1 = new Graph();
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);

        graph1.addNode(node1);
        graph1.addNode(node2);
        Graph graph2 = new Graph(graph1);
        graph2.addNode(node3);

        assertTrue(graph1.getNodeCount() == 2);
        assertTrue(graph2.getNodeCount() == 3);

        graph2.connectNodes(node1, node2);
        graph2.connectNodes(node1, node1);
        graph2.connectNodes(node2, node3);
        assertTrue(graph2.getEdgeCount() == 3);
        graph2.removeNode(node2);
        assertTrue(graph2.getEdgeCount() == 1);

        // druha nezavisla baraz testu

        Node node4 = someNodes.get(4);
        Node node5 = someNodes.get(5);
        Node node6 = someNodes.get(6);
        node4.addNode(node5);
        node5.addNode(node6);
        Graph graph4 = new Graph();
        Graph graph5 = new Graph();
        graph4.addNode(node4);
        graph4.addNode(node5);
        graph5.addNode(node5);
        graph5.addNode(node6);
        assertTrue(graph4.getEdgeCount() == 1);
        assertTrue(graph4.getNodeCount() == 2);
        graph4.removeNode(node5);
        assertTrue(!graph4.hasNode(node5));
        assertTrue(graph4.getNodeCount() == 1);
        assertTrue(graph4.getEdgeCount() == 0);
        assertTrue(graph5.hasNode(node5));
        assertTrue(graph5.getEdgeCount() == 1);
    }

    @Test
    public void testToString() throws Exception {
        Graph graph1 = new Graph();
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);

        graph1.addNode(node1);
        graph1.addNode(node2);

        graph1.connectNodes(node1, node2);
        graph1.connectNodes(node1, node1);
        assertTrue(graph1.toString().equals("Graph[nodes:2,edges:2]"));
    }
}