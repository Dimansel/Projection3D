package org.dimansel.projection3d;

import org.dimansel.math3d.Vertex3D;
import org.dimansel.shader3d.IShader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public final class OBJModelLoader {
    public static Model load(String path, IShader shader) {
        File file = new File(path);
        if (!file.exists()) return null;

        ArrayList<Vertex3D> vertices = new ArrayList<>();
        ArrayList<Face> faces = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            for(String line; (line = br.readLine()) != null;) {
                if (line.startsWith("v ")) {
                    String[] data = line.split(" ");
                    if (data.length != 4) continue;

                    vertices.add(new Vertex3D(
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]),
                            Double.parseDouble(data[3]))
                    );
                } else if (line.startsWith("f ")) {
                    String[] data = line.split(" ");
                    if (data.length != 4) continue;

                    String[] fn1 = data[1].split("//");
                    String[] fn2 = data[2].split("//");
                    String[] fn3 = data[3].split("//");
                    int f1 = Integer.parseInt(fn1[0]);
                    int f2 = Integer.parseInt(fn2[0]);
                    int f3 = Integer.parseInt(fn3[0]);

                    faces.add(new Face(f1, f2, f3));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return new Model(vertices, faces, shader);
    }
}
