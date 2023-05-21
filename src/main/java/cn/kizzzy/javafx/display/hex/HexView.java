package cn.kizzzy.javafx.display.hex;

import cn.kizzzy.javafx.JavafxControlParameter;
import cn.kizzzy.javafx.JavafxControl;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

abstract class HexViewBase extends AnchorPane implements JavafxControl {
    
    @FXML
    protected ListView<String> line_number_lsv;
    
    @FXML
    protected TextArea hex_view_txa;
    
    @FXML
    protected TextArea ascii_view_txa;
    
    @FXML
    protected ScrollBar progress_slb;
    
    public HexViewBase() {
        init();
    }
}

@JavafxControlParameter(fxml = "/fxml/custom/display/hex_view.fxml")
public class HexView extends HexViewBase implements Initializable {
    
    private static final int line_size = 16;
    
    private int line = 16;
    
    private int start;
    
    private IntegerProperty property;
    
    private HexArg arg;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        property = new SimpleIntegerProperty();
        property.addListener(((observable, oldValue, newValue) -> {
            start = newValue.intValue();
            showImpl();
        }));
        
        progress_slb.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (arg == null) {
                return;
            }
            
            int totalLine = arg.length() / line_size;
            int position = (int) (newValue.doubleValue() / 100 * totalLine);
            property.set(position * line_size);
        }));
        
        hex_view_txa.heightProperty().addListener(((observable, oldValue, newValue) -> {
            line = (int) (newValue.doubleValue() / 20);
            showImpl();
        }));
    }
    
    public void show(HexArg arg) {
        reset();
        this.arg = arg;
        showImpl();
    }
    
    private void reset() {
        start = 0;
        line_number_lsv.getItems().clear();
        hex_view_txa.clear();
        ascii_view_txa.clear();
        progress_slb.adjustValue(0);
    }
    
    private void showImpl() {
        if (arg == null) {
            return;
        }
        
        int end = Math.min(start + line_size * line, arg.length());
        byte[] data = arg.getData(start, end - start);
        if (data == null) {
            return;
        }
        
        line_number_lsv.getItems().clear();
        for (int addr = start; addr < end; addr += line_size) {
            line_number_lsv.getItems().add(String.format("%08Xh", addr));
        }
        
        StringBuilder hexBuilder = new StringBuilder();
        StringBuilder asciiBuilder = new StringBuilder();
        
        for (int i = start; i < end; ++i) {
            byte value = data[i - start];
            hexBuilder.append(String.format("%02X", value));
            
            asciiBuilder.append((32 <= value && value < 127) ? (char) value : ".");
            
            if (i != end - 1) {
                hexBuilder.append(" ");
            }
            if ((i + 1) % line_size == 0) {
                hexBuilder.append("\r\n");
                asciiBuilder.append("\r\n");
            }
        }
        hex_view_txa.setText(hexBuilder.toString());
        ascii_view_txa.setText(asciiBuilder.toString());
    }
}
