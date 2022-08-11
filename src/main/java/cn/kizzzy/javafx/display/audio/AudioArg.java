package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.vfs.IInputStreamGetter;

public class AudioArg {
    
    private String name;
    
    private boolean auto;
    
    private IInputStreamGetter streamGetter;
    
    public AudioArg(String name, boolean auto, IInputStreamGetter streamGetter) {
        this.name = name;
        this.auto = auto;
        this.streamGetter = streamGetter;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isAuto() {
        return auto;
    }
    
    public void setAuto(boolean auto) {
        this.auto = auto;
    }
    
    public IInputStreamGetter getStreamGetter() {
        return streamGetter;
    }
    
    public void setStreamGetter(IInputStreamGetter streamGetter) {
        this.streamGetter = streamGetter;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
