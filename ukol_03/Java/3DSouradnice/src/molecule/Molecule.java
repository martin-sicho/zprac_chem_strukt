package molecule;

import graph.Graph;
import graph.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Martin Šícho on 7.11.14.
 */
public class Molecule extends Graph {

    // constructors

//    public Molecule(Collection<Atom> atoms) {
//        super();
//        for (Atom atom : atoms) {
//            this.addNode(atom);
//        }
//    }

    public Molecule(String name, Collection<Atom> atoms) {
        super(name);
        for (Atom atom : atoms) {
            this.addNode(atom);
        }
    }

    // static methods

    public static Molecule readMolFile(Reader reader) throws IOException, ParseException {
        StringWriter input_writer = new StringWriter();
        int line_number = 0;
        Pattern coords_row_pattern = Pattern.compile("^(\\s{0,4}-?\\d+\\.\\d+){3}\\s[A-Za-z]+(\\s+\\d)+\\s+$");
        Pattern connectivity_row_pattern = Pattern.compile("^(\\s{0,2}\\d)+\\s+$");
        boolean found_coords = false;
        boolean found_connections = false;
        String name = "";
        double[] coords = new double[3];
        List<Atom> atom_list = new ArrayList<>();

        int data = reader.read();
        while (data != -1) {
            char character = (char) data;
            input_writer.append(character);
            if (character == '\n') {
                ++line_number;
                input_writer.flush();
                String input = input_writer.toString();

                if (input.equals("M  END\n")) {
                    break;
                }

                if (line_number == 1) {
                    name = input.trim();
                    continue;
                }

                Matcher coords_matcher = coords_row_pattern.matcher(input);
                if (coords_matcher.matches()) {
                    found_coords = true;
                    Pattern coords_pattern = Pattern.compile("\\s{0,4}(-?\\d+\\.\\d+)");
                    coords_matcher = coords_pattern.matcher(input);

                    short coord_idx = 0;
                    while (coords_matcher.find()) {
                        String coord = coords_matcher.group(1);
                        coords[coord_idx] = Double.parseDouble(coord);
                        coord_idx++;
                    }

                    Pattern atom_details_pattern = Pattern.compile("\\s([A-Za-z]+)(\\s+\\d)+\\s+$");
                    coords_matcher = atom_details_pattern.matcher(input);

                    String chem_symbol = "";
                    if (coords_matcher.find()) {
                        chem_symbol = coords_matcher.group(1);
                    }
                    atom_list.add(new Atom(chem_symbol, coords));
                }

                Matcher connectivity_matcher = connectivity_row_pattern.matcher(input);
                if (connectivity_matcher.matches() && found_coords) {
                    found_connections = true;
                    Pattern coords_pattern = Pattern.compile("^(\\s{0,2}\\d+)(\\s{0,2}\\d+)");
                    connectivity_matcher = coords_pattern.matcher(input);

                    if (connectivity_matcher.find()) {
                        int atom1 = Integer.parseInt(connectivity_matcher.group(1).trim()) - 1;
                        int atom2 = Integer.parseInt(connectivity_matcher.group(2).trim()) - 1;
                        if (atom_list.size() > (atom1 > atom2 ? atom1 : atom2)) {
                            atom_list.get(atom1).addNode(atom_list.get(atom2));
                        } else {
                            throw new ParseException(String.format("Invalid connectivity at line: %d", line_number), line_number);
                        }
                    }
                }

                input_writer = new StringWriter();
            }
            data = reader.read();
        }

        if (!found_connections || !found_coords) {
            throw new ParseException("Molfile parsing failed. It seems the input file is not a valid Molfile.", line_number);
        }

        input_writer.close();
        return new Molecule(name, atom_list);
    }

    // non-static methods

    public void writeDotty(Writer writer) throws IOException {
        Set<Node> visited = new HashSet<>();
        writer.write("graph " + getName() + " {\n");
        int counter = 0;
        for (Node node : getNodeSet()) {
            node.setLabelInGraph(this, ++counter);
            writer.write(node.getLabelInGraph(this) + "[label=\"" + node.getName() + "\"];\n");
        }

        for (Node node : getNodeSet()) {
            visited.add(node);
            for (Node neighbor : getNeighbors(node)) {
                if (!visited.contains(neighbor)) {
                    writer.write(node.getLabelInGraph(this) + " -- " + neighbor.getLabelInGraph(this) + ";\n");
                }
            }
        }
        writer.write("}\n");
        writer.flush();
    }

    public void writeSVG(Writer writer) throws ParserConfigurationException, TransformerException, FileNotFoundException {
        Double scale = 100.0;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        document.setXmlStandalone(false);

        Element svg = document.createElement("svg");
        document.appendChild(svg);

        //Set some attributes on the root node that are
        // required for proper rendering.
        svg.setAttribute("width","100%");
        svg.setAttribute("height","100%");
        svg.setAttribute("viewBox","500 -500 1000 1000");
        svg.setAttribute("version","1.1");
        svg.setAttribute("xmlns","http://www.w3.org/2000/svg");

        Set<Node> visited = new HashSet<>();
        for (Node node : getNodeSet()) {
            Atom atom = (Atom) node;
            visited.add(node);
            for (Node neighbor : getNeighbors(node)) {
                Atom atom_neigbor = (Atom) neighbor;
                if (!visited.contains(neighbor)) {
                    Element line  = document.createElement("line");
                    line.setAttribute("x1", Double.toString(scale * atom.getX()));
                    line.setAttribute("y1", Double.toString(scale * atom.getY()));
                    line.setAttribute("x2", Double.toString(scale * atom_neigbor.getX()));
                    line.setAttribute("y2", Double.toString(scale * atom_neigbor.getY()));
                    line.setAttribute("style","stroke:rgb(0,0,0);stroke-width:2");
                    svg.appendChild(line);
                }
            }

        }


        //Get a TransformerFactory object.
        TransformerFactory xformFactory = TransformerFactory.newInstance();

        //Get an XSL Transformer object.
        Transformer transformer = xformFactory.newTransformer();

        //This statement is new to this lesson.  It sets
        // the standalone property in the XML declaration,
        // which appears as the first line of the output
        // file.
        transformer.setOutputProperty(OutputKeys.STANDALONE,"no");

        //Get a DOMSource object that represents the
        // Document object
        DOMSource source = new DOMSource(document);

        //Get a StreamResult object that points to the
        // screen. Then transform the DOM sending XML to
        // the screen.
        //StreamResult scrResult = new StreamResult(System.out);
        //transformer.transform(source, scrResult);

        //Get a StreamResult object that points to the
        // output file.  Then transform the DOM sending XML
        // to the file
        StreamResult fileResult = new StreamResult(writer);
        transformer.transform(source,fileResult);

    }

    public long getAtomCount() {
        return getNodeCount();
    }

    public long getBondCount() {
        return getEdgeCount();
    }

    // overrides

    @Override
    public String toString() {
        return String.format("molecule.Molecule %s[nodes:%d,edges:%d]", getName(), getNodeCount(), getEdgeCount());
    }
}
