package cn.kizzzy.javafx.display.table;

import cn.kizzzy.data.FieldsFile;
import cn.kizzzy.data.TableFile;
import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.vfs.IPackage;

@DisplayAttribute(suffix = {
    "txt"
}, priority = 1)
public class TableDisplay extends Display<IPackage> {
    
    public TableDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        TableFile<String[]> target = context.load(path, FieldsFile.class);
        return new DisplayAAA(DisplayType.SHOW_TABLE, target);
    }
}
