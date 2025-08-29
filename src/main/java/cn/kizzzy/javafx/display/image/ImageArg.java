package cn.kizzzy.javafx.display.image;

import java.util.LinkedList;
import java.util.List;

public class ImageArg {
    
    public float pivotX = 200;
    public float pivotY = 200;
    
    public int gridX = 256;
    public int gridY = 256;
    public int gridZ = 256;
    
    public float borderX;
    public float borderY;
    public float borderW;
    public float borderH;
    
    public int cacheSize;
    
    public ImageDrawType drawType
        = ImageDrawType.FULL;
    
    public String[] colors;
    
    public List<Track> tracks
        = new LinkedList<>();
}
