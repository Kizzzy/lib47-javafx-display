package cn.kizzzy.javafx.display.image.getter;

import cn.kizzzy.javafx.display.image.aoi.AoiMap;
import cn.kizzzy.javafx.display.image.aoi.Area;
import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector4f;

import java.util.LinkedList;
import java.util.List;

public class AoiImageGetter implements IImageGetter<Element> {
    
    private final AoiMap map;
    
    public AoiImageGetter(AoiMap map) {
        this.map = map;
    }
    
    @Override
    public List<Element> getImage(double x, double y, double width, double height) {
        final List<Element> images = new LinkedList<>();
        
        List<Area> areas = map.getAreas(new Vector4f(x, y, width, height));
        for (Area area : areas) {
            images.addAll(area.getAll());
        }
        
        return images;
    }
}
