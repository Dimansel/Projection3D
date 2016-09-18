package org.dimansel.math3d;

public final class Matrix {
    public static double[][] multiply(double[][] a, double[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] c = new double[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] += a[i][k] * b[k][j];
        return c;
    }

    public static double[] multiply(double[][] a, double[] x) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += a[i][j] * x[j];
        return y;
    }

    public static double det3(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        if (m != 3 || n != 3) throw new RuntimeException("Illegal matrix dimensions.");
        double det =
                a[0][0]*(a[1][1]*a[2][2] - a[1][2]*a[2][1]) -
                a[0][1]*(a[1][0]*a[2][2] - a[1][2]*a[2][0]) +
                a[0][2]*(a[1][0]*a[2][1] - a[1][1]*a[2][0]);
        return det;
    }
}
