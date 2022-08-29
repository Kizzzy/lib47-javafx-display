package cn.kizzzy.javafx.display.image.aoi;

public interface Element extends ViewObserver {
    
    int getId();
    
    boolean visible();
    
    Vector3 getPosition();
    
    void setPosition(Vector3 position);
}
