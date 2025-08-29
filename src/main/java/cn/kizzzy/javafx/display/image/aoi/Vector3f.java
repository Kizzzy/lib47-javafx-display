package cn.kizzzy.javafx.display.image.aoi;

public class Vector3f extends Vector2f {
    
    private float z;
    
    public Vector3f(float x) {
        this(x, 0);
    }
    
    public Vector3f(float x, float y) {
        this(x, y, 0);
    }
    
    public Vector3f(float x, float y, float z) {
        super(x, y);
        this.z = z;
    }
    
    public float getZ() {
        return z;
    }
    
    public void setZ(float z) {
        this.z = z;
    }
}
