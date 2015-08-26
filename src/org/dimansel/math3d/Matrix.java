package org.dimansel.math3d;

public class Matrix {
    public double[] data;
    public int width;
    public int height;

    public Matrix(int w, int h)
    {
        width = w;
        height = h;

        data = new double[w * h];
    }

    public Matrix(int w, int h, double[] dt)
    {
        width = w;
        height = h;

        if (dt.length != w * h) throw new IllegalArgumentException("Wrong array length");
        data = dt;
    }

    public static double dotProduct(double[] m1, double[] m2)
    {
        if (m1.length != m2.length) return Double.NaN;

        double res = 0;
        for (int a = 0; a < m1.length; a++)
        {
            res += m1[a] * m2[a];
        }

        return res;
    }

    public double[] getRow(int index)
    {
        if (index >= height || index < 0) throw new IndexOutOfBoundsException();

        double[] res = new double[width];
        for (int a = 0; a < width; a++)
        {
            res[a] = data[a + width * index];
        }

        return res;
    }

    public double[] getColumn(int index)
    {
        if (index >= width || index < 0) throw new IndexOutOfBoundsException();

        double[] res = new double[height];
        for (int a = 0; a < height; a++)
        {
            res[a] = data[a * width + index];
        }

        return res;
    }

    public double getDeterminant3x3() {
        if (width != 3 && height != 3) return Double.NaN;

        double det = 0;
        for (int a = 1; a <= 3; a++) {
            det += Math.pow(-1, a-1)*data[a-1]*(data[4-(a/2)]*data[8-(a-1)/2]-data[5-(a-1)/2]*data[7-(a/2)]);
        }

        return det;
    }

    public Matrix transpose()
    {
        double[] dt = new double[width * height];

        for (int a = 0; a < width * height; a++)
        {
            dt[a] = data[a * width + (int)(a / height)];
        }

        return new Matrix(height, width, dt);
    }

    public Matrix multiply(Matrix m2)
    {
        if (this.width != m2.height) return null;

        double[] dt = new double[this.height * m2.width];
        Matrix res = new Matrix(m2.width, this.height);
        for (int a = 0; a < res.width; a++)
        {
            for (int b = 0; b < res.height; b++)
            {
                dt[a + b * res.width] = dotProduct(this.getRow(b), m2.getColumn(a));
            }
        }

        res.data = dt;
        return res;
    }

    public Matrix sum(Matrix m2)
    {
        if (this.width != m2.width || this.height != m2.height) return null;

        double[] dt = new double[this.width * this.height];
        for (int a = 0; a < dt.length; a++)
        {
            dt[a] = this.data[a] + m2.data[a];
        }

        return new Matrix(this.width, this.height, dt);
    }

    public Matrix subtract(Matrix m2)
    {
        if (this.width != m2.width || this.height != m2.height) return null;

        double[] dt = new double[this.width * this.height];
        for (int a = 0; a < dt.length; a++)
        {
            dt[a] = this.data[a] - m2.data[a];
        }

        return new Matrix(this.width, this.height, dt);
    }
}
