package cn.kizzzy.javafx.display.hex;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

public interface HexDisplayLoader extends DisplayLoader {
    
    default HexArg loadHex(IPackage vfs, int id) throws Exception {
        return loadHex(vfs, (Leaf) vfs.getNode(id));
    }
    
    default HexArg loadHex(IPackage vfs, String path) throws Exception {
        return loadHex(vfs, vfs.getLeaf(path));
    }
    
    HexArg loadHex(IPackage vfs, Leaf leaf) throws Exception;
}
