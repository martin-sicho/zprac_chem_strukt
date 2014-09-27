import unittest
from cz.vscht.pzchs.Atom import Atom
from cz.vscht.pzchs.SumarniVzorec import SumarniVzorec

class SumarniVzorecTests(unittest.TestCase):

    def setUp(self):
        self.atom_pocet = {
            'C': 6
            , 'H' : 12
            , 'O' : 6
            , 'Xe' : 1
            , 'Ar' : 1
        }
        self.vzorec = SumarniVzorec('C6H6O6H6XeAr')

    def testAtom(self):
        atom = Atom('Ca')
        self.assertEqual(atom.znacka, 'Ca', msg='Nepovedl se pristup ke znacce atomu')

    def testSumarniVzorecInvalidInput(self):
        invalid_inputs = ('', 'caOH', 'Ca(OH)2', '2HO')
        for bad_input in invalid_inputs:
            self.assertRaises(ValueError, SumarniVzorec, bad_input)

    def testSumarniVzorecVsechnyAtomy(self):
        mnozina = self.vzorec.vsechnyAtomy()
        for znacka in self.atom_pocet.keys():
            self.assertIn(Atom(znacka), mnozina)

    def testSumarniVzorecPocet(self):
        for znacka, pocet in self.atom_pocet.items():
            self.assertEqual(self.vzorec.pocet(znacka), pocet)

    def testSumarniVzorecExtend(self):
        self.vzorec.extend(self.vzorec)
        for znacka, pocet in self.atom_pocet.items():
            self.assertEqual(self.vzorec.pocet(znacka), 2 * pocet)

        vzorec = SumarniVzorec('Be')

        self.assertEqual(
            vzorec.extend(self.vzorec).pocet('Be')
            , 1
        )

        for znacka, pocet in self.atom_pocet.items():
            self.assertEqual(vzorec.pocet(znacka), 2 * pocet)

    def testSumarniVzorecRepr(self):
        self.assertEqual(repr(self.vzorec), 'C6H12ArO6Xe')

if __name__ == '__main__':
    unittest.main()