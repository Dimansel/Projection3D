package org.dimansel.projection3d;

import org.dimansel.math3d.Matrix;
import org.dimansel.math3d.Vertex3D;

import java.awt.*;
import java.util.ArrayList;

public class Model {
    private ArrayList<Vertex3D> vertices;
    private ArrayList<Face> faces;
    private ArrayList<Vertex3D> faceNormals;
    private ArrayList<Vertex3D> vertexNormals;
    private ArrayList<Triangle> triangles;
    public Vertex3D position;
    public Color color;
    public boolean backFaceCulling = true;
    public boolean gouraudShading = false;

    public Model(ArrayList<Vertex3D> vertices, ArrayList<Face> faces, ArrayList<Vertex3D> vertexNormals) {
        this.vertices = vertices;
        this.faces = faces;
        triangles = new ArrayList<>();
        this.vertexNormals = vertexNormals;
        faceNormals = new ArrayList<>();
        position = new Vertex3D();
        color = Color.white;

        calculateFaceNormals();
        if (vertexNormals.size() != vertices.size() || faces.get(0).n == -1) {
            //if vertex normals not loaded or invalid calculate manually
            calculateVertexNormals();
        }
    }

    public void projectVertices(Camera cam, Vertex3D lightPos) {
        triangles.clear();

        for (int a = 0; a < faces.size(); a+=3) {
            Vertex3D w1 = vertices.get(faces.get(a).v - 1).sum(position);
            Vertex3D w2 = vertices.get(faces.get(a + 1).v - 1).sum(position);
            Vertex3D w3 = vertices.get(faces.get(a + 2).v - 1).sum(position);

            Vertex3D v1 = cam.project(w1);
            Vertex3D v2 = cam.project(w2);
            Vertex3D v3 = cam.project(w3);

            if (v1 == null || v2 == null || v3 == null) continue;
            Matrix m = new Matrix(3, 3, new double[]{
                    v2.x-v1.x, v3.x-v1.x, v1.x,
                    v2.y-v1.y, v3.y-v1.y, v1.y,
                    0, 0, 1});

            if (m.getDeterminant3x3() >= 0 || !backFaceCulling) {
                if (gouraudShading) {
                    Color c1 = applyGouraudShading(w1, lightPos, vertexNormals.get(faces.get(a).n - 1));
                    Color c2 = applyGouraudShading(w2, lightPos, vertexNormals.get(faces.get(a + 1).n - 1));
                    Color c3 = applyGouraudShading(w3, lightPos, vertexNormals.get(faces.get(a + 2).n - 1));
                    triangles.add(new Triangle(v1, v2, v3, c1, c2, c3));
                }
                else {
                    Color c = applyFlatShading(w1, w2, w3, lightPos, faceNormals.get(a / 3));
                    triangles.add(new Triangle(v1, v2, v3, c));
                }
            }
        }
    }

    private void calculateFaceNormals() {
        faceNormals.clear();

        for (int a = 0; a < faces.size(); a+=3) {
            Vertex3D w1 = vertices.get(faces.get(a).v - 1);
            Vertex3D w2 = vertices.get(faces.get(a + 1).v - 1);
            Vertex3D w3 = vertices.get(faces.get(a + 2).v - 1);

            Vertex3D vec1 = new Vertex3D(w2.x-w1.x, w2.y-w1.y, w2.z-w1.z);
            Vertex3D vec2 = new Vertex3D(w3.x-w1.x, w3.y-w1.y, w3.z-w1.z);
            Vertex3D normal = new Vertex3D(vec1.y*vec2.z-vec1.z*vec2.y, vec1.z*vec2.x-vec1.x*vec2.z, vec1.x*vec2.y-vec1.y*vec2.x);
            normal.normalize();

            faceNormals.add(normal);
        }
    }

    private void calculateVertexNormals() {
        vertexNormals.clear();

        for (int a = 0; a < vertices.size(); a++) {
            Vertex3D curr = vertices.get(a);

            ArrayList<Vertex3D> fNormals = new ArrayList<>();
            for (int b = 0; b < faces.size(); b++) {
                if (vertices.get(faces.get(b).v-1).equals(curr)) {
                    fNormals.add(faceNormals.get(b/3));
                    faces.get(b).n = a+1;
                }
            }

            Vertex3D vNormal = new Vertex3D();
            for (int b = 0; b < fNormals.size(); b++) {
                vNormal = vNormal.sum(fNormals.get(b));
            }
            vNormal = vNormal.divide(fNormals.size());
            vNormal.normalize();
            vertexNormals.add(vNormal);
        }
    }

    private Color applyFlatShading(Vertex3D w1, Vertex3D w2, Vertex3D w3, Vertex3D lightPos, Vertex3D normal) {
        Vertex3D tC = new Vertex3D((w1.x+w2.x+w3.x)/3, (w1.y+w2.y+w3.y)/3, (w1.z+w2.z+w3.z)/3);
        Vertex3D lightVec = new Vertex3D(lightPos.x - tC.x, lightPos.y - tC.y, lightPos.z - tC.z);
        lightVec.normalize();
        double cos = Math.max(0, normal.dot(lightVec));
        Color c = new Color((int)(cos*color.getRed()), (int)(cos*color.getGreen()), (int)(cos*color.getBlue()));

        return c;
    }

    private Color applyGouraudShading(Vertex3D w1, Vertex3D lightPos, Vertex3D normal) {
        Vertex3D lightVec = new Vertex3D(lightPos.x-w1.x, lightPos.y-w1.y, lightPos.z-w1.z);
        lightVec.normalize();
        double cos = Math.max(0, normal.dot(lightVec));
        Color c = new Color((int)(cos*color.getRed()), (int)(cos*color.getGreen()), (int)(cos*color.getBlue()));

        return c;
    }

    public void Render(int[] data, double[] zbuffer, int width) {
        for (int a = 0; a < triangles.size(); a++)
            triangles.get(a).render(data, zbuffer, width);
    }
}
