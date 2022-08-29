package cn.kizzzy.javafx.display.image.aoi;

public class Vector4f extends Vector3f {
    
    private double w;
    
    public Vector4f(double x) {
        this(x, 0);
    }
    
    public Vector4f(double x, double y) {
        this(x, y, 0);
    }
    
    public Vector4f(double x, double y, double z) {
        this(x, y, z, 0);
    }
    
    public Vector4f(double x, double y, double z, double w) {
        super(x, y, z);
        this.w = w;
    }
    
    public double getW() {
        return w;
    }
    
    public void setW(double w) {
        this.w = w;
    }
}
