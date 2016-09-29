package org.dimansel.projection3d;

import org.dimansel.math3d.Vertex3D;
import org.dimansel.shader3d.IShader;

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
    private IShader shader;

    public Triangle(Vertex3D v1, Vertex3D v2, Vertex3D v3,
                    Vertex3D w1, Vertex3D w2, Vertex3D w3,
                    Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn,
                    Vertex3D lightPos, IShader shader) {
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
        this.shader = shader;
    }

    public void render (int[] data, double[] zbuffer, int width) {
        shader.apply(w1, w2, w3, vn1, vn2, vn3, fn, lightPos);
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
                        Color q = shader.getColor(x, y, v1, v2, v3);
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

    private double interpolateZ(int x, int y) {
        Vertex3D vv1 = new Vertex3D(v1.x, v1.y, 1 / v1.z);
        Vertex3D vv2 = new Vertex3D(v2.x, v2.y, 1 / v2.z);
        Vertex3D vv3 = new Vertex3D(v3.x, v3.y, 1 / v3.z);

        double a = area(vv1, vv2, vv3);
        Vertex3D p = new Vertex3D(x, y, 0);
        double w1 = area(vv2, vv3, p);
        double w2 = area(vv3, vv1, p);
        double w3 = area(vv1, vv2, p);
        w1 /= a;
        w2 /= a;
        w3 /= a;

        double z = w1*vv1.z+w2*vv2.z+w3*vv3.z;
        return 1/z;
    }

    private double area(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3) {
        return (vt3.x - vt1.x) * (vt2.y - vt1.y) - (vt3.y - vt1.y) * (vt2.x - vt1.x);
    }

    private static int rgbToHex(int r, int g, int b) {
        return ((r << 16) + (g << 8) + b) | 0xFF000000;
    }
}
