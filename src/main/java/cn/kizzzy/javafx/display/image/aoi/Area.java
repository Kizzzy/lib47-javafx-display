package cn.kizzzy.javafx.display.image.aoi;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Area {
    
    private final int id;
    
    private int indexX;
    private int indexY;
    
    private List<Area> aoi;
    
    private Map<Integer, Element> frameKvs
        = new HashMap<>();
    
    public Area(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public int getIndexX() {
        return indexX;
    }
    
    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }
    
    public int getIndexY() {
        return indexY;
    }
    
    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }
    
    public List<Area> getAoi() {
        return aoi;
    }
    
    public void setAoi(List<Area> aoi) {
        this.aoi = aoi;
    }
    
    public void add(Element element) {
        frameKvs.put(element.getId(), element);
    }
    
    public void remove(Element element) {
        frameKvs.remove(element.getId());
    }
    
    public Collection<? extends Element> getAll() {
        return frameKvs.values();
    }
}