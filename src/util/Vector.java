/*
 * Copyright (c) 2018 Dimitri Watel
 */

package util;

/**
 * Simple 2D vector class in order to simplify drawing.s
 *
 * Every IP method means "in place" meaning that no new object is build, the vector "this" is modified.
 */
public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector copy(){
        return new Vector(x, y);
    }

    public void copyIP(Vector v){
        x = v.x;
        y = v.y;
    }

    public void reverseIP(){
        x = -x;
        y = -y;
    }

    public void addIP(Vector v){
        x += v.x;
        y += v.y;
    }

    public Vector add(Vector v){
        return new Vector(x + v.x, y + v.y);
    }

    public void diffIP(Vector v){
        x -= v.x;
        y -= v.y;
    }

    public Vector diff(Vector v){
        return new Vector(x - v.x, y - v.y);
    }

    public void multIP(double k){
        x *= k;
        y *= k;
    }

    public Vector mult(double k){
        return new Vector(x * k, y * k);
    }

    public double mag(){
        return Math.hypot(x, y);
    }

    public void normalizeIP(){
        multIP(1/ mag());
    }

    public Vector normalized(){
        return mult(1/ mag());
    }

    public void rotateIP(double alpha){
        double ca = Math.cos(alpha);
        double sa = Math.sin(alpha);
        double x2 = ca * x - sa * y;
        double y2 = sa * x + ca * y;
        x = x2;
        y = y2;
    }

    public Vector rotate(double alpha){
        double ca = Math.cos(alpha);
        double sa = Math.sin(alpha);
        return new Vector(ca * x - sa * y, sa * x + ca * y);
    }

    public double angle(Vector v){
        return Math.atan2(this.y,this.x) - Math.atan2(v.y,v.x);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
