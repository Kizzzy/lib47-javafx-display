package cn.kizzzy.javafx.display.pdf;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

public interface PdfDisplayLoader extends DisplayLoader {
    
    default PdfArg loadPdf(IPackage vfs, int id) throws Exception {
        return loadPdf(vfs, (Leaf) vfs.getNode(id));
    }
    
    default PdfArg loadPdf(IPackage vfs, String path) throws Exception {
        return loadPdf(vfs, vfs.getLeaf(path));
    }
    
    PdfArg loadPdf(IPackage vfs, Leaf leaf) throws Exception;
}
