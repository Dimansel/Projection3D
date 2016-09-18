package org.dimansel.projection3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public class Triangle {
    private Vertex3D v1;
    private Vertex3D v2;
    private Vertex3D v3;
    private Vertex3D w1;
    private Vertex3D w2;
    private Vertex3D w3;
    private Vertex3D lightPos;
    private Vertex3D vn1;
    private Vertex3D vn2;
    private Vertex3D vn3;
    private Vertex3D fn;
    private Model parent;

    public Triangle(Vertex3D v1, Vertex3D v2, Vertex3D v3,
                    Vertex3D w1, Vertex3D w2, Vertex3D w3,
                    Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn,
                    Vertex3D lightPos, Model parent) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        this.lightPos = lightPos;
        this.vn1 = vn1;
        this.vn2 = vn2;
        this.vn3 = vn3;
        this.fn = fn;
        this.parent = parent;
    }

    /*public void render (int[] data, double[] zbuffer, int width) {
        int vxmin = (int)Math.min(v1.x, Math.min(v2.x, v3.x));
        int vxmax = (int)Math.max(v1.x, Math.max(v2.x, v3.x));
        int vymin = (int)Math.min(v1.y, Math.min(v2.y, v3.y));
        int vymax = (int)Math.max(v1.y, Math.max(v2.y, v3.y));
        int xmin = Math.max(0, vxmin);
        int xmax = Math.min(width-1, vxmax);
        int ymin = Math.max(0, vymin);
        int ymax = Math.min(data.length/width-1, vymax);

        //rasterization cycles
        for (int y = ymin; y <= ymax; y++) {
            int x1 = getIntersection(v1.x, v1.y, v2.x, v2.y, y, vxmin, vxmax);
            int x2 = getIntersection(v1.x, v1.y, v3.x, v3.y, y, vxmin, vxmax);
            int x3 = getIntersection(v2.x, v2.y, v3.x, v3.y, y, vxmin, vxmax);
            if ((x1 == -1 && x2 == -1) || (x1 == -1 && x3 == -1) || (x2 == -1 && x3 == -1)) continue;
            if (x1 == -1) x1 = x2;
            if (x2 == -1) x2 = x3;

            for (int x = Math.min(x1, x2); x < Math.max(x1, x2); x++) {
                double z = interpolateZ(x, y);

                if (z < zbuffer[x+y*width]) {
                    zbuffer[x+y*width] = z;
                    data[x+y*width] = gouraudShading ? interpolateColor(x, y) : rgbToHex(c.getRed(), c.getGreen(), c.getBlue());
                }
            }
        }
    }

    private int getIntersection(double x1, double y1, double x2, double y2, int y0, int xmin, int xmax) {
        if (y1 == y2) return -1;
        int x = (int)Math.floor((x2*y1-x1*y2+(x1-x2)*y0)/(y1-y2));
        if (x < xmin || x > xmax) return -1;
        return x;
    }*/

    public void render (int[] data, double[] zbuffer, int width) {
        parent.shader.apply(w1, w2, w3, vn1, vn2, vn3, fn, lightPos);
        int xmin = Math.max(0, (int)Math.min(v1.x, Math.min(v2.x, v3.x)));
        int xmax = Math.min(width-1, (int)Math.max(v1.x, Math.max(v2.x, v3.x)));
        int ymin = Math.max(0, (int)Math.min(v1.y, Math.min(v2.y, v3.y)));
        int ymax = Math.min(data.length/width-1, (int)Math.max(v1.y, Math.max(v2.y, v3.y)));

        //rasterization cycles
        for (int y = ymin; y <= ymax; y++) {
            for (int x = xmin; x <= xmax; x++) {
                if (pointInTriangle(new Vertex3D(x, y, 0), v1, v2, v3)) {
                    double z = interpolateZ(x, y);

                    if (z < zbuffer[x+y*width]) {
                        zbuffer[x+y*width] = z;
                        Color q = parent.shader.getColor(x, y, v1, v2, v3);
                        data[x+y*width] = rgbToHex(q.getRed(), q.getGreen(), q.getBlue());
                    }
                }
            }
        }
    }

    private boolean pointInTriangle (Vertex3D pt, Vertex3D vt1, Vertex3D vt2, Vertex3D vt3) {
        boolean b1, b2, b3;

        b1 = sign(pt, vt1, vt2) < 0;
        b2 = sign(pt, vt2, vt3) < 0;
        b3 = sign(pt, vt3, vt1) < 0;

        return ((b1 == b2) && (b2 == b3));
    }

    private double sign (Vertex3D p1, Vertex3D p2, Vertex3D p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    public double interpolateZ(int x, int y) {
        Vertex3D vv1 = v1.copy();
        Vertex3D vv2 = v2.copy();
        Vertex3D vv3 = v3.copy();
        vv1.z = 1/vv1.z; vv2.z=1/vv2.z; vv3.z=1/vv3.z;

        double a = area(vv1, vv2, vv3);
        double w1 = area(vv2, vv3, new Vertex3D(x, y, 0));
        double w2 = area(vv3, vv1, new Vertex3D(x, y, 0));
        double w3 = area(vv1, vv2, new Vertex3D(x, y, 0));
        w1 /= a;
        w2 /= a;
        w3 /= a;

        double z = w1*vv1.z+w2*vv2.z+w3*vv3.z;
        return 1/z;
    }

    public double area(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3) {
        return (vt3.x - vt1.x) * (vt2.y - vt1.y) - (vt3.y - vt1.y) * (vt2.x - vt1.x);
    }

    public static int rgbToHex(int r, int g, int b) {
        return ((r << 16) + (g << 8) + b) | 0xFF000000;
    }
}
