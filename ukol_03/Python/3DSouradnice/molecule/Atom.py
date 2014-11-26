import Node


class Atom(Node.Node):
    _x = int()
    _y = int()
    _z = int()

    def __init__(self, x, y, z, name=""):
        Node.Node.__init__(self, name=name)
        self._x = x
        self._y = y
        self._z = z

    def __repr__(self):
        return "Atom[order:{0},name:{1}]".format(self.getNodeCount(), self.getName())

    def setCoords(self, x, y, z):
        self._x = x
        self._y = y
        self._z = z

    def getX(self):
        return self._x

    def getY(self):
        return self._y

    def getZ(self):
        return self._z

    def setX(self, x):
        self._x = x

    def setY(self, y):
        self._y = y

    def setZ(self, z):
        self._z = z