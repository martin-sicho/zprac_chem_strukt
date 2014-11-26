import unittest
from molecule.Molecule import Molecule

class SumarniVzorecTests(unittest.TestCase):

    def setUp(self):
        self.toluene = Molecule.readMolFile(open("testfiles/toluene.mol"))
        self.cocaine = Molecule.readMolFile(open("testfiles/cocaine.mol"))

    def testWriteDotty(self):
        outfile = open("toluene.dot", mode="w")
        self.toluene.writeDotty(outfile)

    def testWriteSVG(self):
        outfile = open("cocaine.svg", mode="w")
        self.cocaine.writeSVG(outfile)
        print self.cocaine

if __name__ == '__main__':
    unittest.main()


