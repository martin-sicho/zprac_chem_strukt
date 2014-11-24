import sys
from molecule.Atom import Atom


def main(args):
    atom = Atom(1,2,3, name="C")
    print atom

if __name__ == "__main__":
    main(sys.argv)


