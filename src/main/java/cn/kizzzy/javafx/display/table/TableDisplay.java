package cn.kizzzy.javafx.display.table;

import cn.kizzzy.data.FieldsFile;
import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

@DisplayLoaderAttribute(suffix = {
    "txt"
}, priority = 1)
public class TableDisplay implements TableDisplayLoader {
    
    @Override
    public TableArg loadTable(IPackage vfs, Leaf leaf) throws Exception {
        TableArg arg = new TableArg();
        arg.tableFile = vfs.load(leaf.path, FieldsFile.class);
        return arg.tableFile == null ? null : arg;
    }
}
