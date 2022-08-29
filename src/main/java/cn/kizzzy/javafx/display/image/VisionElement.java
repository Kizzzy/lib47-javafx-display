package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VisionElement implements Element {
    
    private final int id;
    
    private Vector3 position;
    
    private Map<Integer, Element> visibleKvs = new HashMap<>();
    
    public VisionElement(int id, Vector3 position) {
        this.id = id;
        this.position = position;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public boolean visible() {
        return false;
    }
    
    @Override
    public Vector3 getPosition() {
        return position;
    }
    
    @Override
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    
    @Override
    public void enterView(Element object) {
        if (object.visible()) {
            visibleKvs.put(object.getId(), object);
        }
    }
    
    @Override
    public void changeView(Element object) {
    
    }
    
    @Override
    public void leaveView(Element object) {
        if (object.visible()) {
            visibleKvs.remove(object.getId());
        }
    }
    
    public List<Element> getAll() {
        return new LinkedList<>(visibleKvs.values());
    }
}
