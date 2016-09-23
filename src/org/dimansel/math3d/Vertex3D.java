package org.dimansel.math3d;

public class Vertex3D {
    public double x, y, z;

    public Vertex3D(double xx, double yy, double zz) {
        x = xx;
        y = yy;
        z = zz;
    }
    public Vertex3D() {
        x = 0;
        y = 0;
        z = 0;
    }

    public void normalize() {
        if (Math.abs(x) < 0.00001 && Math.abs(y) < 0.00001 && Math.abs(z) < 0.00001) return;

        double len = abs();
        x /= len;
        y /= len;
        z /= len;
    }

    public double abs() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public double dot(Vertex3D v) {
        return (x*v.x+y*v.y+z*v.z);
    }

    public Vertex3D sum(Vertex3D v) {
        return new Vertex3D(x+v.x, y+v.y, z+v.z);
    }

    public Vertex3D multiply(double val) {
        return new Vertex3D(x*val, y*val, z*val);
    }

    @Override
    public String toString() {
        return String.valueOf(x) + "; " + String.valueOf(y) + "; " + String.valueOf(z);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vertex3D)) return false;
        Vertex3D v = (Vertex3D)o;
        return v.x == x && v.y == y && v.z == z;
    }
}
