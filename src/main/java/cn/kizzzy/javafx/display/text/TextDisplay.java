package cn.kizzzy.javafx.display.text;

import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.vfs.IPackage;

@DisplayAttribute(suffix = {
    "txt",
    "ini",
    "xml",
    "lua",
    "eff",
    "cfg",
    "json",
})
public class TextDisplay extends Display<IPackage> {
    
    public TextDisplay(IPackage vfs, String path) {
        super(vfs, path);
    }
    
    @Override
    public DisplayAAA load() {
        String target = context.load(path, String.class);
        return new DisplayAAA(DisplayType.SHOW_TEXT, target);
    }
}
