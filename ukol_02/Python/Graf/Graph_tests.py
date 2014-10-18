from io import StringIO
import unittest
import os

from classes.Node import Node
from classes.Graph import Graph


class GraphTests(unittest.TestCase):

    def setUp(self):
        self.someNodes = [Node(name=x) for x in range(10, 21)]
        self.testdir = 'test_files'
        if not os.path.exists(self.testdir + '/'):
            os.mkdir(self.testdir)
            path = os.path.join(self.testdir, 'input.txt')
            with open(path, mode='w') as infile:
                infile.write('6;1-2,2-3,3-4,4-5,5-6,6-1,')

    def testAddNode(self):
        graph1 = Graph()
        graph2 = Graph(graph=graph1)
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node3 = self.someNodes[2]

        self.assertTrue(graph1.getNodeCount() == 0)
        self.assertTrue(graph2.getEdgeCount() == 0)

        graph1.addNode(node1)
        graph2.addNode(node2)
        self.assertTrue(node1.getGraphCount() == 1 \
                        and graph1 in node1.getAssociatedGraphs() \
                        and graph2 not in node1.getAssociatedGraphs()
        )

        graph1.addNode(node3)
        self.assertTrue(graph1.getNodeCount() == 2)
        graph1.connectNodes(node1, node3)
        self.assertTrue(graph1.getEdgeCount() == 1)
        graph1.connectNodes(node2, node3)
        self.assertTrue(node1.getGraphCount() == 1)
        self.assertTrue(node2.getGraphCount() == 2)
        self.assertTrue(node3.getGraphCount() == 1)
        self.assertTrue(graph1.getEdgeCount() == 2)
        node1.addNode(node1)
        graph1.connectNodes(node1, node2)
        self.assertTrue(graph1.getEdgeCount() == 4)

    def testRemoveNode(self):
        graph1 = Graph()
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node3 = self.someNodes[2]

        graph1.addNode(node1)
        graph1.addNode(node2)
        graph2 = Graph(graph=graph1)
        graph2.addNode(node3)

        self.assertTrue(graph1.getNodeCount() == 2)
        self.assertTrue(graph2.getNodeCount() == 3)

        graph2.connectNodes(node1, node2)
        graph2.connectNodes(node1, node1)
        graph2.connectNodes(node2, node3)
        self.assertTrue(graph2.getEdgeCount() == 3)
        graph2.removeNode(node2)
        self.assertTrue(graph2.getEdgeCount() == 1)

        # druha nezavisla baraz testu

        node4 = self.someNodes[4]
        node5 = self.someNodes[5]
        node6 = self.someNodes[6]
        node4.addNode(node5)
        node5.addNode(node6)
        graph4 = Graph()
        graph5 = Graph()
        graph4.addNode(node4)
        graph4.addNode(node5)
        graph5.addNode(node5)
        graph5.addNode(node6)
        self.assertTrue(graph4.getEdgeCount() == 1)
        self.assertTrue(graph4.getNodeCount() == 2)
        graph4.removeNode(node5)
        self.assertTrue( not graph4.hasNode(node5))
        self.assertTrue(graph4.getNodeCount() == 1)
        self.assertTrue(graph4.getEdgeCount() == 0)
        self.assertTrue(graph5.hasNode(node5))
        self.assertTrue(graph5.getEdgeCount() == 1)

    def testRepr(self):
        nodeSet = {self.someNodes[0], self.someNodes[1]}

        graph1 = Graph(nodes=nodeSet)

        node1 = self.someNodes[0]
        node2 = self.someNodes[1]

        graph1.connectNodes(node1, node2)
        graph1.connectNodes(node1, node1)
        self.assertTrue(repr(graph1) == "Graph[nodes:2,edges:2]")
        graph2 = Graph(name="muj_graf")
        self.assertTrue(repr(graph2) == "Graph muj_graf[nodes:0,edges:0]")
        graph1.setName("muj_graf2")
        self.assertTrue(repr(graph1) == "Graph muj_graf2[nodes:2,edges:2]")

    def testGetNeighbors(self):
        graph = Graph()
        node1 = self.someNodes[0]
        node2 = self.someNodes[1]
        node3 = self.someNodes[2]
        node4 = self.someNodes[3]
        node2.addNode(node1)
        node2.addNode(node3)
        node2.addNode(node4)
        graph.addNode(node1)
        graph.addNode(node2)
        graph.addNode(node3)

        self.assertTrue(len(graph.getNeighbors(node2)) == 2)
        for node in graph.getNeighbors(node2):
            self.assertTrue(node == node1 or node == node3)


        node5 = self.someNodes[4]
        self.assertRaises(LookupError, graph.getNeighbors, node5)

    def testReadGraph(self):
        graph1 = Graph()
        path = os.path.join(self.testdir, 'input.txt')
        with open(path, mode='r') as infile:
            print "Reading representation from file: '{0}'".format(path)
            graph1 = Graph.readGraph(infile)
        self.assertTrue(graph1.getEdgeCount() == 6 and graph1.getNodeCount() == 6)

        reader = StringIO(u'6;1-2,2-3,3-4,4-5,5-6,6-1')
        graph2 = Graph.readGraph(reader)
        self.assertTrue(graph2.getEdgeCount() == 6 and graph2.getNodeCount() == 6)

    def testWriteGraph(self):
        first = True
        first_node = Node()
        for node in self.someNodes[0:6]:
            if first:
                first = False
                first_node = node
                continue
            node.addNode(first_node)
        graph = Graph(nodes=self.someNodes[0:6])

        writer = StringIO()
        graph.writeGraph(writer)
        print "Writing representation: '{0}'".format(writer.getvalue())

        path = os.path.join(self.testdir, 'output.txt')
        with open(path, mode='w') as outfile:
            print "Writing representation to file: '{0}'".format(path)
            graph.writeGraph(outfile)



if __name__ == '__main__':
    unittest.main()