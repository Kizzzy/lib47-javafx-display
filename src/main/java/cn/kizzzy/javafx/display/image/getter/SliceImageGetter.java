package cn.kizzzy.javafx.display.image.getter;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class SliceImageGetter<Image> implements IImageGetter<Image> {
    
    private final BufferedImage bufferedImage;
    
    private final int blockX;
    
    private final int blockY;
    
    private final int countX;
    
    private final int countY;
    
    public SliceImageGetter(BufferedImage bufferedImage, int blockX, int blockY) {
        this.bufferedImage = bufferedImage;
        this.blockX = blockX;
        this.blockY = blockY;
        this.countX = (int) Math.ceil(bufferedImage.getWidth() * 1f / blockX);
        this.countY = (int) Math.ceil(bufferedImage.getHeight() * 1f / blockY);
    }
    
    @Override
    public List<Image> getImage(double x, double y, double width, double height) {
        int minX = (int) Math.floor(x / blockX);
        int minY = (int) Math.floor(y / blockY);
        
        int maxX = (int) Math.ceil((x + width) / blockX);
        int maxY = (int) Math.ceil((y + height) / blockY);
        
        final List<Image> images = new LinkedList<>();
        
        for (int i = minX; i < maxX; ++i) {
            for (int j = minY; j < maxY; ++j) {
                /*Image _image = imageKvs.get(id(i, j));
                if (_image == null) {
                    int sx = i * blockX;
                    int sy = j * blockY;
                    int sw = Math.min(blockX, getWidth() - sx);
                    int sh = Math.min(blockY, getHeight() - sy);
                    
                    BufferedImage subImage = bufferedImage.getSubimage(sx, sy, sw, sh);
                    if (subImage != null) {
                        _image = new Image(
                            id(i, j),
                            SwingFXUtils.toFXImage(subImage, null),
                            new Rect(sx, sy, sw, sh)
                        );
                        imageKvs.put(id(i, j), _image);
                    }
                }
                
                if (_image != null) {
                    images.add(_image);
                }*/
            }
        }
        
        return images;
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
