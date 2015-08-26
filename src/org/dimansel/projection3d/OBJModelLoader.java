package org.dimansel.projection3d;

import org.dimansel.math3d.Vertex3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class OBJModelLoader {
    public static Model load(String path, double multiplier)
    {
        File file = new File(path);
        if (!file.exists()) return null;

        ArrayList<Vertex3D> vertices = new ArrayList<>();
        ArrayList<Vertex3D> normals = new ArrayList<>();
        ArrayList<Face> faces = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            for(String line; (line = br.readLine()) != null;)
            {
                if (line.startsWith("v ")) {
                    String[] data = line.split(" ");
                    if (data.length != 4) continue;

                    vertices.add(new Vertex3D(Double.parseDouble(data[1]) * multiplier, Double.parseDouble(data[2]) * multiplier, Double.parseDouble(data[3]) * multiplier));
                } else if (line.startsWith("vn ")) {
                    String[] data = line.split(" ");
                    if (data.length != 4) continue;

                    normals.add(new Vertex3D(Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])));
                } else if (line.startsWith("f ")) {
                    String[] data = line.split(" ");
                    if (data.length != 4) continue;

                    String[] fn1 = data[1].split("//");
                    String[] fn2 = data[2].split("//");
                    String[] fn3 = data[3].split("//");
                    int f1 = Integer.parseInt(fn1[0]);
                    int f2 = Integer.parseInt(fn2[0]);
                    int f3 = Integer.parseInt(fn3[0]);

                    int n1 = fn1.length == 2 ? Integer.parseInt(fn1[1]) : -1;
                    int n2 = fn2.length == 2 ? Integer.parseInt(fn2[1]) : -1;
                    int n3 = fn3.length == 2 ? Integer.parseInt(fn3[1]) : -1;

                    faces.add(new Face(f1, n1));
                    faces.add(new Face(f2, n2));
                    faces.add(new Face(f3, n3));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return new Model(vertices, faces, normals);
    }
}
