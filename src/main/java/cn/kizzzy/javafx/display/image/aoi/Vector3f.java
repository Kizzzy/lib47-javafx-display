package cn.kizzzy.javafx.display.image.aoi;

public class Vector3f extends Vector2f {
    
    private double z;
    
    public Vector3f(double x) {
        this(x, 0);
    }
    
    public Vector3f(double x, double y) {
        this(x, y, 0);
    }
    
    public Vector3f(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
}
