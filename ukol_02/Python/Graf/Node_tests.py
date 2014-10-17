import unittest

from classes.Node import Node


class NodeTests(unittest.TestCase):

    def setUp(self):
        self.someNodes = [Node(name=x) for x in range(10, 21)]

    def testAddNode(self):
        pass



if __name__ == '__main__':
    unittest.main()