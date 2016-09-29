package org.dimansel.projection3d;

import org.dimansel.math3d.Matrix;
import org.dimansel.math3d.Vertex3D;
import org.dimansel.shader3d.IShader;

import java.util.ArrayList;

public class Model {
    private ArrayList<Vertex3D> vertices;
    private ArrayList<Face> faces;
    private ArrayList<Vertex3D> faceNormals;
    private ArrayList<Vertex3D> vertexNormals;
    private ArrayList<Triangle> triangles;
    public Vertex3D position;
    public IShader shader;
    public boolean backFaceCulling = true;

    public Model(ArrayList<Vertex3D> vertices, ArrayList<Face> faces, IShader shader) {
        this.vertices = vertices;
        this.faces = faces;
        triangles = new ArrayList<>();
        faceNormals = new ArrayList<>();
        vertexNormals = new ArrayList<>();
        position = new Vertex3D();
        this.shader = shader;

        calculateFaceNormals();
        calculateVertexNormals();
    }

    private void calculateFaceNormals() {
        faceNormals.clear();

        for (int a = 0; a < faces.size(); a++) {
            Face f = faces.get(a);
            Vertex3D w1 = vertices.get(f.v[0]-1);
            Vertex3D w2 = vertices.get(f.v[1]-1);
            Vertex3D w3 = vertices.get(f.v[2]-1);

            Vertex3D vec1 = new Vertex3D(w2.x-w1.x, w2.y-w1.y, w2.z-w1.z);
            Vertex3D vec2 = new Vertex3D(w3.x-w1.x, w3.y-w1.y, w3.z-w1.z);
            Vertex3D normal = new Vertex3D(vec1.y*vec2.z-vec1.z*vec2.y, vec1.z*vec2.x-vec1.x*vec2.z, vec1.x*vec2.y-vec1.y*vec2.x);
            normal.normalize();

            faceNormals.add(normal);
            f.fn = a+1;
        }
    }

    private void calculateVertexNormals() {
        vertexNormals.clear();

        for (int a = 0; a < vertices.size(); a++) {
            Vertex3D vNormal = new Vertex3D();
            for (Face f : faces) {
                int vi = f.contains(a + 1);
                if (vi != -1) {
                    f.vn[vi] = a + 1;
                    Vertex3D w1 = vertices.get(f.v[vi] - 1);
                    Vertex3D w2 = vertices.get(f.v[(vi + 1) % 3] - 1);
                    Vertex3D w3 = vertices.get(f.v[(vi + 2) % 3] - 1);
                    Vertex3D vec1 = new Vertex3D(w2.x - w1.x, w2.y - w1.y, w2.z - w1.z);
                    Vertex3D vec2 = new Vertex3D(w3.x - w1.x, w3.y - w1.y, w3.z - w1.z);
                    vec1.normalize();
                    vec2.normalize();
                    double angle = Math.acos(vec1.dot(vec2));
                    vNormal = vNormal.sum(faceNormals.get(f.fn - 1).multiply(angle));
                }
            }
            vNormal.normalize();
            vertexNormals.add(vNormal);
        }
    }

    public void projectVertices(Camera cam, Vertex3D lightPos) {
        triangles.clear();

        for (Face f : faces) {
            Vertex3D w1 = vertices.get(f.v[0] - 1).sum(position);
            Vertex3D w2 = vertices.get(f.v[1] - 1).sum(position);
            Vertex3D w3 = vertices.get(f.v[2] - 1).sum(position);

            Vertex3D v1 = cam.project(w1);
            Vertex3D v2 = cam.project(w2);
            Vertex3D v3 = cam.project(w3);

            if (v1 == null || v2 == null || v3 == null) continue;
            double[][] m = {
                    {v2.x - v1.x, v3.x - v1.x, v1.x},
                    {v2.y - v1.y, v3.y - v1.y, v1.y},
                    {0, 0, 1}
            };

            if (Matrix.det3(m) >= 0 || !backFaceCulling) {
                Vertex3D vn1 = vertexNormals.get(f.vn[0] - 1);
                Vertex3D vn2 = vertexNormals.get(f.vn[1] - 1);
                Vertex3D vn3 = vertexNormals.get(f.vn[2] - 1);
                Vertex3D fn = faceNormals.get(f.fn - 1);
                triangles.add(new Triangle(v1, v2, v3, w1, w2, w3, vn1, vn2, vn3, fn, lightPos, shader));
            }
        }
    }

    public void Render(int[] data, double[] zbuffer, int width) {
        for (Triangle triangle : triangles) triangle.render(data, zbuffer, width);
    }
}
