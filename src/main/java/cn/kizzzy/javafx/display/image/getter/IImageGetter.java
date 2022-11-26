package cn.kizzzy.javafx.display.image.getter;

import java.util.List;

public interface IImageGetter<Image> {
    
    List<Image> getImage(double x, double y, double width, double height);
    
}
