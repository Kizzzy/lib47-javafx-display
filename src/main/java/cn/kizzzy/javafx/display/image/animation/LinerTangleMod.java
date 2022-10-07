package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.TangentMode;
import cn.kizzzy.javafx.display.image.DisplayFrame;

public class LinerTangleMod implements TangentMode<DisplayFrame> {
    
    @Override
    public DisplayFrame lerp(DisplayFrame x, DisplayFrame y, float rate) {
        DisplayFrame frame = new DisplayFrame();
        frame.layer = x.layer;
        frame.order = x.order;
        
        frame.x = x.x + (y.x - x.x) / rate;
        frame.y = x.y + (y.y - x.y) / rate;
        frame.z = x.z + (y.z - x.z) / rate;
        
        frame.width = x.width;
        frame.height = x.height;
        
        frame.rotateX = x.rotateX + (y.rotateX - x.rotateX) / rate;
        frame.rotateY = x.rotateY + (y.rotateY - x.rotateY) / rate;
        frame.rotateZ = x.rotateZ + (y.rotateZ - x.rotateZ) / rate;
        
        frame.flipX = x.flipX;
        frame.flipY = x.flipY;
        
        frame.mixed = x.mixed;
        frame.image = x.image;
        frame.getter = x.getter;
        frame.extra = x.extra;
        return frame;
    }
}
