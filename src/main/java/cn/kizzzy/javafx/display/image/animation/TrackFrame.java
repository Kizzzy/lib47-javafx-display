package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.javafx.display.image.DisplayFrame;
import cn.kizzzy.javafx.display.image.TrackElement;

public class TrackFrame {
    
    public final TrackElement element;
    
    public final DisplayFrame frame;
    
    public TrackFrame(TrackElement element, DisplayFrame frame) {
        this.element = element;
        this.frame = frame;
    }
}
