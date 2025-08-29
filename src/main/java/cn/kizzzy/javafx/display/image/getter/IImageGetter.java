package cn.kizzzy.javafx.display.image.getter;

import cn.kizzzy.javafx.display.image.Rect;

import java.util.List;

public interface IImageGetter<Image> {
    
    default List<Image> getImage(Rect r) {
        return getImage(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    List<Image> getImage(float x, float y, float width, float height);
}
