package cn.kizzzy.javafx.display.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Track {
    
    public LoopType loopType
        = LoopType.LOOP_EVER;
    
    public List<StaticFrame> sfs
        = new ArrayList<>();
    
    public List<DynamicFrame> dfs
        = new ArrayList<>();
    
    public static class StaticFrame {
        public int layer;
        public int order;
        
        public float x;
        public float y;
        public float z;
        
        public float time = 167;
        
        public float width;
        public float height;
        
        public float rotateX;
        public float rotateY;
        public float rotateZ;
        
        public boolean flipX;
        public boolean flipY;
        
        public boolean mixed;
        
        public BufferedImage image;
        public ImageGetter getter;
        public String extra;
    }
    
    public static class DynamicFrame {
        public int time;
        
        public float x;
        public float y;
        public float z;
        
        public float scale_x = 1.0f;
        public float scale_y = 1.0f;
        
        public float rotate_z;
        
        public float color_r;
        public float color_g;
        public float color_b;
        public float color_a;
    }
}
