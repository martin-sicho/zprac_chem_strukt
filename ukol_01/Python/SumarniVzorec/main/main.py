import sys
from cz.vscht.pzchs.SumarniVzorec import SumarniVzorec


def main(args):
    vzorec = SumarniVzorec("C6H6O6H6XeAr")
    print vzorec

if __name__ == "__main__":
    main(sys.argv)


