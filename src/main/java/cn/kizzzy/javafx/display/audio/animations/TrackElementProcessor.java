package cn.kizzzy.javafx.display.audio.animations;

import cn.kizzzy.animations.AnimatorCallback;
import cn.kizzzy.animations.IProcessor;
import cn.kizzzy.animations.StateInfo;
import cn.kizzzy.javafx.display.audio.TrackElement;
import javafx.application.Platform;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TrackElementProcessor implements IProcessor<TrackElement>, AnimatorCallback {
    
    private final List<TrackElement> frames
        = new LinkedList<>();
    
    private final Consumer<Set<TrackElement>> consumer;
    
    public TrackElementProcessor(Consumer<Set<TrackElement>> consumer) {
        this.consumer = consumer;
    }
    
    @Override
    public void process(StateInfo stateInfo, TrackElement value) {
        synchronized (frames) {
            frames.add(value);
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
            Set<TrackElement> temp = null;
            synchronized (frames) {
                temp = new HashSet<>(frames);
            }
            consumer.accept(temp);
        });
    }
}
