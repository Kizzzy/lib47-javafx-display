package cn.kizzzy.javafx.display.image;

public class CanvasDraggingThread extends Thread {
    
    public interface Callback {
        
        void onDragging(Rect startRect, Point diff);
    }
    
    private final Point startPoint;
    private final Rect startRect;
    private final Callback callback;
    
    private Point current;
    
    private volatile boolean valid = true;
    private volatile boolean update;
    private volatile long updateTime;
    
    public CanvasDraggingThread(Point startPoint, Rect startRect, Callback callback) {
        this.startPoint = startPoint;
        this.startRect = startRect;
        this.callback = callback;
    }
    
    public void setCurrent(Point current) {
        this.current = current;
        update = true;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    @Override
    public void run() {
        while (valid) {
            try {
                if (update) {
                    update = false;
                    callback.onDragging(startRect, current.subtract(startPoint));
                }
                Thread.sleep(16);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
