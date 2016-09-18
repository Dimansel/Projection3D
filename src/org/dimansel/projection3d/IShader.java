package org.dimansel.projection3d;

import org.dimansel.math3d.Vertex3D;

import java.awt.*;

public interface IShader {
    void apply(Vertex3D w1, Vertex3D w2, Vertex3D w3, Vertex3D vn1, Vertex3D vn2, Vertex3D vn3, Vertex3D fn, Vertex3D lightPos);
    Color getColor(int x, int y, Vertex3D v1, Vertex3D v2, Vertex3D v3);
}
