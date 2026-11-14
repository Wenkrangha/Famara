package com.wenkrang.famara.render.rota.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Point {
    private double x;
    private double y;
    private double z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Location location) {
        x = location.getX();
        y = location.getY();
        z = location.getZ();
    }

    public Point(Vector vector) {
        x = vector.getX();
        y = vector.getY();
        z = vector.getZ();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
