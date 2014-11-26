import Graph, Atom, re, sys
import xml.etree.ElementTree as etree


class Molecule(Graph.Graph):

    def __init__(self, atoms, name=""):
        Graph.Graph.__init__(self, nodes=atoms, name=name)

    @staticmethod
    def readMolFile(istream):
        if not istream.closed:
            line = ""
            try:
                line_number = 0
                coords_row_pattern = re.compile(r"^(\s{0,4}-?\d+\.\d+){3}\s[A-Za-z]+(\s+\d)+\s+$")
                connectivity_row_pattern = re.compile(r"^(\s{0,2}\d)+\s+$")
                found_coords = False
                found_connections = False
                name = str()
                atom_list = list()

                while line.strip() != "M  END":
                    line = istream.readline()
                    line_number += 1

                    if line_number == 1:
                        name = line.strip()
                        continue

                    if coords_row_pattern.search(line):
                        found_coords = True
                        coords = list()
                        coords_pattern = re.compile(r"\s{0,4}(-?\d+\.\d+)")
                        for match in coords_pattern.finditer(line):
                            value = match.groups()[0]
                            coords.append(float(value))

                        atom_details_pattern = re.compile(r"\s([A-Za-z]+)(\s+\d)+\s+$")
                        for match in atom_details_pattern.finditer(line):
                            name = match.groups()[0]
                            pass

                        atom_list.append(Atom.Atom(coords[0], coords[1], coords[2], name=name))

                    if connectivity_row_pattern.search(line) and found_coords:
                        found_connections = True
                        connect_pattern = re.compile(r"^(\s{0,2}\d+)(\s{0,2}\d+)")
                        for match in connect_pattern.finditer(line):
                            atom1 = int(match.groups()[0]) - 1
                            atom2 = int(match.groups()[1]) - 1
                            if len(atom_list) > max(atom1, atom2):
                                atom_list[atom1].addNode(atom_list[atom2])
                            else:
                                raise RuntimeError("Invalid connectivity at line: {0}".format(line_number))

                if not found_connections or not found_coords:
                    raise RuntimeError("Molfile parsing failed. It seems the input file is not a valid Molfile. Line: {0}".format(line_number))

                return Molecule(atom_list, name=name)
            except Exception as exp:
                sys.stderr.write(repr(exp) + '\n')
                raise exp
        else:
            raise IOError('Input stream must be opened.')

    def writeDotty(self, ostream):
        if not ostream.closed:
            visited = set()
            ostream.write("graph " + self.getName() + " {\n")
            counter = 0
            for node in self.getNodeSet():
                counter+=1
                node.setLabelInGraph(self, counter)
                ostream.write(str(node.getLabelInGraph(self)) + "[label=\"" + node.getName() + "\"];\n")

            for node in self.getNodeSet():
                visited.add(node)
                for neighbor in self.getNeighbors(node):
                    if neighbor not in visited:
                        ostream.write(str(node.getLabelInGraph(self)) + " -- " + str(neighbor.getLabelInGraph(self)) + ";\n")
            ostream.write("}\n")
        else:
            raise IOError('Output stream must be opened.')

    def writeSVG(self, ostream):
        if not ostream.closed:
            scale = 100.0
            svg = etree.Element(
                "svg"
                , attrib={
                    "width" : "100%"
                    , "height" : "100%"
                    , "viewBox" : "500 -500 1000 1000"
                    , "version" : "1.1"
                    , "xmlns" : "http://www.w3.org/2000/svg"
                }
            )

            visited = set()
            for node in self.getNodeSet():
                visited.add(node)
                for neighbor in self.getNeighbors(node):
                    if neighbor not in visited:
                        line = etree.Element(
                            "line"
                            , attrib={
                                "x1" : str(scale * node.getX())
                                , "y1" : str(scale * node.getY())
                                , "x2" : str(scale * neighbor.getX())
                                , "y2" : str(scale * neighbor.getY())
                                , "style" : "stroke:rgb(0,0,0);stroke-width:2"
                            }
                        )
                        svg.append(line)

            ostream.write(etree.tostring(svg, encoding="utf-8"))
        else:
            raise IOError('Output stream must be opened.')

    def __repr__(self):
        return "molecule.Molecule {0}[atoms:{1},bonds:{2}]".format(self.getName(), self.getNodeCount(), self.getEdgeCount())