import sys
from Node import Node

def main(args):
    node = Node()
    node.addNode(node)


if __name__ == "__main__":
    main(sys.argv)