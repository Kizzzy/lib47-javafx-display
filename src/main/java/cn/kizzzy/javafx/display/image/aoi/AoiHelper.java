package cn.kizzzy.javafx.display.image.aoi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AoiHelper {
    
    public static Area[][] createAreas(int width, int height, float gridX, float gridY) {
        int xCount = (int) Math.floor(width / gridX + 1);
        int yCount = (int) Math.floor(height / gridY + 1);
        
        Area[][] areas = new Area[xCount][yCount];
        
        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                Area area = new Area(i * xCount + j);
                area.setIndexX(i);
                area.setIndexY(j);
                
                areas[i][j] = area;
            }
        }
        
        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                Area area = areas[i][j];
                area.setAoi(getAoiAreas(area, areas));
            }
        }
        
        return areas;
    }
    
    public static List<Area> getAoiAreas(Area area, Area[][] areas) {
        int xCount = areas.length;
        int yCount = areas[0].length;
        
        Set<Area> set = new HashSet<>();
        
        int sX = area.getIndexX() - 1;
        if (sX < 0) {
            sX = 0;
        }
        
        int eX = area.getIndexX() + 1;
        if (eX > xCount - 1) {
            eX = xCount - 1;
        }
        
        int sY = area.getIndexY() - 1;
        if (sY < 0) {
            sY = 0;
        }
        
        int eY = area.getIndexY() + 1;
        if (eY > yCount - 1) {
            eY = yCount - 1;
        }
        
        for (int i = sX; i <= eX; ++i) {
            for (int j = sY; j <= eY; ++j) {
                set.add(areas[i][j]);
            }
        }
        return new LinkedList<>(set);
    }
    
    public static List<Area> getDiffAreas(List<Area> baseAreas, List<Area> diffAreas) {
        Map<Integer, Area> map = new HashMap<>();
        for (Area area : baseAreas) {
            map.put(area.getId(), area);
        }
        
        List<Area> list = new LinkedList<>();
        for (Area area : diffAreas) {
            if (!map.containsKey(area.getId())) {
                list.add(area);
            }
        }
        return list;
    }
    
    public static List<Area> getSameAreas(List<Area> baseAreas, List<Area> diffAreas) {
        Map<Integer, Area> map = new HashMap<>();
        for (Area area : baseAreas) {
            map.put(area.getId(), area);
        }
        
        List<Area> list = new LinkedList<>();
        for (Area area : diffAreas) {
            if (map.containsKey(area.getId())) {
                list.add(area);
            }
        }
        return list;
    }
}