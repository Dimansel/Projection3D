package org.dimansel.math3d;

public class Vertex3D {
    public double x, y, z;

    public Vertex3D(double xx, double yy, double zz)
    {
        x = xx;
        y = yy;
        z = zz;
    }
    public Vertex3D()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public void normalize() {
        if (Math.abs(x) < 0.00001 && Math.abs(y) < 0.00001 && Math.abs(z) < 0.00001) return;

        double len = Math.sqrt(x*x+y*y+z*z);
        x /= len;
        y /= len;
        z /= len;
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

    public Vertex3D divide(double val) {
        return new Vertex3D(x/val, y/val, z/val);
    }

    public Vertex3D copy() {
        return new Vertex3D(x, y, z);
    }

    public Matrix toMatrix() {
        return new Matrix(1, 3, new double[]{x, y, z});
    }

    @Override
    public String toString() {
        return String.valueOf(x) + "; " + String.valueOf(y) + "; " + String.valueOf(z);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vertex3D)) return false;
        Vertex3D v = (Vertex3D)o;
        if (v.x != x || v.y != y || v.z != z) return false;
        return true;
    }
}
