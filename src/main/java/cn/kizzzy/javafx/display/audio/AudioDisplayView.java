package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.io.IFullyReader;
import cn.kizzzy.javafx.custom.CustomControlParamter;
import cn.kizzzy.javafx.custom.ICustomControl;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.javafx.display.DisplayViewAdapter;
import cn.kizzzy.javafx.display.DisplayViewAttribute;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

@DisplayViewAttribute(type = DisplayType.SHOW_AUDIO, title = "音效")
@CustomControlParamter(fxml = "/fxml/custom/display/display_audio_view.fxml")
public class AudioDisplayView extends DisplayViewAdapter implements ICustomControl, Initializable {
    
    @FXML
    private ListView<AudioArg> playlist;
    
    @FXML
    private ProgressBar progress_pgb;
    
    @FXML
    private Button prev_btn;
    
    @FXML
    private Button play_btn;
    
    @FXML
    private Button next_btn;
    
    private int index;
    
    public AudioDisplayView() {
        this.init();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prev_btn.setOnAction(this::doPrev);
        play_btn.setOnAction(this::doPlay);
        next_btn.setOnAction(this::doNext);
        
        playlist.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                playImpl(playlist.getSelectionModel().getSelectedIndex());
            }
        });
    }
    
    public void show(Object data) {
        AudioArg audioArg = (AudioArg) data;
        if (playlist.getItems().size() >= 10) {
            playlist.getItems().remove(0);
        }
        
        playlist.getItems().add(audioArg);
        if (audioArg.isAuto()) {
            playImpl(playlist.getItems().size() - 1);
        }
    }
    
    private void doPrev(ActionEvent actionEvent) {
        if (index > 0) {
            playImpl(index - 1);
        }
    }
    
    private void doPlay(ActionEvent actionEvent) {
        playImpl(index);
    }
    
    private void doNext(ActionEvent actionEvent) {
        if (index < playlist.getItems().size() - 1) {
            playImpl(index + 1);
        }
    }
    
    private void playImpl(int index) {
        if (index >= 0 && index < playlist.getItems().size()) {
            this.index = index;
            AudioArg audioArg = playlist.getItems().get(index);
            playImpl(audioArg);
        }
    }
    
    private void playImpl(AudioArg audioArg) {
        AudioPlayer audioPlayer = new AudioPlayer();
        try (IFullyReader reader = audioArg.getStreamGetter().getInput();
             InputStream stream = new ByteArrayInputStream(reader.readAll())) {
            audioPlayer.play(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
