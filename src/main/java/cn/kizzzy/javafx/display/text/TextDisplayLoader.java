package cn.kizzzy.javafx.display.text;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

public interface TextDisplayLoader extends DisplayLoader {
    
    default TextArg loadText(IPackage vfs, int id) throws Exception {
        return loadText(vfs, (Leaf) vfs.getNode(id));
    }
    
    default TextArg loadText(IPackage vfs, String path) throws Exception {
        return loadText(vfs, vfs.getLeaf(path));
    }
    
    TextArg loadText(IPackage vfs, Leaf leaf) throws Exception;
}
