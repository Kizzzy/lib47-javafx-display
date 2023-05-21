package cn.kizzzy.javafx.display.text;

import cn.kizzzy.helper.StringHelper;
import cn.kizzzy.javafx.JavafxControl;
import cn.kizzzy.javafx.JavafxControlParameter;
import cn.kizzzy.javafx.control.LabeledTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

abstract class TextDisplayViewBase extends AnchorPane implements JavafxControl {
    
    @FXML
    protected LabeledTextField filterText;
    
    @FXML
    protected Button filterButton;
    
    @FXML
    protected TextArea textArea;
    
    public TextDisplayViewBase() {
        this.init();
    }
}

@JavafxControlParameter(fxml = "/fxml/display/text_view.fxml")
public class TextDisplayView extends TextDisplayViewBase implements Initializable {
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterButton.setOnAction(this::doFilter);
    }
    
    public void show(TextArg args) {
        textArea.setText(args.text);
    }
    
    @FXML
    protected void doFilter(ActionEvent actionEvent) {
        String filter = filterText.getContent();
        if (StringHelper.isNotNullAndEmpty((filter))) {
            String content = textArea.getText();
            String[] all = content.split("\n");
            for (int i = 0; i < all.length; i++) {
                if (all[i].contains(filter)) {
                    textArea.setScrollTop(i * 1d * textArea.getHeight() / (textArea.getHeight() / 15));
                    break;
                }
            }
        }
    }
}
