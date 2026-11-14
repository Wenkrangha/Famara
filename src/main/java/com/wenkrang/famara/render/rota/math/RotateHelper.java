package com.wenkrang.famara.render.rota.math;

public class RotateHelper {
    public static double angleToRad(double angle) {
        return angle * Math.PI / 180;
    }

    public static double radToAngle(double rad) {
        return rad * 180 / Math.PI;
    }
}
