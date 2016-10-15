package org.dimansel.shader3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public class GouraudShader implements IShader {
    private Color color;
    private Color shaded1;
    private Color shaded2;
    private Color shaded3;

    public GouraudShader(Color c) {
        color = c;
    }

    @Override
    public void apply(Vertex3D w1, Vertex3D w2, Vertex3D w3, Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn, Vertex3D lightPos) {
        Vertex3D lightVec1 = new Vertex3D(lightPos.x-w1.x, lightPos.y-w1.y, lightPos.z-w1.z);
        Vertex3D lightVec2 = new Vertex3D(lightPos.x-w2.x, lightPos.y-w2.y, lightPos.z-w2.z);
        Vertex3D lightVec3 = new Vertex3D(lightPos.x-w3.x, lightPos.y-w3.y, lightPos.z-w3.z);
        lightVec1.normalize();
        lightVec2.normalize();
        lightVec3.normalize();
        double cos1 = Math.max(0, vn1.dot(lightVec1));
        double cos2 = Math.max(0, vn2.dot(lightVec2));
        double cos3 = Math.max(0, vn3.dot(lightVec3));
        shaded1 = new Color((int)(cos1*color.getRed()), (int)(cos1*color.getGreen()), (int)(cos1*color.getBlue()));
        shaded2 = new Color((int)(cos2*color.getRed()), (int)(cos2*color.getGreen()), (int)(cos2*color.getBlue()));
        shaded3 = new Color((int)(cos3*color.getRed()), (int)(cos3*color.getGreen()), (int)(cos3*color.getBlue()));
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

        double g1 = shaded1.getRed()*w1+shaded2.getRed()*w2+shaded3.getRed()*w3;
        double g2 = shaded1.getGreen()*w1+shaded2.getGreen()*w2+shaded3.getGreen()*w3;
        double g3 = shaded1.getBlue()*w1+shaded2.getBlue()*w2+shaded3.getBlue()*w3;
        return new Color((int)(z*g1), (int)(z*g2), (int)(z*g3));
    }

    private double area(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3) {
        return (vt3.x - vt1.x) * (vt2.y - vt1.y) - (vt3.y - vt1.y) * (vt2.x - vt1.x);
    }
}
