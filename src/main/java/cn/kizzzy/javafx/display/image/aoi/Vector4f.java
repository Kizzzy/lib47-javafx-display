package cn.kizzzy.javafx.display.image.aoi;

public class Vector4f extends Vector3f {
    
    private float w;
    
    public Vector4f(float x) {
        this(x, 0);
    }
    
    public Vector4f(float x, float y) {
        this(x, y, 0);
    }
    
    public Vector4f(float x, float y, float z) {
        this(x, y, z, 0);
    }
    
    public Vector4f(float x, float y, float z, float w) {
        super(x, y, z);
        this.w = w;
    }
    
    public float getW() {
        return w;
    }
    
    public void setW(float w) {
        this.w = w;
    }
}
