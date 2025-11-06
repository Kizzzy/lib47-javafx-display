package cn.kizzzy.javafx.display.audio.player;

import cn.kizzzy.javafx.display.audio.AudioPlayer;

import java.io.InputStream;

public class AudioPlayers implements AudioPlayer {
    
    private AudioPlayer[] players;
    
    public AudioPlayers(AudioPlayer... players) {
        this.players = players;
    }
    
    @Override
    public boolean play(String filePath) {
        for (AudioPlayer player : players) {
            if (player.play(filePath)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean play(InputStream stream) {
        for (AudioPlayer player : players) {
            if (player.play(stream)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void pause() {
        for (AudioPlayer player : players) {
            player.pause();
        }
    }
    
    @Override
    public void resume() {
        for (AudioPlayer player : players) {
            player.resume();
        }
    }
    
    @Override
    public void stop() {
        for (AudioPlayer player : players) {
            player.stop();
        }
    }
    
    @Override
    public void terminate() {
        for (AudioPlayer player : players) {
            player.terminate();
        }
    }
    
    @Override
    public boolean isPlaying() {
        for (AudioPlayer player : players) {
            if (player.isPlaying()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void addListener(IProgressListener listener) {
        // todo
    }
}
