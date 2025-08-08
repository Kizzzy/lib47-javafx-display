package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.AnimatorCallback;
import cn.kizzzy.animations.IProcessor;
import cn.kizzzy.animations.StateInfo;
import cn.kizzzy.javafx.display.image.Frame;
import javafx.application.Platform;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class TrackFrameProcessor implements IProcessor<TrackFrame>, AnimatorCallback {
    
    private final Consumer<List<Frame>> consumer;
    
    private final List<Frame> frames = new LinkedList<>();
    
    public TrackFrameProcessor(Consumer<List<Frame>> consumer) {
        this.consumer = consumer;
    }
    
    @Override
    public void process(StateInfo stateInfo, TrackFrame value) {
        value.element.setFrame(value.frame);
        if (value.element.isInRange() && value.element.isValid()) {
            synchronized (frames) {
                frames.add(value.frame);
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
            List<Frame> temp = null;
            synchronized (frames) {
                temp = new LinkedList<>(frames);
            }
            consumer.accept(temp);
        });
    }
}
