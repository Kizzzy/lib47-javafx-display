package cn.kizzzy.javafx.display.table;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

public interface TableDisplayLoader extends DisplayLoader {
    
    default TableArg loadTable(IPackage vfs, int id) throws Exception {
        return loadTable(vfs, (Leaf) vfs.getNode(id));
    }
    
    default TableArg loadTable(IPackage vfs, String path) throws Exception {
        return loadTable(vfs, vfs.getLeaf(path));
    }
    
    TableArg loadTable(IPackage vfs, Leaf leaf) throws Exception;
}
