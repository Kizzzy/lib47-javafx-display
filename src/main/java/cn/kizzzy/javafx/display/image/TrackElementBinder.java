package cn.kizzzy.javafx.display.image;

public class TrackElementBinder {
    
    public final TrackElement element;
    
    public final Track.StaticFrame sf;
    
    public final Track.DynamicFrame df;
    
    public TrackElementBinder(TrackElement element, Track.StaticFrame sf, Track.DynamicFrame df) {
        this.element = element;
        this.sf = sf;
        this.df = df;
    }
}
