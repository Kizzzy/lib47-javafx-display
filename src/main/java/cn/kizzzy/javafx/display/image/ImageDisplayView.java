package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.custom.CustomControlParamter;
import cn.kizzzy.javafx.custom.ICustomControl;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.javafx.display.DisplayViewAdapter;
import cn.kizzzy.javafx.display.DisplayViewAttribute;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

abstract class ImageDisplayViewWrapper extends DisplayViewAdapter implements ICustomControl {
    
    @FXML
    protected CheckBox canvas_black;
    
    @FXML
    protected CheckBox image_filter;
    
    @FXML
    protected Slider slider;
    
    @FXML
    protected HBox color_selector;
    
    @FXML
    protected Canvas canvas;
    
    @FXML
    protected Button play_button;
    
    public ImageDisplayViewWrapper() {
        this.init();
    }
}

@DisplayViewAttribute(type = DisplayType.SHOW_IMAGE, title = "图像")
@CustomControlParamter(fxml = "/fxml/custom/display/display_image_view.fxml")
public class ImageDisplayView extends ImageDisplayViewWrapper implements Initializable {
    
    private int index;
    private int total;
    
    private Timeline timeline;
    
    private DisplayTracks tracks;
    
    private DisplayFrame[] frames;
    
    private Color mixedColor;
    
