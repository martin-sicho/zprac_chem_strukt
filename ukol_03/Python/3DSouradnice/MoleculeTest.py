import unittest
from molecule.Molecule import Molecule

class SumarniVzorecTests(unittest.TestCase):

    def setUp(self):
        self.toluene = Molecule.readMolFile(open("testfiles/toluene.mol"))
        self.cocaine = Molecule.readMolFile(open("testfiles/cocaine.mol"))

    def testReadMolFile(self):
        pass

if __name__ == '__main__':
    unittest.main()


