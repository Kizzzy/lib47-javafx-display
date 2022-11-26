package cn.kizzzy.javafx.display.image.getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CacheImageGetter<Image> implements IImageGetter<Image> {
    
    private final IImageGetter<Image> getter;
    
    private final LinkedHashMap<Integer, Image> imageKvs;
    
    public CacheImageGetter(IImageGetter<Image> getter, int capacity) {
        this.getter = getter;
        this.imageKvs = new LinkedHashMap<Integer, Image>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > capacity;
            }
        };
    }
    
    @Override
    public List<Image> getImage(double x, double y, double width, double height) {
        return null;
    }
}
