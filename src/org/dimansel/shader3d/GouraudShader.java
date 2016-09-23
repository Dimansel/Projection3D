package org.dimansel.shader3d;

import org.dimansel.math3d.Vertex3D;
import org.dimansel.shader3d.IShader;

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
        //Barycentric perspective correct interpolation
        Vertex3D vc1 = new Vertex3D(shaded1.getRed() / v1.z, shaded1.getGreen() / v1.z, shaded1.getBlue() / v1.z);
        Vertex3D vc2 = new Vertex3D(shaded2.getRed() / v2.z, shaded2.getGreen() / v2.z, shaded2.getBlue() / v2.z);
        Vertex3D vc3 = new Vertex3D(shaded3.getRed() / v3.z, shaded3.getGreen() / v3.z, shaded3.getBlue() / v3.z);
        Vertex3D vv1 = new Vertex3D(v1.x, v1.y, 1/v1.z);
        Vertex3D vv2 = new Vertex3D(v2.x, v2.y, 1/v2.z);
        Vertex3D vv3 = new Vertex3D(v3.x, v3.y, 1/v3.z);

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

        return new Color((int)r, (int)g, (int)b);
    }

    private double area(Vertex3D vt1, Vertex3D vt2, Vertex3D vt3) {
        return (vt3.x - vt1.x) * (vt2.y - vt1.y) - (vt3.y - vt1.y) * (vt2.x - vt1.x);
    }
}
