package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.LinearTangleMod;
import cn.kizzzy.javafx.display.image.Track;
import cn.kizzzy.javafx.display.image.TrackElementBinder;

public class SfLinearTangleMod extends LinearTangleMod<TrackElementBinder> {
    
    @Override
    public TrackElementBinder lerp(TrackElementBinder xBinder, TrackElementBinder yBinder, float rate) {
        Track.StaticFrame x = xBinder.sf;
        Track.StaticFrame y = yBinder.sf;
        
        Track.StaticFrame sf = new Track.StaticFrame();
        sf.layer = x.layer;
        sf.order = x.order;
        
        sf.x = lerp(x.x, y.x, rate);
        sf.y = lerp(x.y, y.y, rate);
        sf.z = lerp(x.z, y.z, rate);
        
        sf.width = lerp(x.width, y.width, rate);
        sf.height = lerp(x.height, y.height, rate);
        
        sf.rotateX = lerp(x.rotateX, y.rotateX, rate);
        sf.rotateY = lerp(x.rotateY, y.rotateY, rate);
        sf.rotateZ = lerp(x.rotateZ, y.rotateZ, rate);
        
        sf.flipX = x.flipX;
        sf.flipY = x.flipY;
        
        sf.mixed = x.mixed;
        sf.image = x.image;
        sf.getter = x.getter;
        sf.extra = x.extra;
        
        return new TrackElementBinder(xBinder.element, sf, null);
    }
}
