package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector3;

public class FrameElement implements Element {
    
    private final int id;
    private final int frameIndex;
    private final boolean keep;
    private final Frame frame;
    
    public FrameElement(int id, Frame frame, int frameIndex, boolean keep) {
        this.id = id;
        this.frame = frame;
        this.frameIndex = frameIndex;
        this.keep = keep;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public boolean visible() {
        return true;
    }
    
    @Override
    public Vector3 getPosition() {
        return new Vector3((int) frame.x, (int) frame.y, 0);
    }
    
    @Override
    public void setPosition(Vector3 position) {
    
    }
    
    @Override
    public void enterView(Element object) {
    
    }
    
    @Override
    public void changeView(Element object) {
    
    }
    
    @Override
    public void leaveView(Element object) {
    
    }
    
    public Frame getFrame() {
        return frame;
    }
    
    public boolean checkIndex(int index) {
        return frameIndex == index || (index > frameIndex && keep);
    }
}
