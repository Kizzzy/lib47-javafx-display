package cn.kizzzy.javafx.display.image.getter;

import cn.kizzzy.javafx.display.image.FrameElement;
import cn.kizzzy.javafx.display.image.Track;
import cn.kizzzy.javafx.display.image.aoi.Element;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SliceImageGetter implements IImageGetter<Element> {
    
    private final BufferedImage bufferedImage;
    
    private final int blockX;
    
    private final int blockY;
    
    private final int countX;
    
    private final int countY;
    
    private Map<Integer, Element> elementKvs
        = new HashMap<>();
    
    public SliceImageGetter(BufferedImage bufferedImage, int blockX, int blockY) {
        this.bufferedImage = bufferedImage;
        this.blockX = blockX;
        this.blockY = blockY;
        this.countX = (int) Math.ceil(bufferedImage.getWidth() * 1f / blockX);
        this.countY = (int) Math.ceil(bufferedImage.getHeight() * 1f / blockY);
    }
    
    @Override
    public List<Element> getImage(float x, float y, float width, float height) {
        int minX = (int) Math.floor(x / blockX);
        int minY = (int) Math.floor(y / blockY);
        
        int maxX = (int) Math.ceil((x + width) / blockX);
        int maxY = (int) Math.ceil((y + height) / blockY);
        
        final List<Element> images = new LinkedList<>();
        
        for (int i = minX; i < maxX; ++i) {
            for (int j = minY; j < maxY; ++j) {
                
                Element _image = elementKvs.get(id(i, j));
                if (_image == null) {
                    int sx = i * blockX;
                    int sy = j * blockY;
                    int sw = Math.min(blockX, bufferedImage.getWidth() - sx);
                    int sh = Math.min(blockY, bufferedImage.getHeight() - sy);
                    
                    BufferedImage subImage = bufferedImage.getSubimage(sx, sy, sw, sh);
                    if (subImage != null) {
                        Track.StaticFrame frame = new Track.StaticFrame();
                        frame.x = sx;
                        frame.y = sy;
                        frame.width = sw;
                        frame.height = sh;
                        frame.image = subImage;
                        
                        _image = new FrameElement(id(i, j), frame, 0, true);
                        elementKvs.put(id(i, j), _image);
                    }
                }
                
                if (_image != null) {
                    images.add(_image);
                }
            }
        }
        
        return images;
    }
    
    private int id(int x, int y) {
        return y * blockX + x;
    }
    
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
    
    public int getBlockX() {
        return blockX;
    }
    
    public int getBlockY() {
        return blockY;
    }
    
    public int getCountX() {
        return countX;
    }
    
    public int getCountY() {
        return countY;
    }
}
