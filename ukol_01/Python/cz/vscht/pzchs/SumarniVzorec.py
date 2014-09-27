import re
from Atom import Atom

class SumarniVzorec:

    def __init__(self, vzorec):
        self.__atomPocet = dict()

        if SumarniVzorec.inputIsValid(vzorec):
            pattern = re.compile(r'([A-Z][a-z]*)([1-9]*)')
            found = False
            for match in pattern.finditer(vzorec):
                found = True
                vyskyty = 0
                if not match.groups()[1]:
                    vyskyty = 1
                else:
                    vyskyty = int(match.groups()[1])
                znacka = match.groups()[0]
                atom = Atom(znacka)

                if self.__atomPocet.has_key(atom):
                    puvodni_pocet = self.__atomPocet[atom]
                    self.__atomPocet[atom] = puvodni_pocet + vyskyty
                else:
                    self.__atomPocet[atom] = vyskyty

            if not found:
                raise ValueError("Vzorec '{0}' ma spatny format.".format(vzorec))
        else:
            raise ValueError("Vzorec '{0}' neni validni sumarni vzorec.".format(vzorec))

    @staticmethod
    def inputIsValid(vzorec):
        pattern = re.compile(r'^[A-Z][A-Za-z0-9]*$')
        return pattern.match(vzorec)

    def vsechnyAtomy(self):
        return set(self.__atomPocet.keys())

    def pocet(self, znacka):
        if self.__atomPocet[Atom(znacka)]:
            return self.__atomPocet[Atom(znacka)]
        else:
            return 0

    def extend(self, vzorec):
        for atom in vzorec.vsechnyAtomy():
            if self.__atomPocet[atom]:
                puvodni_pocet = self.__atomPocet[atom]
                self.__atomPocet[atom] = puvodni_pocet + vzorec.pocet(atom.znacka)
            else:
                self.__atomPocet[atom] = vzorec.pocet(atom.znacka)
        return self

    def __repr__(self):
        pocet_uhliku = self.pocet("C")
        pocet_vodiku = self.pocet("H")
        output = "C{0}H{1}".format(pocet_uhliku, pocet_vodiku)
        sorted_dict = sorted(self.__atomPocet.items(), key=lambda (k, v): k.znacka)
        for key, value in sorted_dict:
            if key.znacka != "C" and key.znacka != "H":
                pocet = self.__atomPocet[key]
                output += key.znacka
                if pocet > 1:
                    output += str(pocet)
        return output