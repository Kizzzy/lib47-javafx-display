package cn.kizzzy.javafx.display.image.aoi;

import java.util.LinkedList;
import java.util.List;

public class AoiMap {
    
    private final Areas areas;
    
    public AoiMap(int width, int height, int gridX, int gridY) {
        areas = new Areas(width, height, gridX, gridY);
    }
    
    public List<Area> getAreas(Vector4f r) {
        return areas.getAreas(r);
    }
    
    private Area getCurrentArea(Element element) {
        return getCurrentArea(element.getPosition());
    }
    
    private Area getCurrentArea(Vector3 position) {
        return areas.FindArea(position.getX(), position.getY());
    }
    
    public void add(Element element) {
        getCurrentArea(element).add(element);
        enterView(element);
    }
    
    public void remove(Element element) {
        getCurrentArea(element).remove(element);
        leaveView(element);
    }
    
    public List<Element> getElement(Element element) {
        List<Element> list = new LinkedList<>();
        for (Area area : getCurrentArea(element).getAoi()) {
            list.addAll(area.getAll());
        }
        return list;
    }
    
    public void enterView(Element element) {
        List<Element> list = getElement(element);
        enterView(list, element);
    }
    
    private void enterView(List<Element> list, Element target) {
        for (Element object : list) {
            object.enterView(target);
            target.enterView(object);
        }
    }
    
    public void changeView(Element element) {
        List<Element> list = getElement(element);
        changeView(list, element);
    }
    
    private void changeView(List<Element> list, Element target) {
        for (Element object : list) {
            object.changeView(target);
        }
    }
    
    public void leaveView(Element element) {
        List<Element> list = getElement(element);
        leaveView(list, element);
    }
    
    private void leaveView(List<Element> list, Element target) {
        for (Element object : list) {
            object.leaveView(target);
            target.leaveView(object);
        }
    }
    
    public void move(Element element, Vector3 position) {
        Area oldArea = getCurrentArea(element);
        Area newArea = getCurrentArea(position);
        element.setPosition(position);
        
        if (oldArea != newArea) {
            List<Area> oldAreas = oldArea.getAoi();
            List<Area> newAreas = newArea.getAoi();
            
            List<Element> list = new LinkedList<>();
            
            List<Area> subAreas = AoiHelper.getDiffAreas(newAreas, oldAreas);
            for (Area area : subAreas) {
                list.addAll(area.getAll());
            }
            leaveView(list, element);
            oldArea.remove(element);
            
            list.clear();
            List<Area> addAreas = AoiHelper.getDiffAreas(oldAreas, newAreas);
            for (Area area : addAreas) {
                list.addAll(area.getAll());
            }
            enterView(list, element);
            newArea.add(element);
        }
    }
}
