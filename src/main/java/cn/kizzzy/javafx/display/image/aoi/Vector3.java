package cn.kizzzy.javafx.display.image.aoi;

public class Vector3 extends Vector2 {
    
    private int z;
    
    public Vector3(int x) {
        this(x, 0);
    }
    
    public Vector3(int x, int y) {
        this(x, y, 0);
    }
    
    public Vector3(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }
    
    public int getZ() {
        return z;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
}
