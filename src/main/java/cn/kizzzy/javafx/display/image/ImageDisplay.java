package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.javafx.display.Magic;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@DisplayLoaderAttribute(suffix = {
    "bmp",
    "gif",
    "jpg",
    "jpeg",
    "png",
}, magic = {
    Magic.JPG,
    Magic.PNG,
})
public class ImageDisplay implements ImageDisplayLoader {
    
    @Override
    public ImageArg loadImage(IPackage vfs, Leaf leaf) throws Exception {
        byte[] data = vfs.load(leaf.path, byte[].class);
        if (data != null) {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            if (image != null) {
                Frame frame = new Frame();
                frame.x = 0;
                frame.y = 0;
                frame.width = image.getWidth();
                frame.height = image.getHeight();
                frame.image = image;
                
                Track track = new Track();
                track.frames.add(frame);
                
                ImageArg tracks = new ImageArg();
                tracks.tracks.add(track);
                
                return tracks;
            }
        }
        return null;
    }
}
