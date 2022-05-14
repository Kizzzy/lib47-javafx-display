package cn.kizzzy.javafx.display.image;

import java.util.LinkedList;
import java.util.List;

public class DisplayTracks {
    
    public float pivotX = 200;
    public float pivotY = 200;
    
    public float borderX;
    public float borderY;
    public float borderW;
    public float borderH;
    
    public boolean loop;
    
    public String[] colors;
    
    public List<DisplayTrack> tracks
        = new LinkedList<>();
}
