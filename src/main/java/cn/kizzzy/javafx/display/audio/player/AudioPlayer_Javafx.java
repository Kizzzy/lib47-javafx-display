package cn.kizzzy.javafx.display.audio.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioPlayer_Javafx {
    
    // 轨道ID生成器
    private final AtomicInteger trackIdGenerator
        = new AtomicInteger(0);
    
    // 存储所有音频轨道
    private final Map<Integer, Track> tracks
        = new ConcurrentHashMap<>();
    
    // 播放事件监听器列表
    // 延时播放的调度器
    private final ScheduledExecutorService scheduler
        = Executors.newScheduledThreadPool(1);
    
    // 线程池用于处理播放任务
    private final ExecutorService executorService
        = Executors.newCachedThreadPool();
    
    // 支持的音频格式
    private static final Set<String> SUPPORTED_FORMATS
        = new HashSet<>(Arrays.asList("mp3", "ogg", "wav", "mid", "midi", "flac"));
    
    /**
     * 添加音频轨道
     *
     * @param filePath 音频文件路径
     * @return 轨道ID
     * @throws MalformedURLException         URL格式错误
     * @throws UnsupportedAudioFileException 不支持的音频格式
     */
    public int addTrack(String filePath) throws MalformedURLException, UnsupportedAudioFileException, IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("音频文件不存在: " + filePath);
        }
        
        // 检查文件格式是否支持
        String extension = getFileExtension(file.getName()).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            throw new UnsupportedAudioFileException("不支持的音频格式: " + extension);
        }
        
        int trackId = trackIdGenerator.incrementAndGet();
        
        // FLAC和其他格式使用JavaFX MediaPlayer
        Media media = new Media(file.toURI().toURL().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        
        Track track = new Track(trackId, file.getName(), mediaPlayer, null);
        tracks.put(trackId, track);
        
        return trackId;
    }
    
    /**
     * 从InputStream添加音频轨道
     *
     * @param inputStream 音频数据流
     * @param fileName    文件名（包含扩展名）
     * @return 轨道ID
     * @throws UnsupportedAudioFileException 不支持的音频格式
     * @throws IOException                   IO异常
     */
    public int addTrackFromInputStream(InputStream inputStream, String fileName) throws UnsupportedAudioFileException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }
        
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        // 检查文件格式是否支持
        String extension = getFileExtension(fileName).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            throw new UnsupportedAudioFileException("不支持的音频格式: " + extension);
        }
        
        // 创建临时文件
        Path tempFile = Files.createTempFile("audio_", "." + extension);
        File file = tempFile.toFile();
        // 设置JVM退出时删除临时文件
        file.deleteOnExit();
        
        // 将InputStream内容写入临时文件
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            // 关闭输入流
            inputStream.close();
        }
        
        int trackId = trackIdGenerator.incrementAndGet();
        
        // 从临时文件创建Media和MediaPlayer
        Media media = new Media(file.toURI().toURL().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        
        // 创建轨道并存储临时文件引用以便后续清理
        Track track = new Track(trackId, fileName, mediaPlayer, file);
        tracks.put(trackId, track);
        
        return trackId;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    
    /**
     * 播放指定轨道
     *
     * @param trackId 轨道ID
     */
    public void play(int trackId) {
        Track track = getTrack(trackId);
        executorService.submit(() -> {
            try {
                track.getMediaPlayer().play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    
    /**
     * 延时播放指定轨道
     *
     * @param trackId  轨道ID
     * @param delay    延时时间
     * @param timeUnit 时间单位
     */
    public void playWithDelay(int trackId, long delay, TimeUnit timeUnit) {
        Track track = getTrack(trackId);
        scheduler.schedule(() -> {
            play(trackId);
        }, delay, timeUnit);
    }
    
    /**
     * 暂停指定轨道
     *
     * @param trackId 轨道ID
     */
    public void pause(int trackId) {
        Track track = getTrack(trackId);
        executorService.submit(() -> {
            try {
                track.getMediaPlayer().pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 恢复播放指定轨道
     *
     * @param trackId 轨道ID
     */
    public void resume(int trackId) {
        Track track = getTrack(trackId);
        executorService.submit(() -> {
            try {
                track.getMediaPlayer().play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 停止指定轨道
     *
     * @param trackId 轨道ID
     */
    public void stop(int trackId) {
        Track track = getTrack(trackId);
        executorService.submit(() -> {
            try {
                track.getMediaPlayer().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 停止所有轨道播放
     */
    public void stopAll() {
        tracks.keySet().forEach(this::stop);
    }
    
    /**
     * 设置播放速度
     *
     * @param trackId 轨道ID
     * @param rate    播放速度倍率（0.5-4.0）
     */
    public void setPlaybackRate(int trackId, double rate) {
        // 验证速度范围
        if (rate < 0.5 || rate > 4.0) {
            throw new IllegalArgumentException("播放速度必须在0.5到4.0之间");
        }
        
        Track track = getTrack(trackId);
        executorService.submit(() -> {
            try {
                track.getMediaPlayer().setRate(rate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 指定位置播放
     *
     * @param trackId 轨道ID
     * @param seconds 秒数
     */
    public void seek(int trackId, double seconds) {
        Track track = getTrack(trackId);
        executorService.submit(() -> {
            try {
                track.getMediaPlayer().seek(Duration.seconds(seconds));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 获取当前播放位置（秒）
     *
     * @param trackId 轨道ID
     * @return 当前播放位置
     */
    public double getCurrentTime(int trackId) {
        Track track = getTrack(trackId);
        String extension = getFileExtension(track.getFileName()).toLowerCase();
        
        // APE格式暂不支持获取当前位置
        if ("ape".equals(extension)) {
            return 0;
        }
        
        return track.getMediaPlayer().getCurrentTime().toSeconds();
    }
    
    /**
     * 获取音频总时长（秒）
     *
     * @param trackId 轨道ID
     * @return 总时长
     */
    public double getTotalDuration(int trackId) {
        Track track = getTrack(trackId);
        String extension = getFileExtension(track.getFileName()).toLowerCase();
        
        // APE格式暂不支持获取时长
        if ("ape".equals(extension)) {
            return 0;
        }
        
        Media media = track.getMediaPlayer().getMedia();
        return media.getDuration().toSeconds();
    }
    
    
    /**
     * 获取所有轨道ID
     *
     * @return 轨道ID列表
     */
    public List<Integer> getTrackIds() {
        return new ArrayList<>(tracks.keySet());
    }
    
    /**
     * 移除指定轨道
     *
     * @param trackId 轨道ID
     */
    public void removeTrack(int trackId) {
        // 先停止播放
        stop(trackId);
        
        Track track = tracks.remove(trackId);
        if (track != null) {
            if (track.getMediaPlayer() != null) {
                track.getMediaPlayer().dispose();
            }
            // 清理临时文件（如果存在）
            if (track.getTempFile() != null && track.getTempFile().exists()) {
                try {
                    Files.delete(track.getTempFile().toPath());
                } catch (IOException e) {
                    System.err.println("删除临时文件失败: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 关闭播放器，释放资源
     */
    public void shutdown() {
        stopAll();
        // 释放MediaPlayer资源并清理临时文件
        tracks.values().forEach(track -> {
            if (track.getMediaPlayer() != null) {
                track.getMediaPlayer().dispose();
            }
            // 清理临时文件（如果存在）
            if (track.getTempFile() != null && track.getTempFile().exists()) {
                try {
                    Files.delete(track.getTempFile().toPath());
                } catch (IOException e) {
                    System.err.println("删除临时文件失败: " + e.getMessage());
                }
            }
        });
        tracks.clear();
        scheduler.shutdown();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
    
    /**
     * 获取轨道
     *
     * @param trackId 轨道ID
     * @return 轨道对象
     */
    private Track getTrack(int trackId) {
        Track track = tracks.get(trackId);
        if (track == null) {
            throw new IllegalArgumentException("轨道不存在: " + trackId);
        }
        return track;
    }
    
    /**
     * 内部轨道类，封装单个音频轨道信息
     */
    private static class Track {
        private final int id;
        private final String fileName;
        private final MediaPlayer mediaPlayer;
        private final File tempFile; // 用于存储临时文件引用
        
        public Track(int id, String fileName, MediaPlayer mediaPlayer, File tempFile) {
            this.id = id;
            this.fileName = fileName;
            this.mediaPlayer = mediaPlayer;
            this.tempFile = tempFile;
        }
        
        public int getId() {
            return id;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
        
        public File getTempFile() {
            return tempFile;
        }
    }
    
    /**
     * 导入Duration类，用于JavaFX的时间处理
     */
    private static class Duration {
        public static javafx.util.Duration seconds(double seconds) {
            return javafx.util.Duration.seconds(seconds);
        }
    }
}