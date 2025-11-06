package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.animations.AnimationClip;
import cn.kizzzy.animations.AnimationController;
import cn.kizzzy.animations.AnimationCurve;
import cn.kizzzy.animations.AnimationCurveBinding;
import cn.kizzzy.animations.Animator;
import cn.kizzzy.animations.AnimatorPlayer;
import cn.kizzzy.animations.ConstTangentMode;
import cn.kizzzy.animations.KeyFrame;
import cn.kizzzy.animations.LinkedKfEvaluator;
import cn.kizzzy.javafx.JavafxControlParameter;
import cn.kizzzy.javafx.JavafxView;
import cn.kizzzy.javafx.display.Stoppable;
import cn.kizzzy.javafx.display.audio.animations.TrackElementProcessor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

abstract class AudioDisplayViewBase extends JavafxView {
    
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
}

@JavafxControlParameter(fxml = "/fxml/display/audio_view.fxml")
public class AudioDisplayView extends AudioDisplayViewBase implements Initializable, Stoppable {
    
    private int index;
    
    private Animator animator;
    private AnimatorPlayer player;
    
    private TrackElementProcessor processor;
    
    private List<TrackElement> elements;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        processor = new TrackElementProcessor(this::playImpl);
        
        animator = new Animator();
        animator.getStateInfo().callback = processor;
        
        player = new AnimatorPlayer(animator);
        
        prev_btn.setOnAction(this::doPrev);
        play_btn.setOnAction(this::doPlay);
        next_btn.setOnAction(this::doNext);
        
        playlist.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                int index = playlist.getSelectionModel().getSelectedIndex();
                playImpl(index);
            }
        });
    }
    
    @Override
    public void stop() {
        if (player != null) {
            player.stop();
        }
    }
    
    public void show(AudioArg arg) {
        List<TrackElement> elements = new ArrayList<>();
        
        List<AnimationCurveBinding<TrackElement>> curves = new LinkedList<>();
        for (AudioArg.Track track : arg.tracks) {
            TrackElement element = new TrackElement(track);
            elements.add(element);
            
            KeyFrame<TrackElement> kf = new KeyFrame<>();
            kf.time = element.length();
            kf.value = element;
            
            List<KeyFrame<TrackElement>> kfs = new ArrayList<>();
            kfs.add(kf);
            
            curves.add(new AnimationCurveBinding<>(
                new AnimationCurve<>(
                    new LinkedKfEvaluator<>(kfs.toArray(new KeyFrame[]{}), track.time),
                    new ConstTangentMode<>()
                ),
                processor
            ));
        }
        
        this.elements = elements;
        
        animator.setController(new AnimationController(
            new AnimationClip(curves.toArray(new AnimationCurveBinding[]{}))), true);
        animator.setLoop(arg.loop);
        
        if (arg.auto) {
            player.start();
        }
    }
    
    private void doPrev(ActionEvent actionEvent) {
        if (index > 0) {
            playImpl(index - 1);
        }
    }
    
    private void doPlay(ActionEvent actionEvent) {
        if (player.isPlaying()) {
            player.stop();
        } else {
            player.start();
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
            // todo
        }
    }
    
    private void playImpl(Set<TrackElement> currents) {
        for (TrackElement element : this.elements) {
            if (currents.contains(element)) {
                element.play();
            } else {
                element.stop();
            }
        }
    }
}
