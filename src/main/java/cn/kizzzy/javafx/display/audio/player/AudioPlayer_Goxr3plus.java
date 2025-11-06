package cn.kizzzy.javafx.display.audio.player;

import cn.kizzzy.javafx.display.audio.AudioPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class AudioPlayer_Goxr3plus implements AudioPlayer, StreamPlayerListener {
    
    private final StreamPlayer player;
    
    private IProgressListener listener;
    
    public AudioPlayer_Goxr3plus() {
        player = new StreamPlayer();
        player.addStreamPlayerListener(this);
    }
    
    public boolean play(String filePath) {
        final File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            play(fis);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean play(final InputStream stream) {
        try {
            player.open(stream);
            player.play();
            return true;
        } catch (StreamPlayerException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void pause() {
        player.pause();
    }
    
    @Override
    public void resume() {
        player.resume();
    }
    
    @Override
    public void stop() {
        player.stop();
    }
    
    @Override
    public void terminate() {
        stop();
        
        terminateExecutor("streamPlayerExecutorService");
        terminateExecutor("eventsExecutorService");
    }
    
    private void terminateExecutor(String fieldName) {
        Field field = null;
        try {
            field = player.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            
            ExecutorService executorService = (ExecutorService) field.get(player);
            if (executorService != null) {
                executorService.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }
    
    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }
    
    @Override
    public void addListener(IProgressListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void opened(Object o, Map<String, Object> map) {
    
    }
    
    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] bytes, Map<String, Object> map) {
        if (listener != null) {
            listener.onProgress(nEncodedBytes, player.getTotalBytes());
        }
    }
    
    @Override
    public void statusUpdated(StreamPlayerEvent streamPlayerEvent) {
    
    }
}
