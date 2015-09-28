package org.dimansel.projection3d;

import org.dimansel.math3d.*;

import java.awt.event.KeyEvent;

public class Camera {
    public Vertex3D pos = new Vertex3D(0, 0, 0); //camera position
    public double yaw = 0; //camera orientation
    public double pitch = 0;
    public double roll = 0;
    private int width; //screen width and height
    private int height;
    private double fov; //field of view angle
    private double aspect; //width to height ratio
    private double nearPlane; //z coordinate of clipping planes
    private double farPlane;

    public Camera(int width, int height, double fov, double near, double far)
    {
        this.width = width;
        this.height = height;
        this.fov = fov;
        aspect = width / height;
        nearPlane = near;
        farPlane = far;
    }

    public Vertex3D project(Vertex3D v)
    {
        v = translate(v);
        v = rotate(v);

        if (v.z <= nearPlane || v.z > farPlane) return null;

        double h = Trig.cot(fov/2);
        double w = aspect * h;
        double a = farPlane / (farPlane - nearPlane);
        double b = -nearPlane * farPlane / (farPlane - nearPlane);

        Matrix ver = new Matrix(1, 4, new double[] { v.x, v.y, v.z, 1 });
        Matrix projection = new Matrix(4, 4, new double[] {
                h, 0, 0, 0,
                0, w, 0, 0,
                0, 0, a, b,
                0, 0, 1, 0 });
        Matrix pv = projection.multiply(ver);

        double xn = pv.data[0] / pv.data[3];
        double yn = pv.data[1] / pv.data[3];
        double zn = pv.data[2];

        double x = (1 + xn) * width / 2;
        double y = (1 - yn) * height / 2;

        return new Vertex3D(x, y, zn);
    }

    private Vertex3D translate(Vertex3D v)
    {
        return new Vertex3D(v.x - pos.x, v.y - pos.y, v.z - pos.z);
    }

    private Vertex3D rotate(Vertex3D v)
    {
        Matrix yawRotation = new Matrix(3, 3, new double[] {
                Trig.cos(yaw), 0, -Trig.sin(yaw),
                0, 1, 0,
                Trig.sin(yaw), 0, Trig.cos(yaw) });
        Matrix pitchRotation = new Matrix(3, 3, new double[] {
                1, 0, 0,
                0, Trig.cos(pitch), Trig.sin(pitch),
                0, -Trig.sin(pitch), Trig.cos(pitch) });
        Matrix rollRotation = new Matrix(3, 3, new double[] {
                Trig.cos(roll), Trig.sin(roll), 0,
                -Trig.sin(roll), Trig.cos(roll), 0,
                0, 0, 1 });
        Matrix result = pitchRotation.multiply(yawRotation).multiply(rollRotation).multiply(v.toMatrix());

        return new Vertex3D(result.data[0], result.data[1], result.data[2]);
    }

    public void processKeyboard(double len) {
        boolean keyUp = Keyboard.isKeyDown(KeyEvent.VK_W);
        boolean keyDown = Keyboard.isKeyDown(KeyEvent.VK_S);
        boolean keyLeft = Keyboard.isKeyDown(KeyEvent.VK_A);
        boolean keyRight = Keyboard.isKeyDown(KeyEvent.VK_D);
        boolean flyUp = Keyboard.isKeyDown(KeyEvent.VK_SPACE);
        boolean flyDown = Keyboard.isKeyDown(KeyEvent.VK_SHIFT);

        if (keyUp && !keyDown) {
            pos.x += len*Trig.cos(pitch)*Trig.sin(yaw);
            //pos.y -= len*Trig.sin(pitch);
            pos.z += len*Trig.cos(pitch)*Trig.cos(yaw);
        }
        if (keyDown && !keyUp) {
            pos.x -= len*Trig.cos(pitch)*Trig.sin(yaw);
            //pos.y += len*Trig.sin(pitch);
            pos.z -= len*Trig.cos(pitch)*Trig.cos(yaw);
        }
        if (keyLeft && !keyRight) {
            pos.x -= len*Trig.cos(pitch)*Trig.cos(yaw);
            pos.z += len*Trig.cos(pitch)*Trig.sin(yaw);
        }
        if (keyRight && !keyLeft) {
            pos.x += len*Trig.cos(pitch)*Trig.cos(yaw);
            pos.z -= len*Trig.cos(pitch)*Trig.sin(yaw);
        }
        if (flyUp && !flyDown) {
            pos.y += len;
        }
        if (flyDown && !flyUp) {
            pos.y -= len;
        }
    }

    public void processMouse(double dx, double dy) {
        final int max_yaw = 90;
        final int min_yaw = -90;
        dx *= 0.16;
        dy *= 0.16;

        if (yaw + dx >= 360) {
            yaw += dx - 360;
        } else if (yaw + dx < 0) {
            yaw = 360 - yaw + dx;
        } else {
            yaw += dx;
        }

        if (pitch + dy >= min_yaw && pitch + dy <= max_yaw) {
            pitch += dy;
        } else if (pitch + dy < min_yaw) {
            pitch = min_yaw;
        } else if (pitch + dy > max_yaw) {
            pitch = max_yaw;
        }
    }
}
