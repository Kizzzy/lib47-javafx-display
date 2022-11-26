package cn.kizzzy.javafx.display.image;

import cn.kizzzy.animations.AnimationClip;
import cn.kizzzy.animations.AnimationController;
import cn.kizzzy.animations.AnimationCurve;
import cn.kizzzy.animations.Animator;
import cn.kizzzy.animations.AnimatorPlayer;
import cn.kizzzy.animations.AnimatorUpdateType;
import cn.kizzzy.animations.ConstTangentMode;
import cn.kizzzy.animations.CurveBinding;
import cn.kizzzy.animations.KeyFrame;
import cn.kizzzy.javafx.custom.CustomControlParamter;
import cn.kizzzy.javafx.custom.ICustomControl;
import cn.kizzzy.javafx.custom.LabeledSlider;
import cn.kizzzy.javafx.display.Stoppable;
import cn.kizzzy.javafx.display.image.animation.LinerTangleMod;
import cn.kizzzy.javafx.display.image.animation.TrackFrame;
import cn.kizzzy.javafx.display.image.animation.TrackFrameProcessor;
import cn.kizzzy.javafx.display.image.aoi.AoiMap;
import cn.kizzzy.javafx.display.image.aoi.Area;
import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.aoi.Vector4f;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

abstract class ImageDisplayViewBase extends AnchorPane implements ICustomControl {
    
    @FXML
    protected CheckBox black_chk;
    
    @FXML
    protected CheckBox filter_chk;
    
    @FXML
    protected HBox color_hld;
    
    @FXML
    protected LabeledSlider scale_sld;
    
    @FXML
    protected ChoiceBox<Layer> layer_cob;
    
    @FXML
    protected LabeledSlider layer_sld;
    
    @FXML
    protected Button prev_btn;
    
    @FXML
    protected Button next_btn;
    
    @FXML
    protected Button play_btn;
    
    @FXML
    protected LabeledSlider speed_sld;
    
    @FXML
    protected CheckBox loop_chk;
    
    @FXML
    protected HBox canvas_hld;
    
    @FXML
    protected Canvas canvas;
    
    public ImageDisplayViewBase() {
        this.init();
    }
}

@SuppressWarnings("unchecked")
@CustomControlParamter(fxml = "/fxml/custom/display/display_image_view.fxml")
public class ImageDisplayView extends ImageDisplayViewBase implements Initializable, Stoppable {
    
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
    
    private Color mixedColor;
    
    private IntegerProperty layer;
    
    private Rect drawRect;
    private Vector4f range;
    private CanvasDraggingThread draggingThread;
    
    private AoiMap map;
    
    private ImageArg tracks;
    
    private AnimatorPlayer animatorPlayer;
    private TrackFrameProcessor frameProcessor;
    
    private final List<TrackElement> elements = new LinkedList<>();
    private List<Frame> frames = new LinkedList<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        frameProcessor = new TrackFrameProcessor(this::showFrames);
        
        Animator animator = new Animator();
        animator.getStateInfo().before = frameProcessor::clearFrame;
        animator.getStateInfo().after = frameProcessor::flushFrame;
        animatorPlayer = new AnimatorPlayer(animator);
        
        layer = new SimpleIntegerProperty();
        layer.addListener((observable, oldValue, newValue) -> {
            if (layer_cob.getValue() != Layer.NONE) {
                showImpl();
            }
        });
        
