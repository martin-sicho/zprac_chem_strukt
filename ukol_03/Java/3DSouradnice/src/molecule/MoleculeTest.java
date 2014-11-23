package molecule;

import junit.framework.TestCase;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;

public class MoleculeTest extends TestCase {

    FileReader tolueneFile;
    FileReader cocaineFile;
    FileReader trimetHexFile;
    FileReader dimetPentFile;

    @Override
    protected void setUp() throws FileNotFoundException{
        tolueneFile = new FileReader("testfiles/toluene.mol");
        cocaineFile = new FileReader("testfiles/cocaine.mol");
        trimetHexFile = new FileReader("testfiles/3-methyl_hexane.mol");
        dimetPentFile = new FileReader("testfiles/2,4-dimethylpentan.sdf");
    }

    public void testReadMolecule() throws IOException, ParseException {
        Molecule toluene = Molecule.readMolFile(tolueneFile);
        Molecule cocaine = Molecule.readMolFile(cocaineFile);

        assertTrue(toluene.getAtomCount() == 15);
        assertTrue(toluene.getBondCount() == 15);

        assertTrue(cocaine.getAtomCount() == 43);
        assertTrue(cocaine.getBondCount() == 45);

        StringReader reader = new StringReader("fgfgfg\ndfgfdg");
        boolean check = false;
        try {
            Molecule.readMolFile(reader);
        } catch (ParseException exp) {
            check = true;
        }
        assertTrue(check);

        FileReader cocaine_invalid = new FileReader("testfiles/cocaine_invalid.mol");
        check = false;
        try {
            Molecule.readMolFile(cocaine_invalid);
        } catch (ParseException exp) {
            check = true;
        }
        assertTrue(check);

    }

    public void testEstimateLeadingEigenvalue() throws Exception {
        Molecule trimetHex = Molecule.readMolFile(trimetHexFile);
        boolean[][] matrix = trimetHex.getAdjecencyMatrix();

        int n = matrix[0].length;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                int value = 0;
                if (matrix[x][y]) {
                    value = 1;
                }
                System.out.print(value);
            }
            System.out.print('\n');
        }

        double[] initial_vector = {1, 1, 1, 1, 1, 1, 1};
        System.out.println(trimetHex.estimateLeadingEigenvalue(initial_vector));


    }

    public void testWriteDotty() throws Exception {
        Molecule dimetPent = Molecule.readMolFile(dimetPentFile);
        Molecule etPent = Molecule.readMolFile(new FileReader("testfiles/3-ethylpentan.sdf"));
        Writer file = new FileWriter("testfiles/dimetPent.dot");
        dimetPent.writeDotty(file);
        file = new FileWriter("testfiles/3-ethylpentan.dot");
        etPent.writeDotty(file);

        // homework
        double[] initial_vector = new double[ (int) etPent.getAtomCount()];
        Arrays.fill(initial_vector, 1.0);
        System.out.println("3-ethylpentan: " + etPent.estimateLeadingEigenvalue(initial_vector));

        initial_vector = new double[ (int) dimetPent.getAtomCount()];
        Arrays.fill(initial_vector, 1.0);
        System.out.println("2,4-dimethylpentan: " + dimetPent.estimateLeadingEigenvalue(initial_vector));


    }
}