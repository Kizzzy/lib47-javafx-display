package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.io.IFullyReader;
import cn.kizzzy.javafx.custom.CustomControlParamter;
import cn.kizzzy.javafx.custom.ICustomControl;
import cn.kizzzy.javafx.display.Stoppable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

abstract class AudioDisplayViewBase extends AnchorPane implements ICustomControl {
    
    @FXML
    protected ListView<AudioArg> playlist;
    
    @FXML
    protected ProgressBar progress_pgb;
    
    @FXML
    protected Button prev_btn;
    
    @FXML
    protected Button play_btn;
    
    @FXML
    protected Button next_btn;
    
    public AudioDisplayViewBase() {
        this.init();
    }
}

@CustomControlParamter(fxml = "/fxml/custom/display/display_audio_view.fxml")
public class AudioDisplayView extends AudioDisplayViewBase implements Initializable, Stoppable {
    
    private int index;
    
    private IAudioPlayer player;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        player = new Goxr3plusAudioPlayer();
        player.addListener((curr, total) -> {
            Platform.runLater(() -> {
                progress_pgb.setProgress(curr / total);
            });
        });
        
        prev_btn.setOnAction(this::doPrev);
        play_btn.setOnAction(this::doPlay);
        next_btn.setOnAction(this::doNext);
        
        playlist.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                playImpl(playlist.getSelectionModel().getSelectedIndex());
            }
        });
    }
    
    @Override
    public void stop() {
        player.terminate();
    }
    
    public void show(AudioArg audioArg) {
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
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.resume();
        }
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
        player.stop();
        
        try (IFullyReader reader = audioArg.getStreamGetter().getInput();
             InputStream stream = new ByteArrayInputStream(reader.readAll())) {
            player.play(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
