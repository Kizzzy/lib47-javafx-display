package cn.kizzzy.javafx.display.hex;

import cn.kizzzy.io.IFullyReader;
import cn.kizzzy.io.SeekType;
import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

@DisplayLoaderAttribute(suffix = {
    "bin",
})
public class HexDisplay implements HexDisplayLoader {
    
    @Override
    public HexArg loadHex(IPackage vfs, Leaf leaf) throws Exception {
        IFullyReader reader = vfs.getInputStreamGetter(leaf.path).getInput();
        return new HexArg() {
            @Override
            public int length() {
                try {
                    return (int) reader.length();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
            
            @Override
            public byte[] getData(int offset, int length) {
                try {
                    reader.seek(offset, SeekType.BEGIN);
                    return reader.readBytes(length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
