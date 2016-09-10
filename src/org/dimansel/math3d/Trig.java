package org.dimansel.math3d;

public class Trig {
    public static double sin(double a) {
        return Math.sin(Math.toRadians(a));
    }

    public static double cos(double a) {
        return Math.cos(Math.toRadians(a));
    }

    public static double tan(double a) {
        return Math.tan(Math.toRadians(a));
    }

    public static double cot(double a) {
        double ang = Math.toRadians(a);
        return Math.cos(ang)/Math.sin(ang);
    }
}
