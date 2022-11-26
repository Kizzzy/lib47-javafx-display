package cn.kizzzy.javafx.display.image;

import java.awt.image.BufferedImage;

public class Frame {
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
