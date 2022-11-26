package cn.kizzzy.javafx.display.image;

import java.util.LinkedList;
import java.util.List;

public class Track {
    
    public LoopType loopType = LoopType.LOOP_EVER;
    
    public boolean liner = false;
    
    public List<Frame> frames
        = new LinkedList<>();
}
