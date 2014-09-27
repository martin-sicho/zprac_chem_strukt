

class Atom:

    def __init__(self, znacka):
        self.znacka = znacka

    def __eq__(self,other):
        return self.znacka.__hash__() == other.znacka.__hash__()


    def  __hash__(self):
        return self.znacka.__hash__()

    def __repr__(self):
        return "Atom: " + self.znacka