        black_chk.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showImpl();
        });
        
        layer_cob.getItems().addAll(Layer.values());
        layer_cob.valueProperty().addListener((observable, oldValue, newValue) -> {
            showImpl();
        });
        
        layer_sld.valueProperty().addListener((observable, oldValue, newValue) -> {
            layer.setValue(newValue);
        });
        
        prev_btn.setOnAction(event -> {
            animatorPlayer.prev();
        });
        
        next_btn.setOnAction(event -> {
            animatorPlayer.next();
        });
        
        play_btn.setOnAction(event -> {
            if (animatorPlayer.isPlaying()) {
                animatorPlayer.stop();
            } else {
                animatorPlayer.start();
            }
            play_btn.setText(animatorPlayer.isPlaying() ? "暂停" : "播放");
            prev_btn.setDisable(animatorPlayer.isPlaying());
            next_btn.setDisable(animatorPlayer.isPlaying());
        });
        
        speed_sld.valueProperty().addListener((observable, oldValue, newValue) -> {
            animatorPlayer.getAnimator().setSpeed(newValue.floatValue());
        });
        speed_sld.setValue(1);
        
        loop_chk.selectedProperty().addListener((observable, oldValue, newValue) -> {
            animatorPlayer.getAnimator().setLoop(newValue);
        });
        loop_chk.setSelected(true);
        
        canvas.setOnDragDetected(event -> canvas.startFullDrag());
        canvas.setOnMouseDragEntered(event -> {
            if (draggingThread == null) {
                draggingThread = new CanvasDraggingThread(
                    new Point(event.getX(), event.getY()),
                    drawRect,
                    this::onCanvasDragging
                );
                draggingThread.start();
            }
        });
        canvas.setOnMouseDragged(event -> {
            if (draggingThread != null) {
                draggingThread.setCurrent(new Point(event.getX(), event.getY()));
            }
        });
        canvas.setOnMouseDragReleased(event -> {
            if (draggingThread != null) {
                draggingThread.setValid(false);
                draggingThread = null;
            }
        });
        
        canvas_hld.widthProperty().addListener((observable, oldValue, newValue) -> {
            onCanvasResize(canvas_hld.getWidth(), canvas_hld.getHeight());
        });
        canvas_hld.heightProperty().addListener((observable, oldValue, newValue) -> {
            onCanvasResize(canvas_hld.getWidth(), canvas_hld.getHeight());
        });
        
        resetAll();
    }
    
    @Override
    public void stop() {
        if (animatorPlayer != null) {
            animatorPlayer.stop();
        }
        
        if (draggingThread != null) {
            draggingThread.setValid(false);
        }
    }
    
    private void onCanvasDragging(Rect startRect, Point diff) {
        Platform.runLater(() -> {
            double newX = startRect.getMinX() - diff.getX();
            newX = Math.min(Math.max(range.getX(), newX), range.getZ());
            
            double newY = startRect.getMinY() - diff.getY();
            newY = Math.min(Math.max(range.getY(), newY), range.getW());
            
            drawRect = new Rect(newX, newY, startRect.getWidth(), startRect.getHeight());
            
            showImpl(true);
        });
    }
    
    private void onCanvasResize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
        
        drawRect = new Rect(drawRect.getMinX(), drawRect.getMinY(), width, height);
        
        showImpl();
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
                showImpl();
            });
            
            color_hld.getChildren().add(pane);
        }
    }
    
    private void resetAll() {
        canvas.setWidth(canvas_hld.getWidth());
        canvas.setHeight(canvas_hld.getHeight());
        
        drawRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void show(ImageArg tracks) {
        Platform.runLater(() -> {
            showImpl(tracks);
        });
    }
    
    public void showImpl(ImageArg data) {
        resetAll();
        
        tracks = data;
        
        setColors(tracks.colors);
        
        List<Element> elements = new LinkedList<>();
        List<CurveBinding<TrackFrame>> curves = new LinkedList<>();
        
        int id = 0, total = 0;
        float width = 0, height = 0;
        int min_layer = 0, max_layer = 0;
        for (Track track : tracks.tracks) {
            int size = track.frames.size();
            if (size == 0) {
                continue;
            }
            
            total = Math.max(total, size);
            
            TrackElement element = new TrackElement(++id, track);
            elements.add(element);
            
            List<KeyFrame<TrackFrame>> kfs = new LinkedList<>();
            for (Frame frame : track.frames) {
                KeyFrame<TrackFrame> kf = new KeyFrame<>();
                kf.time = (long) frame.time;
                kf.value = new TrackFrame(element, frame);
                kfs.add(kf);
                
                if ((frame.x + frame.width) > width) {
                    width = (frame.x + frame.width);
                }
                if ((frame.y + frame.height) > height) {
                    height = (frame.y + frame.height);
                }
                
                if (frame.order > max_layer) {
                    max_layer = frame.order;
                }
                if (frame.order < min_layer) {
                    min_layer = frame.order;
                }
            }
            
            curves.add(new CurveBinding<>(
                new AnimationCurve<>(
                    kfs.toArray(new KeyFrame[]{}),
                    track.liner ? new LinerTangleMod() : new ConstTangentMode<>()
                ), frameProcessor
            ));
        }
        
        animatorPlayer.getAnimator().setController(new AnimationController(new AnimationClip(
            curves.toArray(new CurveBinding[]{})
        )), true);
        
        if (tracks.drawType == ImageDrawType.FULL) {
            map = new AoiMap((int) width, (int) height, (int) width + 1, (int) height + 1);
        } else {
            map = new AoiMap((int) width, (int) height, tracks.gridX, tracks.gridY);
        }
        for (Element element : elements) {
            map.add(element);
        }
        
        Point p1 = new Point(tracks.pivotX - 200, tracks.pivotY - 200);
        Point p2 = new Point(tracks.pivotX + 200, tracks.pivotY + 200);
        Point p3 = new Point(
            tracks.pivotX + width + 200 - drawRect.getWidth(),
            tracks.pivotY + height + 200 - drawRect.getHeight()
        );
        range = new Vector4f(
            Math.min(Math.min(p1.getX(), p2.getX()), p3.getX()),
            Math.min(Math.min(p1.getY(), p2.getY()), p3.getY()),
            Math.max(Math.max(p1.getX(), p2.getX()), p3.getX()),
            Math.max(Math.max(p1.getY(), p2.getY()), p3.getY())
        );
        
        play_btn.setDisable(total <= 1);
        play_btn.setText(animatorPlayer.isPlaying() ? "暂停" : "播放");
        prev_btn.setDisable(total <= 1);
        next_btn.setDisable(total <= 1);
        
        layer_cob.setValue(Layer.NONE);
        layer_sld.setMin(min_layer);
        layer_sld.setMax(max_layer);
        layer_sld.setValue(min_layer);
        
        showImpl(true);
    }
    
    private void showImpl() {
        showImpl(false);
    }
    
    private void showImpl(boolean reset) {
        if (map == null) {
            return;
        }
        
        if (!reset) {
            showFrames(frames, false);
            return;
        }
        
        for (TrackElement element : elements) {
            element.setInRange(false);
        }
        elements.clear();
        
        for (Area area : map.getAreas(drawRect)) {
            for (Element element : area.getAll()) {
                if (element.visible()) {
                    TrackElement trackElement = (TrackElement) element;
                    trackElement.setInRange(true);
                }
            }
        }
        
        if (!animatorPlayer.isPlaying()) {
            animatorPlayer.getAnimator().update(AnimatorUpdateType.NONE);
        }
    }
    
    private void showFrames(List<Frame> frames) {
        showFrames(frames, true);
    }
    
    private void showFrames(List<Frame> frames, boolean update) {
        if (update) {
            this.frames = frames;
        }
        
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, drawRect.getWidth(), drawRect.getHeight());
        
        if (frames == null || frames.size() == 0) {
            return;
        }
        
        drawRect(context, 0, 0, drawRect.getWidth(), drawRect.getHeight());
        drawFrame(context, 1, 1, drawRect.getWidth() - 2, drawRect.getHeight() - 2);
        
        frames.sort(Comparator.comparingInt(x -> x.order));
        for (Frame frame : frames) {
            if (layer_cob.getValue().check(frame.order, layer.getValue())) {
                showFrame(context, frame);
            }
        }
        
        drawPivot(context, tracks.pivotX, tracks.pivotY);
        drawFrame(context, tracks.borderX, tracks.borderY, tracks.borderW, tracks.borderH);
    }
    
    private void showFrame(GraphicsContext context, Frame frame) {
        Image image = createImage(frame);
        if (image == null) {
            return;
        }
        
        double sx = 0;
        double sy = 0;
        double sw = frame.width;
        double sh = frame.height;
        
        double dx = tracks.pivotX + frame.x + (frame.flipX ? frame.width : 0);
        double dy = tracks.pivotX + frame.y + (frame.flipY ? frame.height : 0);
        double dw = frame.width * (frame.flipX ? -1 : 1);
        double dh = frame.height * (frame.flipY ? -1 : 1);
        
        Rect imageRect = new Rect(dx, dy, dw, dh);
        if (drawRect.contains(imageRect)) {
            dx -= drawRect.getMinX();
            dy -= drawRect.getMinY();
            
            context.save();
            Rotate rotate = new Rotate(frame.rotateZ, frame.x + frame.width / 2, frame.y + frame.height / 2);
            context.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
            context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
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
            context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
            context.restore();
            
            drawText(context, frame.extra, dx, dy);
        }
    }
    
    private Image createImage(Frame frame) {
        Image image = null;
        if (frame.image != null) {
            image = SwingFXUtils.toFXImage(frame.image, null);
        } else if (frame.getter != null) {
            image = frame.getter.getImage();
        }
        
        if (image != null && mixedColor != null && frame.mixed) {
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
}
