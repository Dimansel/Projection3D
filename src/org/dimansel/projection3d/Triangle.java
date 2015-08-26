package org.dimansel.projection3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public class Triangle {
    private Vertex3D v1;
    private Vertex3D v2;
    private Vertex3D v3;
    private boolean gouraudShading;
    private Color c, c1, c2, c3;

    public Triangle(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3, Color color) {
        v1 = vt1;
        v2 = vt2;
        v3 = vt3;
        c = color;
        gouraudShading = false;
    }

    public Triangle(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3, Color c1, Color c2, Color c3) {
        v1 = vt1;
        v2 = vt2;
        v3 = vt3;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        gouraudShading = true;
    }

    public void render (int[] data, double[] zbuffer, int width) {
        int xmin = (int)Math.min(v1.x, Math.min(v2.x, v3.x));
        int xmax = (int)Math.max(v1.x, Math.max(v2.x, v3.x));
        int ymin = (int)Math.min(v1.y, Math.min(v2.y, v3.y));
        int ymax = (int)Math.max(v1.y, Math.max(v2.y, v3.y));

        //rasterization cycles
        for (int y = ymin; y <= ymax; y++) {
            if (y < 0 || y >= data.length/width) continue;

            for (int x = xmin; x <= xmax; x++) {
                if (x < 0 || x >= width) continue;

                if (pointInTriangle(new Vertex3D(x, y, 0), v1, v2, v3)) {
                    double z = interpolateZ(x, y);

                    if (z < zbuffer[x+y*width]) {
                        zbuffer[x+y*width] = z;
                        data[x+y*width] = gouraudShading ? interpolateColor(x, y) : rgbToHex(c.getRed(), c.getGreen(), c.getBlue());
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

    //Barycentric perspective correct interpolation
    public int interpolateColor(int x, int y) {
        Vertex3D vc1 = new Vertex3D(c1.getRed(), c1.getGreen(), c1.getBlue());
        Vertex3D vc2 = new Vertex3D(c2.getRed(), c2.getGreen(), c2.getBlue());
        Vertex3D vc3 = new Vertex3D(c3.getRed(), c3.getGreen(), c3.getBlue());
        Vertex3D vv1 = v1.copy();
        Vertex3D vv2 = v2.copy();
        Vertex3D vv3 = v3.copy();
        
        vc1.x /= vv1.z; vc1.y /= vv1.z; vc1.z /= vv1.z;
        vc2.x /= vv2.z; vc2.y /= vv2.z; vc2.z /= vv2.z;
        vc3.x /= vv3.z; vc3.y /= vv3.z; vc3.z /= vv3.z;
        vv1.z = 1 / vv1.z; vv2.z = 1 / vv2.z; vv3.z = 1 / vv3.z;
        
        double a = area(vv1, vv2, vv3);
        double w1 = area(vv2, vv3, new Vertex3D(x, y, 0));
        double w2 = area(vv3, vv1, new Vertex3D(x, y, 0));
        double w3 = area(vv1, vv2, new Vertex3D(x, y, 0));
        w1 /= a;
        w2 /= a;
        w3 /= a;

        double r = w1*vc1.x+w2*vc2.x+w3*vc3.x;
        double g = w1*vc1.y+w2*vc2.y+w3*vc3.y;
        double b = w1*vc1.z+w2*vc2.z+w3*vc3.z;

        double z = 1 / (w1*vv1.z + w2*vv2.z + w3*vv3.z);
        r *= z; g *= z; b *= z;

        return rgbToHex((int)r, (int)g, (int)b);
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
