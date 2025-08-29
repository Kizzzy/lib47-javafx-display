package cn.kizzzy.javafx.display.image.aoi;

public class Vector2f {
    
    private float x;
    
    private float y;
    
    public Vector2f(float x) {
        this(x, 0);
    }
    
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public float getX() {
        return x;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }
}
