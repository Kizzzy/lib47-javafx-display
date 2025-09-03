package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector3;

public class TrackElement implements Element, Comparable<TrackElement> {
    
    private final int id;
    private final Track track;
    
    private Track.StaticFrame sf_first;
    private Track.StaticFrame sf_last;
    
    private Track.DynamicFrame df_first;
    private Track.DynamicFrame df_last;
    
    private Track.StaticFrame static_frame;
    private Track.DynamicFrame dynamic_frame;
    
    private boolean inRange;
    
    public TrackElement(int id, Track track) {
        this.id = id;
        this.track = track;
        
        if (!track.sfs.isEmpty()) {
            sf_first = track.sfs.get(0);
            sf_last = track.sfs.get(track.sfs.size() - 1);
        }
        
        if (!track.dfs.isEmpty()) {
            df_first = track.dfs.get(0);
            df_last = track.dfs.get(track.dfs.size() - 1);
        }
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
        return new Vector3((int) sf_first.x, (int) sf_first.y, 0);
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
        return static_frame != null;
    }
    
    public Track.StaticFrame getStaticFrame() {
        return static_frame;
    }
    
    public void setStaticFrame(Track.StaticFrame staticFrame) {
        this.static_frame = staticFrame;
    }
    
    public Track.DynamicFrame getDynamicFrame() {
        return dynamic_frame;
    }
    
    public void setDynamicFrame(Track.DynamicFrame dynamicFrame) {
        this.dynamic_frame = dynamicFrame;
    }
    
    @Override
    public int compareTo(TrackElement o) {
        return static_frame.order - o.static_frame.order;
    }
}
