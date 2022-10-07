package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector3;

public class TrackElement implements Element {
    
    private final int id;
    private final DisplayTrack track;
    
    private DisplayFrame frame;
    
    private boolean inRange;
    
    public TrackElement(int id, DisplayTrack track) {
        this.id = id;
        this.track = track;
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
            return new Vector3((int) track.frames.get(0).x, (int) track.frames.get(0).y, 0);
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
    
    public DisplayFrame getFrame() {
        return frame;
    }
    
    public void setFrame(DisplayFrame frame) {
        this.frame = frame;
    }
}
