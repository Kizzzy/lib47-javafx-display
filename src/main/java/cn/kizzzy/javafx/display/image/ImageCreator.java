package cn.kizzzy.javafx.display.image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class ImageCreator {
    
    private Map<Frame, Image> imageKvs;
    
    public ImageCreator(int capacity) {
        if (capacity > 0) {
            imageKvs = new LinkedHashMap<Frame, Image>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > capacity;
                }
            };
        }
    }
    
    public Image createImage(Frame frame, Color mixedColor) {
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
            WritableImage blendImage = new WritableImage((int) frame.width, (int) frame.height);
            
            for (int i = 0; i < frame.height; ++i) {
                for (int j = 0; j < frame.width; ++j) {
                    int argb_old = image.getPixelReader().getArgb(j, i);
                    int a = (int) (((argb_old >> 24) & 0xFF) * mixedColor.getOpacity());
                    int r = (int) (((argb_old >> 16) & 0xFF) * mixedColor.getRed());
                    int g = (int) (((argb_old >> 8) & 0xFF) * mixedColor.getGreen());
                    int b = (int) (((argb_old >> 0) & 0xFF) * mixedColor.getBlue());
                    int argb_new = (a << 24) | (r << 16) | (g << 8) | b;
                    blendImage.getPixelWriter().setArgb(j, i, argb_new);
                }
            }
            image = blendImage;
        }
        
        return image;
    }
}
