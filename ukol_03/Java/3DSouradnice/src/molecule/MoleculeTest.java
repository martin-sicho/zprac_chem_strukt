package molecule;

import junit.framework.TestCase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class MoleculeTest extends TestCase {

    FileReader tolueneFile;
    FileReader cocaineFile;

    @Override
    protected void setUp() throws FileNotFoundException{
        tolueneFile = new FileReader("testfiles/toluene.mol");
        cocaineFile = new FileReader("testfiles/cocaine.mol");
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
}