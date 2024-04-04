package cn.kizzzy.javafx.display.text;

import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

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
        String text = vfs.load(leaf.path, String.class);
        if (text == null) {
            return null;
        }
        
        if (leaf.path.endsWith(".json") || leaf.path.endsWith(".JSON")) {
            text = JSONObject.toJSONString(JSON.parse(text),
                SerializerFeature.PrettyFormat, SerializerFeature.SortField);
        }
        TextArg args = new TextArg();
        args.text = text;
        return args;
    }
}
