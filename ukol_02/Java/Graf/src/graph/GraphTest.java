package graph;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.*;

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
    public void testAddNode() {
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
    public void testRemoveNode() {
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
    public void testToString() {
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(someNodes.get(0));
        nodeSet.add(someNodes.get(1));

        Graph graph1 = new Graph(nodeSet);

        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);

        graph1.connectNodes(node1, node2);
        graph1.connectNodes(node1, node1);
        assertTrue(graph1.toString().equals("graph.Graph[nodes:2,edges:2]"));
        Graph graph2 = new Graph("muj_graf");
        assertTrue(graph2.toString().equals("graph.Graph muj_graf[nodes:0,edges:0]"));
        graph1.setName("muj_graf2");
        assertTrue(graph1.toString().equals("graph.Graph muj_graf2[nodes:2,edges:2]"));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testGetNeigbors() {
        Graph graph = new Graph();
        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        Node node3 = someNodes.get(2);
        Node node4 = someNodes.get(3);
        node2.addNode(node1);
        node2.addNode(node3);
        node2.addNode(node4);
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);

        assertTrue(graph.getNeighbors(node2).size() == 2);
        for (Node node : graph.getNeighbors(node2)) {
            assertTrue(node == node1 || node == node3);
        }

        Node node5 = someNodes.get(4);
        graph.getNeighbors(node5);
    }

    @Test
    public void testReadGraph() {
        Reader reader = new StringReader("6;1-2,2-3,3-4,4-5,5-6,6-1,");
        Graph graph = null;
        try {
            graph = Graph.readGraph(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertTrue(graph.getEdgeCount() == 6 && graph.getNodeCount() == 6);

    }

    @Test
    public void testWriteGraph() {
        boolean first = true;
        Node first_node = null;
        for (Node node : someNodes.subList(0, 5)) {
            if (first) {
                first = false;
                first_node = node;
                continue;
            }
            node.addNode(first_node);
        }
        Graph graph = new Graph(new HashSet<>(someNodes.subList(0, 5)));

        StringWriter string_writer = new StringWriter();
        try {
            graph.writeGraph(string_writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("Writing representation: %s", string_writer.toString()));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testGetComponents() {
        Reader reader = new StringReader("6;1-2,2-3,3-4,4-5,5-6,6-1");
        Graph graph = null;
        try {
            graph = Graph.readGraph(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Node disconected = someNodes.get(0);
        graph.addNode(disconected);

        Set<Graph> components = graph.getConnectedComponents();
        assertTrue(components.size() == 2);
        assertTrue(graph.getConnectedComponentsCount() == 2);

        Node some_node_from_graph = graph.getNodeSet().iterator().next();
        some_node_from_graph.addNode(someNodes.get(1));
        Graph component = graph.getConnectedComponent(some_node_from_graph);
        assertTrue(!component.hasNode(someNodes.get(1)));

        graph.getConnectedComponent(someNodes.get(2));
    }

    @Test
    public void testLabelByDistance() {
        Reader reader = new StringReader("6;1-2,2-3,3-4,4-5,5-6,6-1,");
        Graph graph = null;
        try {
            graph = Graph.readGraph(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Node start_node = someNodes.get(0);
        Node disconnected = someNodes.get(1);
        Node connected = graph.getNodeSet().iterator().next();
        graph.connectNodes(connected, start_node);
        graph.addNode(disconnected);

        Map<Node, Integer> labeling = graph.labelByDistanceFrom(start_node);
        assertTrue(labeling.get(start_node) == 0 && labeling.size() == 7);
        assertTrue(!labeling.containsKey(disconnected));

        assertTrue(graph.getNodeDistance(start_node, disconnected) == Integer.MAX_VALUE);
        assertTrue(graph.getNodeDistance(start_node, connected) == 1);
    }

    @Test
    public void testGetNumberOfCycles() {
        Reader reader = new StringReader("10;1-2,2-2,2-3,3-4,2-4,2-5,5-6,6-6,5-7,5-8,7-8,7-9,8-10,9-10,");
        Graph graph = null;
        try {
            graph = Graph.readGraph(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertTrue(graph.getNumberOfCycles() == 5);

        Node node1 = someNodes.get(0);
        Node node2 = someNodes.get(1);
        graph.connectNodes(node1, node2);
        assertTrue(graph.getNumberOfCycles() == 5);
        Node node3 = someNodes.get(3);
        graph.connectNodes(node1, node3);
        graph.connectNodes(node2, node3);
        assertTrue(graph.getNumberOfCycles() == 6);
    }

}