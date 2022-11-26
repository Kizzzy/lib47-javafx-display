package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.helper.LogHelper;
import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.javafx.display.Register;
import cn.kizzzy.javafx.display.RegisterAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import javafx.scene.layout.AnchorPane;

@RegisterAttribute(name = "音效")
public class AudioRegister implements Register {
    
    @Override
    public boolean acceptDisplay(DisplayLoader loader) {
        return loader instanceof AudioDisplayLoader;
    }
    
    @Override
    public AnchorPane createView() {
        return new AudioDisplayView();
    }
    
    @Override
    public void show(AnchorPane view, DisplayLoader display, IPackage vfs, Leaf leaf) {
        try {
            AudioDisplayLoader loader = (AudioDisplayLoader) display;
            AudioArg arg = loader.loadAudio(vfs, leaf);
            if (arg != null) {
                AudioDisplayView displayView = (AudioDisplayView) view;
                displayView.show(arg);
            }
        } catch (Exception e) {
            LogHelper.info("show text error: ", e);
        }
    }
}
