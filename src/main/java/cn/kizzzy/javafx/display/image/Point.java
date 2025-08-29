package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Vector2f;

public class Point extends Vector2f {
    
    public Point(float x, float y) {
        super(x, y);
    }
    
    public Point subtract(Point p) {
        return new Point(getX() - p.getX(), getY() - p.getY());
    }
}
