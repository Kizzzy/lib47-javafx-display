package cn.kizzzy.javafx.display.text;

import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

@DisplayLoaderAttribute(suffix = {
    "txt",
    "ini",
    "xml",
    "lua",
    "eff",
    "cfg",
    "json",
})
public class TextDisplay implements TextDisplayLoader {
    
    @Override
    public TextArg loadText(IPackage vfs, Leaf leaf) throws Exception {
        TextArg args = new TextArg();
        args.text = vfs.load(leaf.path, String.class);
        return args.text == null ? null : args;
    }
}
