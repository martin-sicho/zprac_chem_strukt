import Node

def node_type_check(method):
    """
    Dekorator kontrolujici zda je druhy parametr
    metody typu Node.

    :param method: kontrolovana metoda
    :return:
    """

    def check_type(self, node, *args, **kwargs):
        if not isinstance(node, Node.Node):
            raise AttributeError("{0} is not a valid object of type Node.".format(node))
        method(self, node, *args, **kwargs)

    return check_type

def two_nodes_type_check(method):
    """
    Dekorator kontrolujici zda jsou
    druhe dva parametry typu Node.

    :param method: kontrolovana metoda
    :return:
    """

    def check_type(self, node1, node2, *args, **kwargs):
        if isinstance(node1, Node.Node) and isinstance(node2, Node.Node):
            raise TypeError("{0} and/or {1} is not a valid object of type Node.".format(node1, node2))
        method(self, node1, node2, *args, **kwargs)

    return check_type

class Graph:

    def __init__(self, **kwargs):
        self._nodeSet = set()
        self._name = ""
        if kwargs["name"]:
            self._name = kwargs["name"]
        if kwargs["nodes"]:
            for item in kwargs["nodes"]:
                item.addToGraph(self)
                self._nodeSet.add(item)

    @staticmethod
    def readGraph(self, istream):
        pass

    def writeGraph(self, ostream):
        pass

    @node_type_check
    def addNode(self, node):
        if not node.isInGraph(self):
            node.addToGraph(self)
        self._nodeSet.add(node)

    @node_type_check
    def removeNode(self, node):
        if not node.isInGraph(self):
            for neighbor in node.getConnectedNodes():
                if neighbor in self._nodeSet \
                and neighbor.isInGraph(self) \
                and neighbor.getGraphCount() == 1:
                    neighbor.removeNode(node)
            node.removeFromGraph(self)
        self._nodeSet.remove(node)

    @two_nodes_type_check
    def connectNodes(self, node1, node2):
        node1.addToGraph(self)
        node2.addToGraph(self)
        node1.addNode(node2)

    @node_type_check
    def hasNode(self, node):
        return node in self._nodeSet

    def getNodeCount(self):
        return len(self._nodeSet)

    @node_type_check
    def getNeighbors(self, node):
        if self.hasNode(node):
            neighbors = set()
            for neighbor in node.getConnectedNodes():
                if neighbor.isInGraph(self):
                    neighbors.add(neighbor)
            return neighbors
        else:
            raise LookupError("Node '{0}' is not present in graph '{1}'.".format(node, self))

    def getEdgeCount(self):
        edge_count = 0
        self_connected = 0
        for node in self._nodeSet:
            for neighbor in self.getNeighbors(node):
                if neighbor != node:
                    edge_count+=1
                else:
                    self_connected+=1
        return edge_count / 2 + self_connected

    def __repr__(self):
        if self._name == "":
            return "Graph[nodes:{0},edges:{1}]".format(self.getNodeCount(), self.getEdgeCount())
        else:
            return "Graph {0}[nodes:{1},edges:{2}]".format(self.getName(), self.getNodeCount(), self.getEdgeCount())

    def getName(self):
        return self._name

    def setName(self, name):
        self._name = name

    def getNodeSet(self):
        return set(self._nodeSet)