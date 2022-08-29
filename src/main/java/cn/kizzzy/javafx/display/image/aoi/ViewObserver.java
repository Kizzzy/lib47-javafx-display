package cn.kizzzy.javafx.display.image.aoi;

public interface ViewObserver {
    
    /**
     * call when object enter view
     */
    void enterView(Element object);
    
    /**
     * call when object change view
     */
    void changeView(Element object);
    
    /**
     * call when object leave view
     */
    void leaveView(Element object);
}
