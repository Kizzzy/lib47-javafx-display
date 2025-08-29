package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.AnimatorCallback;
import cn.kizzzy.animations.IProcessor;
import cn.kizzzy.animations.StateInfo;
import cn.kizzzy.javafx.display.image.TrackElement;
import cn.kizzzy.javafx.display.image.TrackElementBinder;
import javafx.application.Platform;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SfTrackElementProcessor implements IProcessor<TrackElementBinder>, AnimatorCallback {
    
    private final List<TrackElement> frames
        = new LinkedList<>();
    
    private final Consumer<List<TrackElement>> consumer;
    
    public SfTrackElementProcessor(Consumer<List<TrackElement>> consumer) {
        this.consumer = consumer;
    }
    
    @Override
    public void process(StateInfo stateInfo, TrackElementBinder value) {
        value.element.setStaticFrame(value.sf);
        
        if (value.element.isInRange() && value.element.isValid()) {
            synchronized (frames) {
                frames.add(value.element);
            }
        }
    }
    
    @Override
    public void beforeUpdate() {
        synchronized (frames) {
            frames.clear();
        }
    }
    
    @Override
    public void afterUpdate() {
        Platform.runLater(() -> {
            List<TrackElement> temp = null;
            synchronized (frames) {
                temp = new LinkedList<>(frames);
            }
            consumer.accept(temp);
        });
    }
}
