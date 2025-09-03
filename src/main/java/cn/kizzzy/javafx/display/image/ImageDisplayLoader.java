package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ImageDisplayLoader extends DisplayLoader {
    
    Logger logger = LoggerFactory.getLogger(ImageDisplayLoader.class);
    
    default ImageArg loadImage(IPackage vfs, int id) throws Exception {
        return loadImage(vfs, (Leaf) vfs.getNode(id));
    }
    
    default ImageArg loadImage(IPackage vfs, String path) throws Exception {
        Leaf leaf = vfs.getLeaf(path);
        if (leaf == null) {
            logger.info("leaf is not found: {}", path);
            return null;
        }
        return loadImage(vfs, leaf);
    }
    
    ImageArg loadImage(IPackage vfs, Leaf leaf) throws Exception;
}
