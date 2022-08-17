package cn.kizzzy.javafx.display.image;

import cn.kizzzy.javafx.custom.CustomControlParamter;
import cn.kizzzy.javafx.custom.ICustomControl;
import cn.kizzzy.javafx.custom.LabeledSlider;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.javafx.display.DisplayViewAdapter;
import cn.kizzzy.javafx.display.DisplayViewAttribute;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
    protected CheckBox black_chk;
    
    @FXML
    protected CheckBox filter_chk;
    
    @FXML
    protected HBox color_hld;
    
    @FXML
    protected LabeledSlider scale_sld;
    
    @FXML
    protected ChoiceBox<DisplayLayer> layer_cob;
    
    @FXML
    protected LabeledSlider layer_sld;
    
    @FXML
    protected Button prev_btn;
    
    @FXML
    protected Button next_btn;
    
    @FXML
    protected Button play_btn;
    
    @FXML
    protected HBox canvas_hld;
    
    @FXML
    protected Canvas canvas;
    
    public ImageDisplayViewWrapper() {
        this.init();
    }
}

@DisplayViewAttribute(type = DisplayType.SHOW_IMAGE, title = "图像")
@CustomControlParamter(fxml = "/fxml/custom/display/display_image_view.fxml")
public class ImageDisplayView extends ImageDisplayViewWrapper implements Initializable {
    
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
    
    private int index;
    private int total;
    
    private Timeline timeline;
    
    private DisplayTracks tracks;
    private DisplayFrame[] frames;
    
    private Color mixedColor;
    
    private IntegerProperty layer;
    
    private Rectangle2D drawRect;
    
    private Point2D start;
    private Rectangle2D startRect;
    private boolean drag = false;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        layer = new SimpleIntegerProperty();
        layer.addListener((observable, oldValue, newValue) -> {
            if (layer_cob.getValue() != DisplayLayer.NONE) {
                showFrames(frames);
            }
        });
        
