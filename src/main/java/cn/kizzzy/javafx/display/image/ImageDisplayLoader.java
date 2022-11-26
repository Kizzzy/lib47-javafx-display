package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

public interface ImageDisplayLoader extends DisplayLoader {
    
    default ImageArg loadImage(IPackage vfs, int id) throws Exception {
        return loadImage(vfs, (Leaf) vfs.getNode(id));
    }
    
    default ImageArg loadImage(IPackage vfs, String path) throws Exception {
        return loadImage(vfs, vfs.getLeaf(path));
    }
    
    ImageArg loadImage(IPackage vfs, Leaf leaf) throws Exception;
}
