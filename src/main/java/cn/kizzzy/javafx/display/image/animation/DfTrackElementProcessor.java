package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.IProcessor;
import cn.kizzzy.animations.StateInfo;
import cn.kizzzy.javafx.display.image.TrackElementBinder;

public class DfTrackElementProcessor implements IProcessor<TrackElementBinder> {
    
    public DfTrackElementProcessor() {
    
    }
    
    @Override
    public void process(StateInfo stateInfo, TrackElementBinder value) {
        value.element.setDynamicFrame(value.df);
    }
}
