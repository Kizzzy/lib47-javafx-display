package cn.kizzzy.javafx.display.pdf;

import cn.kizzzy.javafx.JavafxControlParameter;
import cn.kizzzy.javafx.JavafxView;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

abstract class PdfViewerBase extends JavafxView {
    
    @FXML
    protected AnchorPane holder;
}

@JavafxControlParameter(fxml = "/fxml/display/pdf_view.fxml")
public class PdfViewer extends PdfViewerBase implements Initializable {
    
    private PDFDisplayer displayer;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayer = new PDFDisplayer();
        Node node = displayer.toNode();
        holder.getChildren().add(node);
        AnchorPane.setLeftAnchor(node, 0d);
        AnchorPane.setTopAnchor(node, 0d);
        AnchorPane.setRightAnchor(node, 0d);
        AnchorPane.setBottomAnchor(node, 0d);
    }
    
    public void show(PdfArg arg) {
        try (InputStream in = arg.getInput()) {
            displayer.loadPDF(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
