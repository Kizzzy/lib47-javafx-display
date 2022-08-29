package cn.kizzzy.javafx.display.image;

public class CanvasResizeThread extends Thread {
    
    public interface Callback {
        
        void onResize(double width, double height);
    }
    
    private Callback callback;
    
    private double width;
    private double height;
    
    private volatile boolean valid = true;
    private volatile boolean update;
    
    public CanvasResizeThread(double width, double height, Callback callback) {
        this.width = width;
        this.height = height;
        this.callback = callback;
    }
    
    public void setWidth(double width) {
        this.width = width;
        update = true;
    }
    
    public void setHeight(double height) {
        this.height = height;
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
                    callback.onResize(width, height);
                }
                Thread.sleep(16);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
