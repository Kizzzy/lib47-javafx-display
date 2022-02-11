package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.helper.LogHelper;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class AudioPlayer {
    
    public void play(String filePath) {
        final File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            play(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void play(final InputStream stream) {
        new Thread(() -> {
            try (final AudioInputStream in = AudioSystem.getAudioInputStream(stream)) {
                
                final AudioFormat outFormat = getOutFormat(in.getFormat());
                final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
                
                try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                    if (line != null) {
                        line.open(outFormat);
                        line.start();
                        AudioInputStream inputMystream = AudioSystem.getAudioInputStream(outFormat, in);
                        stream(inputMystream, line);
                        line.drain();
                        line.stop();
                    }
                }
                
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                LogHelper.error("play error", e);
            }
        }).start();
    }
    
    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }
    
    private void stream(AudioInputStream in, SourceDataLine line)
        throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
}
