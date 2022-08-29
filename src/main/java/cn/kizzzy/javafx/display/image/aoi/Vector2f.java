package cn.kizzzy.javafx.display.image.aoi;

public class Vector2f {
    
    private double x;
    
    private double y;
    
    public Vector2f(double x) {
        this(x, 0);
    }
    
    public Vector2f(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
}
