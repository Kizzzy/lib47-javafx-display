package cn.kizzzy.javafx.display.pdf;

import cn.kizzzy.io.IFullyReader;
import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@DisplayLoaderAttribute(suffix = {
    "pdf",
})
public class PdfDisplay implements PdfDisplayLoader {
    @Override
    public PdfArg loadPdf(IPackage vfs, Leaf leaf) throws Exception {
        return new PdfArg() {
            @Override
            public InputStream getInput() throws Exception {
                try (IFullyReader reader = vfs.getInputStreamGetter(leaf.path).getInput()) {
                    return new ByteArrayInputStream(reader.readAll());
                }
            }
        };
    }
}
