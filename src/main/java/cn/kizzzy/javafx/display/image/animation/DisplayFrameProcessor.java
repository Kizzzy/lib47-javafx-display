package cn.kizzzy.javafx.display.image.animation;

import cn.kizzzy.animations.IProcessor;
import cn.kizzzy.animations.StateInfo;
import cn.kizzzy.javafx.display.image.DisplayFrame;
import javafx.application.Platform;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class DisplayFrameProcessor implements IProcessor<DisplayFrame> {
    
    private final Consumer<List<DisplayFrame>> consumer;
    
    private final List<DisplayFrame> frames = new LinkedList<>();
    
    public DisplayFrameProcessor(Consumer<List<DisplayFrame>> consumer) {
        this.consumer = consumer;
    }
    
    @Override
    public void process(StateInfo stateInfo, DisplayFrame value) {
        synchronized (frames) {
            frames.add(value);
        }
    }
    
    public void clearFrame() {
        synchronized (frames) {
            frames.clear();
        }
    }
    
    public void flushFrame() {
        Platform.runLater(() -> {
            List<DisplayFrame> temp = null;
            synchronized (frames) {
                temp = new LinkedList<>(frames);
            }
            consumer.accept(temp);
        });
    }
}
