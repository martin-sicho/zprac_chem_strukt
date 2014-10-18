from Queue import Queue, PriorityQueue
import sys

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
        return method(self, node, *args, **kwargs)

    return check_type

def two_nodes_type_check(method):
    """
    Dekorator kontrolujici zda jsou
    druhe dva parametry typu Node.

    :param method: kontrolovana metoda
    :return:
    """

    def check_type(self, node1, node2, *args, **kwargs):
        if not isinstance(node1, Node.Node) or not isinstance(node2, Node.Node):
            raise TypeError("{0} and/or {1} is not a valid object of type Node.".format(node1, node2))
        return method(self, node1, node2, *args, **kwargs)

    return check_type

class Graph:

    MAX_DISTANCE = 999999999

    def __init__(self, **kwargs):
        self._nodeSet = set()
        self._name = ""
        if kwargs.has_key("graph"):
            self._name = kwargs["graph"].getName() + "_clone"
            for item in kwargs["graph"].getNodeSet():
                item.addToGraph(self)
                self._nodeSet.add(item)

        if kwargs.has_key("name"):
            self._name = kwargs["name"]
        if kwargs.has_key("nodes"):
            for item in kwargs["nodes"]:
                item.addToGraph(self)
                self._nodeSet.add(item)


    @staticmethod
    def readGraph(istream):
        if not istream.closed:
            contents = ''
            try:
                contents = istream.readline()
                node_count = int(contents.split(';')[0])
                connectivity = contents.split(';')[1].split(',')
                new_nodes = { str(x): Node.Node(name=x) for x in range(1, node_count + 1)}
                for connection in connectivity:
                    if connection != '':
                        connected_nodes = connection.split('-')
                        new_nodes[connected_nodes[0]].addNode(new_nodes[connected_nodes[1]])
                print "cessfully read input: '{0}'".format(contents)
                return Graph(nodes=new_nodes.values())
            except Exception as exp:
                sys.stderr.write(repr(exp) + '\n')
                raise IOError("Parsing of input ({0}) FAILED.".format(contents))
        else:
            raise IOError('Input stream must be opened.')

    # non-static methods

    def writeGraph(self, ostream):
        node_label = dict()
        label_counter = 0
        for node in self._nodeSet:
            label_counter+=1
            node_label[node] = label_counter

        ostream.write(u'{0};'.format(self.getNodeCount()))
        visited = set()
        for node in self._nodeSet:
            label = node_label[node]
            visited.add(node)
            for neighbor in self.getNeighbors(node):
                if neighbor not in visited:
                    neighbor_label = node_label[neighbor]
                    ostream.write(u'{0}-{1},'.format(label, neighbor_label))

    @node_type_check
    def addNode(self, node):
        if not node.isInGraph(self):
            node.addToGraph(self)
        self._nodeSet.add(node)

    @node_type_check
    def removeNode(self, node):
        if node.isInGraph(self):
            for neighbor in node.getConnectedNodes():
                if neighbor in self._nodeSet \
                and neighbor.isInGraph(self) \
                and neighbor.getGraphCount() == 1:
                    neighbor.removeNode(node)
            node.removeFromGraph(self)
        self._nodeSet.discard(node)

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

    @node_type_check
    def getConnectedComponent(self, startNode):
        if self.hasNode(startNode):
            current_node = startNode
            queue = Queue()
            visited = set()

            queue.put(current_node)
            while not queue.empty():
                current_node = queue.get()
                visited.add(current_node)
                neigbors = self.getNeighbors(current_node)
                for neighbor in neigbors:
                    if neighbor not in visited:
                        queue.put(neighbor)

            return Graph(nodes=visited)
        else:
            raise LookupError("Node '{0}' is not present in graph '{1}'.".format(startNode, self))

    def getConnectedComponents(self):
        all_nodes_count = self.getNodeCount()
        component_nodes_count = 0
        found_components = set()
        not_visited = set(self._nodeSet)

        while all_nodes_count != component_nodes_count:
            component = self.getConnectedComponent(not_visited.pop())
            not_visited.difference_update(component.getNodeSet())
            found_components.add(component)
            component_nodes_count += component.getNodeCount()

        return found_components

    def getConnectedComponentsCount(self):
        return len(self.getConnectedComponents())

    @node_type_check
    def labelByDistanceFrom(self, startNode):
        if self.hasNode(startNode):
            node_distance = dict()
            to_visit = PriorityQueue()
            visited = set()

            for node in self._nodeSet:
                node.setLabelInGraph(self, Graph.MAX_DISTANCE)

            startNode.setLabelInGraph(self, 0)
            node_distance[startNode] = 0
            to_visit.put( (0, startNode) )
            while not to_visit.empty():
                current = to_visit.get()[1]
                visited.add(current)

                for neighbor in self.getNeighbors(current):
                    g = current.getLabelInGraph(self) + 1
                    if g < neighbor.getLabelInGraph(self) and neighbor not in visited:
                        neighbor.setLabelInGraph(self, g)
                        node_distance[neighbor] = g
                        to_visit.put( (g, neighbor) )

            return node_distance

        else:
            raise LookupError("Node '{0}' is not present in graph '{1}'.".format(startNode, self))

    @two_nodes_type_check
    def getNodeDistance(self, node1, node2):
        labeled = self.labelByDistanceFrom(node1)
        if labeled.has_key(node2):
            return labeled[node2]
        else:
            return Graph.MAX_DISTANCE

    def getNumberOfCycles(self):
        cycles_count = 0
        for component in self.getConnectedComponents():
            to_visit = set()
            white_nodes = set(component.getNodeSet())
            grey_nodes = set()
            node_parent = dict()

            to_visit.add(component.getNodeSet().pop())
            while not len(to_visit) == 0:
                current = to_visit.pop()
                for neighbor in component.getNeighbors(current):
                    if neighbor in white_nodes:
                        if neighbor == current:
                            cycles_count+=1
                            continue
                        node_parent[neighbor] = current
                        white_nodes.discard(neighbor)
                        grey_nodes.add(neighbor)
                        to_visit.add(neighbor)
                    elif neighbor in grey_nodes and node_parent[neighbor] != current:
                        cycles_count+=1
                white_nodes.discard(current) # If the node is neither white nor grey, it is black.
                grey_nodes.discard(current)

        return cycles_count

    # overrides

    def __repr__(self):
        if self._name == "":
            return "Graph[nodes:{0},edges:{1}]".format(self.getNodeCount(), self.getEdgeCount())
        else:
            return "Graph {0}[nodes:{1},edges:{2}]".format(self.getName(), self.getNodeCount(), self.getEdgeCount())


    # getters

    def getName(self):
        return self._name

    def setName(self, name):
        self._name = name

    def getNodeSet(self):
        return set(self._nodeSet)