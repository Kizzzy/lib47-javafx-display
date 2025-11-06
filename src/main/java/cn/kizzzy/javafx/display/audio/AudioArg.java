package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.vfs.IInputStreamGetter;

import java.util.ArrayList;
import java.util.List;

public class AudioArg {
    
    public static class Track {
        
        public String name;
        
        public int time;
        
        public IInputStreamGetter getter;
    }
    
    public boolean auto;
    
    public boolean loop;
    
    public List<Track> tracks
        = new ArrayList<>();
}
