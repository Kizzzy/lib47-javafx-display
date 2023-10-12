package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.javafx.display.Register;
import cn.kizzzy.javafx.display.RegisterAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import javafx.scene.layout.AnchorPane;

@RegisterAttribute(name = "图像")
public class ImageRegister implements Register {
    
    @Override
    public boolean acceptDisplay(DisplayLoader loader) {
        return loader instanceof ImageDisplayLoader;
    }
    
    @Override
    public AnchorPane createView() {
        return new ImageDisplayView();
    }
    
    @Override
    public void show(AnchorPane view, DisplayLoader display, IPackage vfs, Leaf leaf) throws Exception {
        ImageDisplayLoader loader = (ImageDisplayLoader) display;
        ImageArg arg = loader.loadImage(vfs, leaf);
        if (arg != null) {
            ImageDisplayView displayView = (ImageDisplayView) view;
            displayView.show(arg);
        }
    }
}
