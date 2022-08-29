package cn.kizzzy.javafx.display.image.aoi;

import java.util.LinkedList;
import java.util.List;

public class Areas {
    
    private final int width;
    
    private final int height;
    
    private final int gridX;
    
    private final int gridY;
    
    private final int countX;
    
    private final int countY;
    
    private final Area[][] areas;
    
    public Areas(int width, int height, int gridX, int gridY) {
        this.width = width;
        this.height = height;
        this.gridX = gridX;
        this.gridY = gridY;
        this.countX = (int) Math.floor(width / gridX + 1);
        this.countY = (int) Math.floor(height / gridY + 1);
        this.areas = AoiHelper.createAreas(width, height, gridX, gridY);
    }
    
    public Area FindArea(int x, int y) {
        x = Math.max(0, Math.min(width, x));
        y = Math.max(0, Math.min(height, y));
        x = (int) Math.floor(x / gridX);
        y = (int) Math.floor(y / gridY);
        return areas[x][y];
    }
    
    public List<Area> getAreas(Vector4f r) {
        int x = (int) Math.floor(r.getX() / gridX);
        int y = (int) Math.floor(r.getY() / gridY);
        int cx = (int) Math.floor(r.getZ() / gridX) + 1;
        int cy = (int) Math.floor(r.getW() / gridY) + 1;
        List<Area> list = new LinkedList<>();
        for (int i = -1; i <= cx; ++i) {
            for (int j = -1; j <= cy; ++j) {
                int ii = x + i;
                int jj = y + j;
                if (0 <= ii && ii < countX && 0 <= jj && jj < countY) {
                    list.add(areas[ii][jj]);
                }
            }
        }
        return list;
    }
}
