package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.javafx.custom.CustomControlParamter;
import cn.kizzzy.javafx.custom.ICustomControl;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.javafx.display.DisplayViewAdapter;
import cn.kizzzy.javafx.display.DisplayViewAttribute;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
@DisplayViewAttribute(type = DisplayType.SHOW_AUDIO, title = "音效")
@CustomControlParamter(fxml = "/fxml/custom/display/display_audio_view.fxml")
public class AudioDisplayView extends DisplayViewAdapter implements ICustomControl, Initializable {
    
    @FXML
    private Button play_btn;
    
    private byte[] audioData;
    
    public AudioDisplayView() {
        this.init();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        play_btn.setOnAction(this::doPlay);
    }
    
    public void show(Object data) {
        audioData = (byte[]) data;
    }
    
    @FXML
    protected void doPlay(ActionEvent actionEvent) {
        AudioPlayer audioPlayer = new AudioPlayer();
        try (InputStream stream = new ByteArrayInputStream(audioData)) {
            audioPlayer.play(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
