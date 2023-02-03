package cn.kizzzy.javafx.display.hex;

import java.io.IOException;

public interface HexArg {
    
    int length() throws IOException;
    
    byte[] getData(int offset, int length) throws IOException;
}
