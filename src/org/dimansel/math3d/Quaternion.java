package org.dimansel.math3d;

public class Quaternion {
    public double x, y, z, w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Quaternion multiply(Quaternion q) {
        double a = x*q.w + q.x*w + y*q.z -q.y*z;
        double b = y*q.w + q.y*w + q.x*z - x*q.z;
        double c = z*q.w + q.z*w + x*q.y - q.x*y;
        double d = -(x*q.x + y*q.y + z*q.z - w*q.w);
        return new Quaternion(a, b, c, d);
    }

    public Quaternion multiply(double k) {
        return new Quaternion(x*k, y*k, z*k, w*k);
    }

    public Quaternion add(double x0, double y0, double z0, double w0) {
        return new Quaternion(x+x0, y+y0, z+z0, w+w0);
    }
}
