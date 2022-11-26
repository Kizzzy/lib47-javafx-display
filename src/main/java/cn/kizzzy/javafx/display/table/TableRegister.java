package cn.kizzzy.javafx.display.table;

import cn.kizzzy.helper.LogHelper;
import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.javafx.display.Register;
import cn.kizzzy.javafx.display.RegisterAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import javafx.scene.layout.AnchorPane;

@RegisterAttribute(name = "表格")
public class TableRegister implements Register {
    
    @Override
    public boolean acceptDisplay(DisplayLoader loader) {
        return loader instanceof TableDisplayLoader;
    }
    
    @Override
    public AnchorPane createView() {
        return new TableDisplayView();
    }
    
    @Override
    public void show(AnchorPane view, DisplayLoader display, IPackage vfs, Leaf leaf) {
        try {
            TableDisplayLoader loader = (TableDisplayLoader) display;
            TableArg arg = loader.loadTable(vfs, leaf);
            if (arg != null) {
                TableDisplayView displayView = (TableDisplayView) view;
                displayView.show(arg);
            }
        } catch (Exception e) {
            LogHelper.info("show table error: ", e);
        }
    }
}
