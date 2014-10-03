package cz.vscht.pzchs;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AtomTest {

    private Atom atom1_Cl;
    private Atom atom2_C;

    @Before
    public void setUp() {
        atom1_Cl = new Atom("Cl");
        atom2_C = new Atom("C");
    }

    @Test
    public void testHashCode() {
        assertTrue(atom1_Cl.hashCode() != atom2_C.hashCode());
        Atom carbon = new Atom("C");
        assertTrue(carbon.hashCode() == atom2_C.hashCode());
    }

    @Test
    public void testEquals() {
        assertTrue(!atom1_Cl.equals(atom2_C));
        Atom carbon = new Atom("C");
        assertTrue(carbon.equals(atom2_C));
    }

    @Test
    public void testCompareTo() {
        assertTrue(atom1_Cl.compareTo(atom2_C) > 0);
        Atom carbon = new Atom("C");
        assertTrue(carbon.compareTo(atom2_C) == 0);
        assertTrue(atom2_C.compareTo(atom1_Cl) < 0);
    }

    @Test (expected = ClassCastException.class)
    public void testCompareToException() {
        int integer = 10;
        atom1_Cl.compareTo(integer);
    }

    @Test
    public void testGetZnacka() {
        assertTrue(atom1_Cl.getZnacka().equals("Cl"));
    }
}