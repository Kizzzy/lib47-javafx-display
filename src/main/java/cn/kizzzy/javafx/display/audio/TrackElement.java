package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.io.IFullyReader;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class TrackElement {
    
    private AudioArg.Track track;
    
    private Media media;
    private MediaPlayer player;
    
    public TrackElement(AudioArg.Track track) {
        this.track = track;
        
        try {
            // 创建临时文件
            // 设置JVM退出时删除临时文件
            File file = Files.createTempFile("audio_", ".tmp").toFile();
            file.deleteOnExit();
            
            try (IFullyReader reader = track.getter.getInput();
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(reader.readAll());
                
                // 从临时文件创建Media和MediaPlayer
                media = new Media(file.toURI().toURL().toString());
                player = new MediaPlayer(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void play() {
        if (player == null) {
            return;
        }
        if (player.getStatus() != MediaPlayer.Status.PLAYING) {
            player.play();
        }
    }
    
    public void stop() {
        if (player == null) {
            return;
        }
        if (player.getStatus() != MediaPlayer.Status.STOPPED) {
            player.stop();
        }
    }
    
    public long length() {
        if (media == null) {
            return 0L;
        }
        
        long duration = (long) media.getDuration().toSeconds() * 1000;
        if (duration == 0) {
            return 100;
        }
        
        return duration;
    }
}
