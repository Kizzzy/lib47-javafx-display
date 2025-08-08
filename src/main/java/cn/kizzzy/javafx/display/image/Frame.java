package cn.kizzzy.javafx.display.image;

import java.awt.image.BufferedImage;

public class Frame {
    public int layer;
    public int order;
    
    public double x;
    public double y;
    public double z;
    
    public double time = 167;
    
    public double width;
    public double height;
    
    public double rotateX;
    public double rotateY;
    public double rotateZ;
    
    public boolean flipX;
    public boolean flipY;
    
    public boolean mixed;
    public boolean empty;
    
    public BufferedImage image;
    public ImageGetter getter;
    public String extra;
}
