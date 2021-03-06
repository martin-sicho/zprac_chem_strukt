import Graph

def type_check(method):
    """
    Dekorator kontrolujici zda jsou prvni dva parametry
    metody stejneho typu.

    :param method: kontrolovana metoda
    :return:
    """

    def check_type(self, node2, *args, **kwargs):
        if type(self) != type(node2):
            raise TypeError("{0} is not a valid object of type {1}.".format(node2, type(self)))
        return method(self, node2, *args, **kwargs)

    return check_type

def graph_type_check(method):
    """
    Dekorator kontrolujici zda je druhy parametr
    metody typu Graph.

    :param method: kontrolovana metoda
    :return:
    """

    def check_type(self, graph, *args, **kwargs):
        if not isinstance(graph, Graph.Graph):
            raise TypeError("{0} is not a valid object of type Graph.".format(graph))
        return method(self, graph, *args, **kwargs)

    return check_type

class Node:

    def __init__(self, **kwargs):
        self._connectedNodes = set()
        self._assoiciatedGraphs = dict()
        self._name = ""
        if kwargs.has_key("name"):
            self._name = str(kwargs["name"])
        else:
            self._name = hash(self)

    @type_check
    def addNode(self, other):
        neco = other.isConnectedTo(self)
        if not neco:
            self._connectedNodes.add(other)
            other.addNode(self)
            return
        self._connectedNodes.add(other)

    @type_check
    def removeNode(self, other):
        if other.isConnectedTo(self):
            self._connectedNodes.remove(other)
            other.removeNode(self)
            return
        self._connectedNodes.discard(other)

    @type_check
    def isConnectedTo(self, other):
        return other in self._connectedNodes

    def getNodeCount(self):
        return len(self._connectedNodes)

    def getConnectedNodes(self):
        return set(self._connectedNodes)

    @graph_type_check
    def addToGraph(self, graph):
        if not self.isInGraph(graph):
            self._assoiciatedGraphs[graph] = hash(self)
            graph.addNode(self)

    @graph_type_check
    def removeFromGraph(self, graph):
        if self.isInGraph(graph):
            self._assoiciatedGraphs.pop(graph)
            graph.removeNode(self)

    @graph_type_check
    def isInGraph(self, graph):
        return self._assoiciatedGraphs.has_key(graph)

    def getGraphCount(self):
        return len(self._assoiciatedGraphs)

    @graph_type_check
    def getLabelInGraph(self, graph):
        if not self.isInGraph(graph):
            raise LookupError("{0} does not belong to {1}.".format(self, graph))
        return self._assoiciatedGraphs[graph]

    @graph_type_check
    def setLabelInGraph(self, graph, label):
        if not self.isInGraph(graph):
            raise LookupError("{0} does not belong to {1}.".format(self, graph))
        self._assoiciatedGraphs[graph] = label

    def __repr__(self):
        return "Node[order:{0},name:{1}]".format(self.getNodeCount(), self.getName())

    def getName(self):
        return self._name

    def getAssociatedGraphs(self):
        return self._assoiciatedGraphs.keys()

    def setName(self, name):
        self._name = str(name)






