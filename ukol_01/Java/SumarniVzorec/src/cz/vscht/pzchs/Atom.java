package cz.vscht.pzchs;

import com.sun.corba.se.impl.io.TypeMismatchException;

import java.util.Comparator;

/**
 * Prostě atom :)
 *
 * Created by Martin Šícho on 26.9.14.
 */
class Atom implements Comparable {
    private String znacka;

    public Atom(String znacka) {
        this.znacka = znacka;
    }

    @Override
    public int hashCode() {
        return znacka.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Atom) {
            Atom at = (Atom) obj;
            return this.znacka.equals(at.getZnacka());
        }
        return false;
    }

    @Override
    public int compareTo(Object obj) throws ClassCastException{
        if (obj instanceof Atom) {
            Atom at = (Atom) obj;
            return this.getZnacka().compareTo(at.getZnacka());
        }
        throw new ClassCastException("Nelze porovnavat objekt tridy Atom s objektem: " + obj.toString());
    }

    // getters

    public String getZnacka() {
        return znacka;
    }
}
