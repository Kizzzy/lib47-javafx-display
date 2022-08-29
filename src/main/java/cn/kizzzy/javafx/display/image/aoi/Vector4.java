package cn.kizzzy.javafx.display.image.aoi;

public class Vector4 extends Vector3 {
    
    private int w;
    
    public Vector4(int x) {
        this(x, 0);
    }
    
    public Vector4(int x, int y) {
        this(x, y, 0);
    }
    
    public Vector4(int x, int y, int z) {
        this(x, y, z, 0);
    }
    
    public Vector4(int x, int y, int z, int w) {
        super(x, y, z);
        this.w = w;
    }
    
    public int getW() {
        return w;
    }
    
    public void setW(int w) {
        this.w = w;
    }
}
