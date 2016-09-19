package org.dimansel.shader3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public class EmptyShader implements IShader {
    private Color color;

    public EmptyShader(Color c) {
        color = c;
    }

    @Override
    public void apply(Vertex3D w1, Vertex3D w2, Vertex3D w3, Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn, Vertex3D lightPos) {

    }

    @Override
    public Color getColor(int x, int y, Vertex3D v1, Vertex3D v2, Vertex3D v3) {
        return color;
    }
}
