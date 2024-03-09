package cn.kizzzy.javafx.display.pdf;

import cn.kizzzy.javafx.display.DisplayLoader;
import cn.kizzzy.javafx.display.Register;
import cn.kizzzy.javafx.display.RegisterAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;
import javafx.scene.layout.AnchorPane;

@RegisterAttribute(name = "Pdf")
public class PdfRegister implements Register {
    @Override
    public boolean acceptDisplay(DisplayLoader loader) {
        return loader instanceof PdfDisplayLoader;
    }
    
    @Override
    public AnchorPane createView() {
        return new PdfViewer();
    }
    
    @Override
    public void show(AnchorPane view, DisplayLoader display, IPackage vfs, Leaf leaf) throws Exception {
        PdfDisplayLoader loader = (PdfDisplayLoader) display;
        PdfArg arg = loader.loadPdf(vfs, leaf);
        if (arg != null) {
            PdfViewer displayView = (PdfViewer) view;
            displayView.show(arg);
        }
    }
}
