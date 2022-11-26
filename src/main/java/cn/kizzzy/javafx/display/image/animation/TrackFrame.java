package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.javafx.display.image.Frame;
import cn.kizzzy.javafx.display.image.TrackElement;

public class TrackFrame {
    
    public final TrackElement element;
    
    public final Frame frame;
    
    public TrackFrame(TrackElement element, Frame frame) {
        this.element = element;
        this.frame = frame;
    }
}
