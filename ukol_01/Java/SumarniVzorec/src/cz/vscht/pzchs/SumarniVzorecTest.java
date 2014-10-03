package cz.vscht.pzchs;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class SumarniVzorecTest {

    private SumarniVzorec vzorec;
    private Map<String, Integer> atom_pocet;

    @Before
    public void setUp() {
        vzorec = new SumarniVzorec("C6H6O6H6XeAr");
        atom_pocet = new HashMap<>();
        atom_pocet.put("C", 6);
        atom_pocet.put("H", 12);
        atom_pocet.put("O", 6);
        atom_pocet.put("Xe", 1);
        atom_pocet.put("Ar", 1);
    }

    @Test
    public void testInvalidInput() {
        String[] invalid_inputs = {"", "caOH", "Ca(OH)2", "2HO"};
        for (String input : invalid_inputs) {
            try {
                SumarniVzorec vzorec = new SumarniVzorec(input);
            } catch (IllegalArgumentException exp) {
                assertTrue(
                        exp.getMessage().contains("ma spatny format")
                        || exp.getMessage().contains("neni validni sumarni vzorec")
                );
            }
        }
    }

    @Test
    public void testVsechnyAtomy() {
        Set<Atom> mnozina = vzorec.vsechnyAtomy();
        for (String znacka : atom_pocet.keySet()) {
            assertTrue(mnozina.contains(new Atom(znacka)));
        }
    }

    @Test
    public void testPocet() {
        for (Map.Entry entry : atom_pocet.entrySet()) {
            String znacka = (String) entry.getKey();
            int pocet = (int) entry.getValue();
            assertTrue(vzorec.pocet(znacka) == pocet);
        }
    }

    @Test
    public void testExtend() {
        vzorec.extend(vzorec);
        for (Map.Entry entry : atom_pocet.entrySet()) {
            String znacka = (String) entry.getKey();
            int pocet = (int) entry.getValue();
            assertTrue(vzorec.pocet(znacka) == 2 * pocet);
        }

        SumarniVzorec baCl2 = new SumarniVzorec("BaCl2");

        assertTrue(baCl2.extend(vzorec).pocet("Cl") == 2);
        assertTrue(baCl2.pocet("Ba") == 1);
        for (Map.Entry entry : atom_pocet.entrySet()) {
            String znacka = (String) entry.getKey();
            int pocet = (int) entry.getValue();
            assertTrue(baCl2.pocet(znacka) == 2 * pocet);
        }
    }

    @Test
    public void testToString() {
        assertTrue(vzorec.toString().equals("C6H12ArO6Xe"));
    }
}