package cn.kizzzy.javafx.display.hex;

import cn.kizzzy.helper.LogHelper;
import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.javafx.display.Register;
import cn.kizzzy.javafx.display.RegisterAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import javafx.scene.layout.AnchorPane;

@RegisterAttribute(name = "Hex")
public class HexRegister implements Register {
    
    @Override
    public boolean acceptDisplay(DisplayLoader loader) {
        return loader instanceof HexDisplayLoader;
    }
    
    @Override
    public AnchorPane createView() {
        return new HexView();
    }
    
    @Override
    public void show(AnchorPane view, DisplayLoader display, IPackage vfs, Leaf leaf) {
        try {
            HexDisplayLoader loader = (HexDisplayLoader) display;
            HexArg arg = loader.loadHex(vfs, leaf);
            if (arg != null) {
                HexView displayView = (HexView) view;
                displayView.show(arg);
            }
        } catch (Exception e) {
            LogHelper.info("show text error: ", e);
        }
    }
}
