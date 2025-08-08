package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Vector4f;

public class Rect extends Vector4f {
    
    public Rect(double x, double y, double z, double w) {
        super(x, y, z, w);
    }
    
    public double getWidth() {
        return getZ();
    }
    
    public void setWidth(double width) {
        setZ(width);
    }
    
    public double getHeight() {
        return getW();
    }
    
    public void setHeight(double height) {
        setW(height);
    }
    
    public double getMinX() {
        return getX();
    }
    
    public double getMinY() {
        return getY();
    }
    
    public double getMaxX() {
        return getMinX() + getWidth();
    }
    
    public double getMaxY() {
        return getMinY() + getHeight();
    }
    
    public double getCenterX() {
        return getMinX() + getWidth() / 2;
    }
    
    public double getCenterY() {
        return getMinY() + getHeight() / 2;
    }
    
    public boolean contains(Rect r) {
        return r != null &&
            r.getMinX() >= getMinX() &&
            r.getMinY() >= getMinY() &&
            r.getMaxX() <= getMaxX() &&
            r.getMaxY() <= getMaxY();
    }
    
    public boolean intersects(Rect r) {
        return r != null &&
            r.getMaxX() > getMinX() &&
            r.getMaxY() > getMinY() &&
            r.getMinX() < getMaxX() &&
            r.getMinY() < getMaxY();
    }
    
    public Rect move(Rect r) {
        return new Rect(
            getX() + r.getX(),
            getY() + r.getY(),
            getWidth() + r.getWidth(),
            getHeight() + r.getHeight()
        );
    }
}
