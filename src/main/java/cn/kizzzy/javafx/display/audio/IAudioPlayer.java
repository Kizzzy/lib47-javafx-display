package cn.kizzzy.javafx.display.audio;

import java.io.InputStream;

public interface IAudioPlayer {
    
    void play(String filePath);
    
    void play(final InputStream stream);
    
    void pause();
    
    void resume();
    
    void stop();
    
    void terminate();
    
    boolean isPlaying();
    
    void addListener(IProgressListener listener);
    
    interface IProgressListener {
        
        void onProgress(double curr, double total);
    }
}
