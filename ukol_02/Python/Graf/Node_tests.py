import unittest

from classes.Node import Node
from classes.Graph import Graph


class NodeTests(unittest.TestCase):

    def setUp(self):
        self.someNodes = [Node(name=x) for x in range(10, 21)]

    def testAddNode(self):
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]

        node1.addNode(node2)
        self.assertTrue(len(node1.getConnectedNodes()) == len(node2.getConnectedNodes()) == 1)
        node1.addNode(node1)
        self.assertTrue(node1.isConnectedTo(node1))

        node1.addNode(node2)
        node1.addNode(node1)
        node1.addNode(node2)
        self.assertTrue(node1.getNodeCount() == 2)

    def testRemoveNode(self):
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node3 = self.someNodes[2]

        node1.addNode(node2)
        node1.addNode(node3)
        node2.addNode(node3)

        node1.removeNode(node3)

        self.assertTrue(not node1.isConnectedTo(node3))
        self.assertTrue(not node3.isConnectedTo(node1))
        self.assertTrue(node2.isConnectedTo(node3))

        node1.addNode(node1)
        self.assertTrue(node1.isConnectedTo(node1))
        node1.removeNode(node1)
        self.assertTrue(not node1.isConnectedTo(node1))

    def testGetNodeCount(self):
        node1 = self.someNodes[0]
        for node in self.someNodes:
            node1.addNode(node)
        self.assertTrue(node1.getNodeCount() == len(self.someNodes))

    def testRepr(self):
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node1.setName("A")
        node2.setName("B")

        node1.addNode(node2)
        node1.addNode(node1)

        self.assertTrue(repr(node1) == "Node[order:2,name:A]")
        self.assertTrue(repr(node2) == "Node[order:1,name:B]")

    def testGetName(self):
        node = Node()
        self.assertTrue(hash(node) == node.getName())
        node = Node(name=5)
        self.assertTrue( node.getName() == "5")

    def testSetName(self):
        node = Node()
        node.setName(5)
        self.assertTrue( node.getName() == "5")

    def testGetConnectedNodes(self):
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node3 = self.someNodes[2]
        node1.addNode(node2)
        node1.addNode(node3)

        nodes = node1.getConnectedNodes()

        self.assertTrue(node2 in nodes)
        self.assertTrue(node3 in nodes)
        self.assertTrue(len(nodes) == 2)

        sneaky = Node()
        nodes.add(sneaky)
        self.assertTrue(sneaky not in node1.getConnectedNodes())

    def testAddToGraph(self):
        graph1 = Graph()
        graph2 = Graph()
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]

        self.assertTrue(graph1.getNodeCount() == 0)
        self.assertTrue(graph2.getEdgeCount() == 0)

        node1.addToGraph(graph1)
        node2.addToGraph(graph2)
        self.assertTrue(node1.getGraphCount() == 1 \
                and graph1 in node1.getAssociatedGraphs() \
                and graph2 not in node1.getAssociatedGraphs()
        )

    def testRemoveFromGraph(self):
        graph1 = Graph()
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node3 = self.someNodes[2]

        graph1.addNode(node1)
        graph1.addNode(node2)
        graph1.connectNodes(node1, node2)

        node3.addNode(node2)

        self.assertTrue(graph1.getNodeCount() == 2 and graph1.getEdgeCount() == 1)
        node3.removeFromGraph(graph1)
        self.assertTrue(graph1.getNodeCount() == 2 and graph1.getEdgeCount() == 1)
        node3.addToGraph(graph1)
        self.assertTrue(graph1.getNodeCount() == 3 and graph1.getEdgeCount() == 2)
        node2.removeFromGraph(graph1)
        self.assertTrue(graph1.getNodeCount() == 2 and graph1.getEdgeCount() == 0)

if __name__ == '__main__':
    unittest.main()