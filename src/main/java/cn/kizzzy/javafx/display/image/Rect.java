package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Vector4f;

public class Rect extends Vector4f {
    
    public Rect(float x, float y, float z, float w) {
        super(x, y, z, w);
    }
    
    public float getWidth() {
        return getZ();
    }
    
    public void setWidth(float width) {
        setZ(width);
    }
    
    public float getHeight() {
        return getW();
    }
    
    public void setHeight(float height) {
        setW(height);
    }
    
    public float getMinX() {
        return getX();
    }
    
    public float getMinY() {
        return getY();
    }
    
    public float getMaxX() {
        return getMinX() + getWidth();
    }
    
    public float getMaxY() {
        return getMinY() + getHeight();
    }
    
    public float getCenterX() {
        return getMinX() + getWidth() / 2;
    }
    
    public float getCenterY() {
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
