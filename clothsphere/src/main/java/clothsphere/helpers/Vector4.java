package clothsphere.helpers;

import java.util.stream.DoubleStream;

public class Vector4 {
    public  float x = 0;
    public float y = 0;
    public float z = 0;

    public float f = 0;

    public Vector4(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector4(float x, float y, float z, float f){
        this.x = x;
        this.y = y;
        this.z = z;
        this.f = f;
    }

    public DoubleStream getCoordinates() { return DoubleStream.of(x,y,z); }

    public Vector4 add(Vector4 point) {
        return add(point.x, point.y, point.z);
    }

    public Vector4 add(float x, float y, float z){
        return new Vector4(this.x + x, this.y + y, this.z + z);
    }

    public Vector4 substract(Vector4 point) {
        return add(point.x, point.y, point.z);
    }

    public Vector4 substract(float x, float y, float z){
        return new Vector4(this.x - x, this.y - y, this.z - z);
    }

    public Vector4 multiply(float factor) {
        return add(this.x * factor, this.y * factor, this.z * factor);
    }

    public Vector4 divide(float factor){
        return new Vector4(this.x / factor, this.y / factor, this.z / factor);
    }

    public Vector4 normalize(){
        final float mag = magnitude();

        if(mag == 0.0){
            return new Vector4(0f, 0f, 0f);
        }

        return new Vector4(x / mag, y / mag, z / mag);
    }

    public float magnitude() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float dotProduct(Vector4 point) {
        return dotProduct(point.x, point.y, point.z);
    }

    public float dotProduct(float x, float y, float z){
        return this.x * x + this.y * y + this.z * z;
    }

    public Vector4 crossProduct(Vector4 point) {
        return add(point.x, point.y, point.z);
    }

    public Vector4 crossProduct(float x, float y, float z) {
        return new Vector4(-this.z * y + this.y * z,
                this.z * x - this.x * z,
                -this.y * x + this.x * y);
    }

    public float length() {
        return (float)Math.sqrt((x*x)+(y*y)+(z*z));
    }

    public float squaredLength() {
        return (float)(x*x)+(y*y)+(z*z);
    }

    @Override
    public String toString() {
        return "Vector4{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
