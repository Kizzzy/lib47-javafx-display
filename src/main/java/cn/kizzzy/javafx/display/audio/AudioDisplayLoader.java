package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

public interface AudioDisplayLoader extends DisplayLoader {
    
    default AudioArg loadAudio(IPackage vfs, int id) throws Exception {
        return loadAudio(vfs, (Leaf) vfs.getNode(id));
    }
    
    default AudioArg loadAudio(IPackage vfs, String path) throws Exception {
        return loadAudio(vfs, vfs.getLeaf(path));
    }
    
    AudioArg loadAudio(IPackage vfs, Leaf leaf) throws Exception;
}
