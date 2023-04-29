package cn.kizzzy.javafx.display.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class AudioPlayer implements IAudioPlayer, LineListener {
    
    private volatile SourceDataLine audioClip;
    
    private IProgressListener listener;
    
    public void play(String filePath) {
        final File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            play(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void play(final InputStream stream) {
        try (final AudioInputStream audioIs = AudioSystem.getAudioInputStream(stream)) {
            final AudioFormat format = getOutFormat(audioIs.getFormat());
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            
            audioClip = (SourceDataLine) AudioSystem.getLine(info);
            audioClip.addLineListener(this);
            audioClip.open(format);
            audioClip.start();
            
            new Thread(() -> {
                try (final AudioInputStream stream1 = AudioSystem.getAudioInputStream(format, audioIs)) {
                    final byte[] buffer = new byte[4096];
                    for (int n = 0; n != -1 && audioClip != null; n = stream1.read(buffer, 0, buffer.length)) {
                        audioClip.write(buffer, 0, n);
                        if (listener != null) {
                            listener.onProgress(
                                audioClip.getMicrosecondPosition(),
                                (long) (buffer.length / format.getFrameSize() * format.getFrameRate())
                            );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stop();
                }
                
            }).start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (LineEvent.Type.OPEN.equals(type)) {
        
        } else if (LineEvent.Type.START.equals(type)) {
        
        } else if (LineEvent.Type.STOP.equals(type)) {
        
        } else if (LineEvent.Type.CLOSE.equals(type)) {
        
        }
    }
    
    @Override
    public void pause() {
    
    }
    
    @Override
    public void resume() {
    
    }
    
    @Override
    public void stop() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
            audioClip.removeLineListener(this);
            audioClip = null;
        }
    }
    
    @Override
    public boolean isPlaying() {
        return audioClip != null && audioClip.isRunning();
    }
    
    @Override
    public void addListener(IProgressListener listener) {
        this.listener = listener;
    }
    
    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }
}
