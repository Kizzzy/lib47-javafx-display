package cn.kizzzy.javafx.display.text;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.javafx.display.Register;
import cn.kizzzy.javafx.display.RegisterAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import javafx.scene.layout.AnchorPane;

@RegisterAttribute(name = "文本")
public class TextRegister implements Register {
    
    @Override
    public boolean acceptDisplay(DisplayLoader loader) {
        return loader instanceof TextDisplayLoader;
    }
    
    @Override
    public AnchorPane createView() {
        return new TextDisplayView();
    }
    
    @Override
    public void show(AnchorPane view, DisplayLoader display, IPackage vfs, Leaf leaf) throws Exception {
        TextDisplayLoader loader = (TextDisplayLoader) display;
        TextArg arg = loader.loadText(vfs, leaf);
        if (arg != null) {
            TextDisplayView displayView = (TextDisplayView) view;
            displayView.show(arg);
        }
    }
}
