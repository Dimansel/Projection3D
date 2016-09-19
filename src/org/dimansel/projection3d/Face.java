package org.dimansel.projection3d;

public class Face {
    public int[] v;
    public int[] vn = {-1, -1, -1};
    public int fn;

    public Face(int v1, int v2, int v3) {
        v = new int[] {v1, v2, v3};
    }

    public int contains(int i) {
        if (v[0] == i) return 0;
        if (v[1] == i) return 1;
        if (v[2] == i) return 2;
        return -1;
    }
}