    private static final String[] DEFAULT_COLORS = new String[]{
        "#000000ff",
        "#0000ffff",
        "#00ff00ff",
        "#ff0000ff",
        "#00ffffff",
        "#ff00ffff",
        "#ffff00ff",
        "#ffffffff"
    };
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas_black.selectedProperty().addListener(this::onBlackChanged);
    }
    
    private void onBlackChanged(Observable observable, boolean oldValue, boolean newValue) {
        showFrames(frames);
    }
    
    @FXML
    private void doPrev(ActionEvent event) {
        index--;
        if (index < 0) {
            index = 0;
        }
        showImpl(index);
    }
    
    @FXML
    private void doNext(ActionEvent event) {
        index++;
        if (index >= total) {
            index = total - 1;
        }
        showImpl(index);
    }
    
    @FXML
    private void doPlay(ActionEvent event) {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
            play_button.setText("播放");
        } else {
            timeline.play();
            play_button.setText("停止");
        }
    }
    
    public void setColors(String... colors) {
        mixedColor = null;
        color_selector.getChildren().clear();
        
        if (colors == null) {
            colors = DEFAULT_COLORS;
        }
        
        for (final String c : colors) {
            Color color = Color.valueOf(c);
            
            Pane pane = new Pane();
            pane.setPrefSize(24, 24);
            pane.setStyle("-fx-background-color: " + c + ";");
            pane.setOnMouseClicked(event -> {
                mixedColor = color;
                showImpl(index);
            });
            
            color_selector.getChildren().add(pane);
        }
    }
    
    public void show(Object data) {
        if (timeline != null) {
            play_button.setText("播放");
            
            timeline.stop();
            timeline = null;
        }
        
        tracks = (DisplayTracks) data;
        frames = new DisplayFrame[tracks.tracks.size()];
        
        index = 0;
        total = 0;
        
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        
        setColors(tracks.colors);
        
        int i = 0;
        for (DisplayTrack track : tracks.tracks) {
            if (track.frames.size() > total) {
                total = track.frames.size();
            }
            for (DisplayFrame frame : track.frames) {
                KeyFrame keyFrame = new KeyFrame(Duration.millis(frame.time), new KeyFrameHandler(i, frame));
                timeline.getKeyFrames().add(keyFrame);
            }
            i++;
        }
        
        showImpl(index);
    }
    
    private void showImpl(int index) {
        int i = 0;
        for (DisplayTrack track : tracks.tracks) {
            if (index <= track.frames.size() - 1) {
                frames[i] = track.frames.get(index);
            } else {
                if (!track.frames.isEmpty() && track.keep) {
                    frames[i] = track.frames.get(track.frames.size() - 1);
                } else {
                    frames[i] = null;
                }
            }
            i++;
        }
        showFrames(frames);
    }
    
    private void showFrames(DisplayFrame[] frames) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        
        if (frames == null || frames.length <= 0) {
            context.clearRect(0, 0, 1920, 1080);
            return;
        }
        
        List<DisplayFrame> list = new LinkedList<>();
        
        float maxWidth = 400, maxHeight = 400;
        for (DisplayFrame frame : frames) {
            if (frame != null) {
                if (frame.x + frame.width > maxWidth) {
                    maxWidth = frame.x + frame.width;
                }
                if (frame.y + frame.height > maxHeight) {
                    maxHeight = frame.y + frame.height;
                }
                
                list.add(frame);
            }
        }
        canvas.setWidth(maxWidth);
        canvas.setHeight(maxHeight);
        
        context.clearRect(0, 0, maxWidth, maxHeight);
        
        if (canvas_black.isSelected()) {
            context.setFill(Color.BLACK);
            context.fillRect(0, 0, maxWidth, maxHeight);
        }
        
        list.sort(Comparator.comparingInt(x -> x.order));
        for (DisplayFrame frame : list) {
            if (frame != null) {
                showFrame(context, frame);
            }
        }
        
        context.setStroke(canvas_black.isSelected() ? Color.WHITE : Color.BLACK);
        context.strokeLine(150, 200, 250, 200);
        context.strokeLine(200, 180, 200, 220);
    }
    
    private void showFrame(GraphicsContext context, DisplayFrame frame) {
        context.save();
        Rotate rotate = new Rotate(frame.rotateZ, frame.x + frame.width / 2, frame.y + frame.height / 2);
        context.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
        
        Image image = SwingFXUtils.toFXImage(frame.image, null);
        
        double dx = frame.x + (frame.flipX ? frame.width : 0);
        double dy = frame.y + (frame.flipY ? frame.height : 0);
        double dw = frame.width * (frame.flipX ? -1 : 1);
        double dh = frame.height * (frame.flipY ? -1 : 1);
        
        if (mixedColor != null && frame.mixed) {
            WritableImage blendImage = new WritableImage((int) frame.width, (int) frame.height);
            
            for (int i = 0; i < frame.height; ++i) {
                for (int j = 0; j < frame.width; ++j) {
                    int argb = image.getPixelReader().getArgb(j, i);
                    int a = (int) (((argb >> 24) & 0xFF) * mixedColor.getOpacity());
                    int r = (int) (((argb >> 16) & 0xFF) * mixedColor.getRed());
                    int g = (int) (((argb >> 8) & 0xFF) * mixedColor.getGreen());
                    int b = (int) (((argb >> 0) & 0xFF) * mixedColor.getBlue());
                    blendImage.getPixelWriter().setArgb(j, i, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }
            image = blendImage;
            
            /*Blend blend1 = new Blend();
            blend1.setMode(BlendMode.SRC_ATOP);
            blend1.setTopInput(new ColorInput(dx, dy, dw, dh, mixedColor));
            blend1.setBottomInput(new ImageInput(image, dx, dy));
            
            Blend blend2 = new Blend();
            blend2.setMode(BlendMode.MULTIPLY);
            blend2.setTopInput(new ImageInput(image, dx, dy));
            blend2.setBottomInput(blend1);
            
            context.applyEffect(blend2);*/
        }
        context.drawImage(image, 0, 0, frame.image.getWidth(), frame.image.getHeight(), dx, dy, dw, dh);
        
        context.restore();
        context.fillText(frame.extra, frame.x, frame.y);
    }
    
    private class KeyFrameHandler implements EventHandler<ActionEvent> {
        private final int i;
        private final DisplayFrame frame;
        
        public KeyFrameHandler(int i, DisplayFrame frame) {
            this.i = i;
            this.frame = frame;
        }
        
        @Override
        public void handle(ActionEvent event) {
            frames[i] = frame;
            showFrames(frames);
        }
    }
}
