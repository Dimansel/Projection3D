package org.dimansel.shader3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public class FlatShader implements IShader {
    private Color color;
    private Color shaded;

    public FlatShader(Color c) {
        color = c;
    }

    @Override
    public void apply(Vertex3D w1, Vertex3D w2, Vertex3D w3, Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn, Vertex3D lightPos) {
        Vertex3D tC = new Vertex3D((w1.x+w2.x+w3.x)/3, (w1.y+w2.y+w3.y)/3, (w1.z+w2.z+w3.z)/3);
        Vertex3D lightVec = new Vertex3D(lightPos.x - tC.x, lightPos.y - tC.y, lightPos.z - tC.z);
        lightVec.normalize();
        double cos = Math.max(0, fn.dot(lightVec));
        shaded = new Color((int)(cos*color.getRed()), (int)(cos*color.getGreen()), (int)(cos*color.getBlue()));
    }

    @Override
    public Color getColor(int x, int y, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        return shaded;
    }
}
