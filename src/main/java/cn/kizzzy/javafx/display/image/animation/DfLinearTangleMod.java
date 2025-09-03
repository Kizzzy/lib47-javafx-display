package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.LinearTangleMod;
import cn.kizzzy.javafx.display.image.Track;
import cn.kizzzy.javafx.display.image.TrackElementBinder;

public class DfLinearTangleMod extends LinearTangleMod<TrackElementBinder> {
    
    @Override
    public TrackElementBinder lerp(TrackElementBinder xBinder, TrackElementBinder yBinder, float rate) {
        Track.DynamicFrame x = xBinder.df;
        Track.DynamicFrame y = yBinder.df;
        
        Track.DynamicFrame df = new Track.DynamicFrame();
        
        df.x = lerp(x.x, y.x, rate);
        df.y = lerp(x.y, y.y, rate);
        df.z = lerp(x.z, y.z, rate);
        
        df.scale_x = lerp(x.scale_x, y.scale_x, rate);
        df.scale_y = lerp(x.scale_y, y.scale_y, rate);
        
        df.rotate_z = lerp(x.rotate_z, y.rotate_z, rate);
        
        df.color_r = lerp(x.color_r, y.color_r, rate);
        df.color_g = lerp(x.color_g, y.color_g, rate);
        df.color_b = lerp(x.color_b, y.color_b, rate);
        df.color_a = lerp(x.color_a, y.color_a, rate);
        
        return new TrackElementBinder(xBinder.element, null, df);
    }
}
