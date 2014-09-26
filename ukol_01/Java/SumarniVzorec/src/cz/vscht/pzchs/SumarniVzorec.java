package cz.vscht.pzchs;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tahle třída implementuje sumární vzorce jako
 * {@link java.util.HashMap}, kde jako klíče jsou objekty třídy
 * {@link cz.vscht.pzchs.Atom} a hodnoty jsou počty jejich výskytu
 * v molekule. Nad touto datovou strukturou pak pracují jednotlivé metody.
 *
 * Created by Martin Šícho on 26.9.14.
 */
public class SumarniVzorec {
    private Map<Atom,Integer> atomPocet;

    /**
     * Konstruktor, který vezme vzorec od uživatele
     * a pokusí se ho uložit.
     *
     * @param vzorec vzorec jako {@link java.lang.String}
     * @throws IllegalArgumentException
     */
    public SumarniVzorec(String vzorec) throws IllegalArgumentException {
        if (SumarniVzorec.inputIsValid(vzorec)) {
            Pattern pattern = Pattern.compile("([A-Z][a-z]*)([1-9]*)");
            Matcher matcher = pattern.matcher(vzorec);

            boolean found = false;
            atomPocet = new TreeMap<>();
            while (matcher.find()) {
//                System.out.println(matcher.group(1));
//                System.out.println(matcher.group(2));
                Atom atom = new Atom(matcher.group(1));
                int vyskyty;
                if (!matcher.group(2).equals("")) {
                    vyskyty = Integer.parseInt(matcher.group(2));
                } else {
                    vyskyty = 1;
                }

                if (!atomPocet.containsKey(atom)) {
                    atomPocet.put(atom, vyskyty);
                } else {
                    atomPocet.put(atom, atomPocet.get(atom) + vyskyty);
                }

                found = true;
            }
            if(!found){
                throw new IllegalArgumentException( String.format("Vzorec '%s' ma spatny format.", vzorec) );
            }
        } else {
            throw new IllegalArgumentException( String.format("'%s' neni validni sumarni vzorec.", vzorec) );
        }
    }

    // staticke metody

    /**
     * Implementace validace vstupu od uživatele.
     *
     * @param input data od uživatele
     * @return {@code true} jestli ok, {@code false}, když ne
     */
    private static boolean inputIsValid(String input) {
        if (!input.isEmpty()) {
            String pattern = "^[A-Z][A-Za-z0-9]*$";
            return input.matches(pattern);
        } else {
            return false;
        }
    }

    // public metody

    /**
     * Vraci jednotlivé atomy jako množinu.
     *
     * @return {@link java.util.Set} atomů
     */
    public Set<Atom> vsechnyAtomy() {
        return atomPocet.keySet();
    }

    /**
     * Vrací počet atomů daného prvku.
     *
     * @param znacka znacka prvku
     * @return
     */
    public int pocet(String znacka) {
        Integer pocet = atomPocet.get(new Atom(znacka));
        if ( pocet != null) {
            return pocet;
        } else {
            return 0;
        }
    }

    /**
     * "Přičte" atomy vzorce z parametru k této instanci.
     *
     * @param vzorec vzorec jehož atomy se přičtou k této instanci
     * @return vrací vlastní instanci po rozšíření
     */
    public SumarniVzorec extend(SumarniVzorec vzorec) {
        for (Atom atom : vzorec.vsechnyAtomy()) {
            if (this.atomPocet.containsKey(atom)) {
                atomPocet.put(atom, atomPocet.get(atom) + vzorec.pocet(atom.getZnacka()));
            } else {
                atomPocet.put(atom, vzorec.pocet(atom.getZnacka()));
            }
        }
        return this;
    }

    /**
     * Vrátí vzorec v Hillově zápisu.
     *
     * @return
     */
    @Override
    public String toString() {
        Integer pocet_uhliku = this.pocet("C");
        Integer pocet_vodiku = this.pocet("H");
        StringWriter output = new StringWriter();
        if (pocet_uhliku > 0) output.write("C" + pocet_uhliku.toString());
        if (pocet_vodiku > 0) output.write("H" + pocet_vodiku.toString());

        for (Atom atom : atomPocet.keySet()) {
            String znacka = atom.getZnacka();
            if (!znacka.equals("C") && !znacka.equals("H")) {
                output.write(znacka);
                int pocet = this.pocet(atom.getZnacka());
                if ( pocet > 1) {
                    output.write( ((Integer) pocet).toString() );
                }
            }
        }

        return output.toString();
    }

}
