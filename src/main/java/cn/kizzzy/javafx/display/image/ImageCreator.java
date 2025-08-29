package cn.kizzzy.javafx.display.image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class ImageCreator {
    
    private Map<Track.StaticFrame, Image> imageKvs;
    
    public ImageCreator(int capacity) {
        if (capacity > 0) {
            imageKvs = new LinkedHashMap<Track.StaticFrame, Image>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > capacity;
                }
            };
        }
    }
    
    public Image createImage(Track.StaticFrame frame, Color mixedColor) {
        Image image = imageKvs != null ? imageKvs.get(frame) : null;
        if (image == null) {
            if (frame.image != null) {
                image = SwingFXUtils.toFXImage(frame.image, null);
            } else if (frame.getter != null) {
                image = frame.getter.getImage();
            }
            if (image != null && imageKvs != null) {
                imageKvs.put(frame, image);
            }
        }
        
        if (image != null && mixedColor != null && frame.mixed) {
            WritableImage blendImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
            
            for (int y = 0; y < (int) image.getHeight(); ++y) {
                for (int x = 0; x < (int) image.getWidth(); ++x) {
                    int argb_old = image.getPixelReader().getArgb(x, y);
                    int a = (int) (((argb_old >> 24) & 0xFF) * mixedColor.getOpacity());
                    int r = (int) (((argb_old >> 16) & 0xFF) * mixedColor.getRed());
                    int g = (int) (((argb_old >> 8) & 0xFF) * mixedColor.getGreen());
                    int b = (int) (((argb_old >> 0) & 0xFF) * mixedColor.getBlue());
                    int argb_new = (a << 24) | (r << 16) | (g << 8) | b;
                    blendImage.getPixelWriter().setArgb(x, y, argb_new);
                }
            }
            image = blendImage;
        }
        
        return image;
    }
}
