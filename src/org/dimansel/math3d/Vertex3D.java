package org.dimansel.math3d;

public class Vertex3D {
    public double x, y, z;

    public Vertex3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex3D(Quaternion q) {
        this(q.x, q.y, q.z);
    }

    public Vertex3D() {
        this(0, 0, 0);
    }

    public void normalize() {
        double len = abs();
        if (Double.isInfinite(1/len)) return;
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