        black_chk.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showFrames(frames);
        });
        
        layer_cob.getItems().addAll(DisplayLayer.values());
        layer_cob.valueProperty().addListener((observable, oldValue, newValue) -> {
            showFrames(frames);
        });
        
        layer_sld.valueProperty().addListener((observable, oldValue, newValue) -> {
            layer.setValue(newValue);
        });
        
        prev_btn.setOnAction(event -> {
            index--;
            if (index < 0) {
                index = 0;
            }
            showImpl(index);
        });
        
        next_btn.setOnAction(event -> {
            index++;
            if (index >= total) {
                index = total - 1;
            }
            showImpl(index);
        });
        
        play_btn.setOnAction(event -> {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
                play_btn.setText("播放");
            } else {
                timeline.play();
                play_btn.setText("停止");
            }
        });
        
        canvas.setOnDragDetected(event -> canvas.startFullDrag());
        canvas.setOnMouseDragEntered(event -> {
            drag = true;
            start = new Point2D(event.getX(), event.getY());
            startRect = new Rectangle2D(drawRect.getMinX(), drawRect.getMinY(), drawRect.getWidth(), drawRect.getHeight());
        });
        canvas.setOnMouseDragged(event -> {
            if (drag) {
                Point2D curr = new Point2D(event.getX(), event.getY());
                
                double newX = startRect.getMinX() - (curr.getX() - start.getX());
                if (newX < -drawRect.getWidth() / 2) {
                    newX = -drawRect.getWidth() / 2;
                }
                
                double newY = startRect.getMinY() - (curr.getY() - start.getY());
                if (newY < -drawRect.getHeight() / 2) {
                    newY = -drawRect.getHeight() / 2;
                }
                
                drawRect = new Rectangle2D(newX, newY, startRect.getWidth(), startRect.getHeight());
                
                showFrames(frames);
            }
        });
        canvas.setOnMouseDragReleased(event -> {
            drag = false;
            start = null;
            startRect = null;
        });
        
        canvas_hld.widthProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            
            drawRect = new Rectangle2D(drawRect.getMinX(), drawRect.getMinY(), newValue.doubleValue(), drawRect.getHeight());
            
            showFrames(frames);
        });
        canvas_hld.heightProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
            
            drawRect = new Rectangle2D(drawRect.getMinX(), drawRect.getMinY(), drawRect.getWidth(), newValue.doubleValue());
            
            showFrames(frames);
        });
        
        drawRect = new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void setColors(String... colors) {
        mixedColor = null;
        color_hld.getChildren().clear();
        
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
            
            color_hld.getChildren().add(pane);
        }
    }
    
    public void show(Object data) {
        if (timeline != null) {
            play_btn.setText("播放");
            
            timeline.stop();
            timeline = null;
        }
        
        drawRect = new Rectangle2D(0, 0, canvas.getWidth(), canvas.getHeight());
        
        tracks = (DisplayTracks) data;
        frames = new DisplayFrame[tracks.tracks.size()];
        
        index = 0;
        total = 0;
        
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        
        setColors(tracks.colors);
        
        int i = 0;
        int min_layer = Integer.MAX_VALUE, max_layer = Integer.MIN_VALUE;
        for (DisplayTrack track : tracks.tracks) {
            if (track.frames.size() > total) {
                total = track.frames.size();
            }
            for (DisplayFrame frame : track.frames) {
                if (frame.order > max_layer) {
                    max_layer = frame.order;
                }
                if (frame.order < min_layer) {
                    min_layer = frame.order;
                }
                
                KeyFrame keyFrame = new KeyFrame(Duration.millis(frame.time), new KeyFrameHandler(i, frame));
                timeline.getKeyFrames().add(keyFrame);
            }
            i++;
        }
        
        layer_cob.setValue(DisplayLayer.NONE);
        layer_sld.setMin(min_layer);
        layer_sld.setMax(max_layer);
        layer_sld.setValue(min_layer);
        
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
        context.clearRect(0, 0, drawRect.getWidth(), drawRect.getHeight());
        
        if (frames == null || frames.length == 0) {
            return;
        }
        
        drawRect(context, 0, 0, drawRect.getWidth(), drawRect.getHeight());
        drawFrame(context, 1, 1, drawRect.getWidth() - 2, drawRect.getHeight() - 2);
        
        List<DisplayFrame> list = new LinkedList<>();
        for (DisplayFrame frame : frames) {
            if (frame != null) {
                list.add(frame);
            }
        }
        
        list.sort(Comparator.comparingInt(x -> x.order));
        for (DisplayFrame frame : list) {
            if (frame != null && layer_cob.getValue().check(frame.order, layer.getValue())) {
                showFrame(context, frame);
            }
        }
        
        drawPivot(context, tracks.pivotX, tracks.pivotY);
        drawFrame(context, tracks.borderX, tracks.borderY, tracks.borderW, tracks.borderH);
    }
    
    private void showFrame(GraphicsContext context, DisplayFrame frame) {
        double sx = 0;
        double sy = 0;
        double sw = frame.image.getWidth();
        double sh = frame.image.getHeight();
        
        double dx = frame.x + (frame.flipX ? frame.width : 0);
        double dy = frame.y + (frame.flipY ? frame.height : 0);
        double dw = frame.width * (frame.flipX ? -1 : 1);
        double dh = frame.height * (frame.flipY ? -1 : 1);
        
        Rectangle2D imageRect = new Rectangle2D(dx, dy, dw, dh);
        if (drawRect.contains(imageRect)) {
            dx -= drawRect.getMinX();
            dy -= drawRect.getMinY();
            
            context.save();
            Rotate rotate = new Rotate(frame.rotateZ, frame.x + frame.width / 2, frame.y + frame.height / 2);
            context.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
            context.drawImage(createImage(frame), sx, sy, sw, sh, dx, dy, dw, dh);
            context.restore();
            
            drawText(context, frame.extra, dx, dy);
        } else if (drawRect.intersects(imageRect)) {
            if (dx < drawRect.getMinX()) {
                sx = drawRect.getMinX() - dx;
                sw -= sx;
                
                dx = 0;
                dw = sw;
            } else if ((dx + dw) > drawRect.getMaxX()) {
                sx = 0;
                sw = drawRect.getMaxX() - dx;
                
                dx -= drawRect.getMinX();
                dw = sw;
            } else {
                dx -= drawRect.getMinX();
            }
            
            if (dy < drawRect.getMinY()) {
                sy = drawRect.getMinY() - dy;
                sh -= sy;
                
                dy = 0;
                dh = sh;
            } else if ((dy + dh) > drawRect.getMaxY()) {
                sy = 0;
                sh = drawRect.getMaxY() - dy;
                
                dy -= drawRect.getMinY();
                dh = sh;
            } else {
                dy -= drawRect.getMinY();
            }
            
            context.save();
            Rotate rotate = new Rotate(frame.rotateZ, frame.x + frame.width / 2, frame.y + frame.height / 2);
            context.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
            context.drawImage(createImage(frame), sx, sy, sw, sh, dx, dy, dw, dh);
            context.restore();
            
            drawText(context, frame.extra, dx, dy);
        }
    }
    
    private Image createImage(DisplayFrame frame) {
        Image image = SwingFXUtils.toFXImage(frame.image, null);
        
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
        }
        
        return image;
    }
    
    private void drawText(GraphicsContext context, String text, double x, double y) {
        context.setFill(black_chk.isSelected() ? Color.WHITE : Color.BLACK);
        context.fillText(text, x, y);
    }
    
    private void drawRect(GraphicsContext context, int x, int y, double width, double height) {
        if (black_chk.isSelected()) {
            context.setFill(Color.BLACK);
            context.fillRect(x, y, width, height);
        }
    }
    
    private void drawFrame(GraphicsContext context, double x, double y, double w, double h) {
        context.setStroke(black_chk.isSelected() ? Color.WHITE : Color.BLACK);
        context.strokeLine(x, y, x + w, y);
        context.strokeLine(x + w, y, x + w, y + h);
        context.strokeLine(x + w, y + h, x, y + h);
        context.strokeLine(x, y + h, x, y);
    }
    
    private void drawPivot(GraphicsContext context, float pivotX, float pivotY) {
        context.setStroke(black_chk.isSelected() ? Color.WHITE : Color.BLACK);
        context.strokeLine(pivotX - 50, pivotY, pivotX + 50, pivotY);
        context.strokeLine(pivotX, pivotY - 20, pivotX, pivotY + 20);
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
