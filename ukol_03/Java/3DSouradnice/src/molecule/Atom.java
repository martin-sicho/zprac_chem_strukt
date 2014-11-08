package molecule;

import graph.Node;

import java.util.Map;

/**
 * Created by Martin Šícho on 7.11.14.
 */
public class Atom extends Node {
    private double x;
    private double y;
    private double z;

    // constructors

    public Atom(double x, double y, double z) {
        super();
        setCoords(x, y, z);
    }

    public Atom(String name, double x, double y, double z) {
        super(name);
        setCoords(x, y, z);
    }

    public Atom(double[] coords) {
        super();
        if (coords.length < 3) {
            throw new ArrayStoreException(String.format("Array size is insufficient (%d).", coords.length));
        }
        setCoords(coords[0], coords[1], coords[2]);
    }

    public Atom(String name, double[] coords) {
        super(name);
        if (coords.length < 3) {
            throw new ArrayStoreException(String.format("Array size is insufficient (%d).", coords.length));
        }
        setCoords(coords[0], coords[1], coords[2]);
    }

    // non-static methods

    public void getCoords(double[] coords) {
        if (coords.length < 3) {
            throw new ArrayStoreException(String.format("Array size is insufficient (%d).", coords.length));
        }
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
    }

    public void getCoords(Map<Character, Double> coords_map) {
        coords_map.put('x', x);
        coords_map.put('y', y);
        coords_map.put('z', z);
    }

    public void setCoords(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // overrides

    @Override
    public String toString() {
        return String.format("molecule.Atom[order:%d,name:%s]", getNodeCount(), getName());
    }


    // getters & setters

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
