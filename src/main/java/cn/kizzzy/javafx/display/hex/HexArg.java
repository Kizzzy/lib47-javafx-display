package cn.kizzzy.javafx.display.hex;

public interface HexArg {
    
    int length();
    
    byte[] getData(int offset, int length);
}
