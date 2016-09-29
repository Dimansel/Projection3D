package org.dimansel.projection3d;

import org.dimansel.math3d.*;

import java.awt.event.KeyEvent;

public class Camera {
    public Vertex3D pos = new Vertex3D(0, 0, 0); //camera position
    public double yaw = 0; //camera orientation
    public double pitch = 0;
    private int width; //screen width and height
    private int height;
    private double fov; //field of view angle
    private double aspect; //width to height ratio
    private double nearPlane; //z coordinate of clipping planes
    private double farPlane;

    public Camera(int width, int height, double fov, double near, double far) {
        this.width = width;
        this.height = height;
        this.fov = fov;
        aspect = (double)width / height;
        nearPlane = near;
        farPlane = far;
    }

    public Vertex3D project(Vertex3D v) {
        v = new Vertex3D(v.x - pos.x, v.y - pos.y, v.z - pos.z);
        v = rotate(v);

        if (v.z <= nearPlane || v.z > farPlane) return null;

        double h = Trig.cot(fov/2);
        double w = aspect * h;

        double[] ver = {v.x, v.y, v.z};
        double[][] projection = {
                {h, 0, 0},
                {0, w, 0},
                {0, 0, 1}
        };
        double[] pv = Matrix.multiply(projection, ver);

        double xn = pv[0] / v.z;
        double yn = pv[1] / v.z;
        double zn = pv[2];

        double x = (1 + xn) * width / 2;
        double y = (1 - yn) * height / 2;

        return new Vertex3D(x, y, zn);
    }

    private Vertex3D rotate(Vertex3D v) {
        Quaternion q1 = new Quaternion(0, -Trig.sin(yaw/2), 0, Trig.cos(yaw/2)); Quaternion r1 = q1.conjugate();
        Quaternion q2 = new Quaternion(-Trig.sin(pitch/2), 0, 0, Trig.cos(pitch/2)); Quaternion r2 = q2.conjugate();
        Quaternion p = q2.multiply(q1).multiply(new Quaternion(v)).multiply(r1).multiply(r2);
        return new Vertex3D(p);
    }

    public void processKeyboard(double len) {
        boolean keyUp = Keyboard.isKeyDown(KeyEvent.VK_W);
        boolean keyDown = Keyboard.isKeyDown(KeyEvent.VK_S);
        boolean keyLeft = Keyboard.isKeyDown(KeyEvent.VK_A);
        boolean keyRight = Keyboard.isKeyDown(KeyEvent.VK_D);
        boolean flyUp = Keyboard.isKeyDown(KeyEvent.VK_SPACE);
        boolean flyDown = Keyboard.isKeyDown(KeyEvent.VK_SHIFT);

        if (keyUp && !keyDown) {
            pos.x += len*Trig.sin(yaw);
            //pos.y -= len*Trig.sin(pitch);
            pos.z += len*Trig.cos(yaw);
        }
        if (keyDown && !keyUp) {
            pos.x -= len*Trig.sin(yaw);
            //pos.y += len*Trig.sin(pitch);
            pos.z -= len*Trig.cos(yaw);
        }
        if (keyLeft && !keyRight) {
            pos.x -= len*Trig.cos(yaw);
            pos.z += len*Trig.sin(yaw);
        }
        if (keyRight && !keyLeft) {
            pos.x += len*Trig.cos(yaw);
            pos.z -= len*Trig.sin(yaw);
        }
        if (flyUp && !flyDown) {
            pos.y += len;
        }
        if (flyDown && !flyUp) {
            pos.y -= len;
        }
    }

    public void processMouse(double dx, double dy) {
        dx *= 0.16;
        dy *= 0.16;

        if (yaw + dx >= 360) yaw += dx - 360;
        else if (yaw + dx < 0) yaw = 360 - yaw + dx;
        else yaw += dx;

        if (pitch + dy < -90) pitch = -90;
        else if (pitch + dy > 90) pitch = 90;
        else pitch += dy;
    }
}
