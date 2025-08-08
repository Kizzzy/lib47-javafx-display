package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector3;

public class TrackElement implements Element {
    
    private final int id;
    private final Track track;
    
    private Frame firstFrame;
    private Frame lastFrame;
    
    private Frame frame;
    
    private boolean inRange;
    private boolean valid = true;
    
    public TrackElement(int id, Track track) {
        this.id = id;
        this.track = track;
        
        firstFrame = track.frames.get(0);
        lastFrame = track.frames.get(track.frames.size() - 1);
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
        if (frame == null) {
            return new Vector3((int) firstFrame.x, (int) firstFrame.y, 0);
        }
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
    
    public boolean isInRange() {
        return inRange;
    }
    
    public void setInRange(boolean inRange) {
        this.inRange = inRange;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public Frame getFrame() {
        return frame;
    }
    
    public void setFrame(Frame frame) {
        if (track.loopType == LoopType.ONCE_FADE && this.frame == lastFrame && frame == firstFrame) {
            valid = false;
        }
        this.frame = frame;
    }
}
