package org.dimansel.shader3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public class PhongShader implements IShader {
    private Color color;
    private Vertex3D lightPos;
    private Vertex3D vn1;
    private Vertex3D vn2;
    private Vertex3D vn3;
    private Vertex3D vt1;
    private Vertex3D vt2;
    private Vertex3D vt3;

    public PhongShader(Color c) {
        color = c;
    }

    @Override
    public void apply(Vertex3D w1, Vertex3D w2, Vertex3D w3, Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn, Vertex3D lightPos) {
        this.lightPos = lightPos;
        this.vn1 = vn1;
        this.vn2 = vn2;
        this.vn3 = vn3;
        vt1 = w1;
        vt2 = w2;
        vt3 = w3;
    }

    @Override
    public Color getColor(int x, int y, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        double a = area(v1, v2, v3);
        Vertex3D p = new Vertex3D(x, y, 0);
        double w1 = area(v2, v3, p);
        double w2 = area(v3, v1, p);
        double w3 = area(v1, v2, p);
        w1 /= a*v1.z;
        w2 /= a*v2.z;
        w3 /= a*v3.z;
        double z = 1/(w1+w2+w3);

        double g1 = vn1.x*w1+vn2.x*w2+vn3.x*w3;
        double g2 = vn1.y*w1+vn2.y*w2+vn3.y*w3;
        double g3 = vn1.z*w1+vn2.z*w2+vn3.z*w3;
        Vertex3D iV = new Vertex3D(z*g1, z*g2, z*g3);
        Vertex3D point = new Vertex3D((vt1.x*w1+vt2.x*w2+vt3.x*w3)*z, (vt1.y*w1+vt2.y*w2+vt3.y*w3)*z, z);
        iV.normalize();
        Vertex3D lightVec = new Vertex3D(lightPos.x - point.x, lightPos.y - point.y, lightPos.z - point.z);
        lightVec.normalize();
        double cos = Math.max(0, iV.dot(lightVec));
        return new Color((int)(cos*color.getRed()), (int)(cos*color.getGreen()), (int)(cos*color.getBlue()));
    }

    private double area(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3) {
        return (vt3.x - vt1.x) * (vt2.y - vt1.y) - (vt3.y - vt1.y) * (vt2.x - vt1.x);
    }
}
