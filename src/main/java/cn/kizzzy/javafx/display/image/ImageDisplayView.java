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
import cn.kizzzy.javafx.JavafxControlParameter;
import cn.kizzzy.javafx.JavafxView;
import cn.kizzzy.javafx.control.LabeledSlider;
import cn.kizzzy.javafx.display.Stoppable;
import cn.kizzzy.javafx.display.image.animation.LinerTangleMod;
import cn.kizzzy.javafx.display.image.animation.TrackFrame;
import cn.kizzzy.javafx.display.image.animation.TrackFrameProcessor;
import cn.kizzzy.javafx.display.image.aoi.AoiMap;
import cn.kizzzy.javafx.display.image.aoi.Element;
import cn.kizzzy.javafx.display.image.getter.AoiImageGetter;
import cn.kizzzy.javafx.display.image.getter.IImageGetter;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

abstract class ImageDisplayViewBase extends JavafxView {
    
    @FXML
    protected CheckBox filter_chk;
    
    @FXML
    protected HBox bg_color_hld;
    
    @FXML
    protected HBox mixed_color_hld;
    
    @FXML
    protected Button export_button;
    
    @FXML
    protected LabeledSlider scale_sld;
    
    @FXML
    protected LabeledSlider layer_sld;
    
    @FXML
    protected ChoiceBox<Layer> layer_chk;
    
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
    protected LabeledSlider frame_sld;
    
    @FXML
    protected HBox canvas_hld;
    
    @FXML
    protected Canvas canvas;
}

@SuppressWarnings("unchecked")
@JavafxControlParameter(fxml = "/fxml/display/image_view.fxml")
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
    
    private Color bgColor;
    private Color mixedColor;
    
    private IntegerProperty layer;
    
    private Stat stat;
    
    private CanvasDraggingThread draggingThread;
    
    private AoiMap map;
    private IImageGetter<Element> imageGetter;
    
    private AnimatorPlayer animatorPlayer;
    private TrackFrameProcessor frameProcessor;
    
    private List<TrackElement> elements;
    private List<Frame> frames;
    
    private ImageArg arg;
    private ImageCreator creator;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        elements = new LinkedList<>();
        frames = new LinkedList<>();
        
        filter_chk.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // todo
        });
        
        setColors(bg_color_hld, color -> bgColor = color, DEFAULT_COLORS);
        
        setColors(mixed_color_hld, color -> mixedColor = color, DEFAULT_COLORS);
        
        export_button.setOnAction(this::doExport);
        
        scale_sld.valueProperty().addListener((observable, oldValue, newValue) -> {
            // todo
        });
        
        layer = new SimpleIntegerProperty();
        layer.addListener((observable, oldValue, newValue) -> {
            if (layer_chk.getValue() != Layer.NONE) {
                showImpl();
            }
        });
        
        layer_sld.valueProperty().addListener((observable, oldValue, newValue) -> {
            layer.setValue(newValue);
        });
        
        layer_chk.getItems().addAll(Layer.values());
        layer_chk.valueProperty().addListener((observable, oldValue, newValue) -> {
            showImpl();
        });
        
        frameProcessor = new TrackFrameProcessor(this::showFrames);
        
        Animator animator = new Animator();
        animator.getStateInfo().callback = frameProcessor;
        
        animatorPlayer = new AnimatorPlayer(animator);
        
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
        
        frame_sld.valueProperty().addListener((observable, oldValue, newValue) -> {
            animatorPlayer.jumpTo((int) newValue);
        });
        
        canvas.setOnDragDetected(event -> canvas.startFullDrag());
        canvas.setOnMouseDragEntered(event -> {
            if (draggingThread == null) {
                draggingThread = new CanvasDraggingThread(
                    new Point(event.getX(), event.getY()),
                    stat.drawRect,
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
            onCanvasResizing(canvas_hld.getWidth(), canvas_hld.getHeight());
        });
        canvas_hld.heightProperty().addListener((observable, oldValue, newValue) -> {
            onCanvasResizing(canvas_hld.getWidth(), canvas_hld.getHeight());
        });
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
    
    private void doExport(ActionEvent actionEvent) {
        BufferedImage image = new BufferedImage(0, 0, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D context = image.createGraphics();
        // todo
    }
    
    private void onCanvasDragging(Rect startRect, Point diff) {
        Platform.runLater(() -> {
            stat.onDragging(startRect, diff, arg);
            
            showImpl(true);
        });
    }
    
    private void onCanvasResizing(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
        
        if (stat != null) {
            stat.onResizing(width, height);
        }
        
        showImpl();
    }
    
    public void setColors(Pane panel, Consumer<Color> callback, String... colors) {
        callback.accept(null);
        
        panel.getChildren().clear();
        
        if (colors == null) {
            colors = DEFAULT_COLORS;
        }
        
        for (final String c : colors) {
            Color color = Color.valueOf(c);
            
            Pane pane = new Pane();
            pane.setPrefSize(24, 24);
            pane.setStyle("-fx-background-color: " + c + ";");
            pane.setOnMouseClicked(event -> {
                callback.accept(color);
                showImpl();
            });
            
            panel.getChildren().add(pane);
        }
    }
    
    public void show(ImageArg args) {
        Platform.runLater(() -> {
            try {
                showImpl(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void showImpl(ImageArg arg) {
        this.arg = arg;
        this.creator = new ImageCreator(arg.cacheSize);
        this.stat = new Stat(canvas.getWidth(), canvas.getHeight());
        
        setColors(mixed_color_hld, color -> mixedColor = color, this.arg.colors);
        
        List<Element> elements = new LinkedList<>();
        List<CurveBinding<TrackFrame>> curves = new LinkedList<>();
        
        for (Track track : this.arg.tracks) {
            int size = track.frames.size();
            if (size == 0) {
                continue;
            }
            
            stat.total = Math.max(stat.total, size);
            
            TrackElement element = new TrackElement(++stat.id, track);
            elements.add(element);
            
            List<KeyFrame<TrackFrame>> kfs = new LinkedList<>();
            for (Frame frame : track.frames) {
                KeyFrame<TrackFrame> kf = new KeyFrame<>();
                kf.time = (long) frame.time;
                kf.value = new TrackFrame(element, frame);
                kfs.add(kf);
                
                if (frame.x < stat.x) {
                    stat.x = frame.x;
                }
                if (frame.y < stat.y) {
                    stat.y = frame.y;
                }
                
                if ((frame.x + frame.width) > stat.maxX) {
                    stat.maxX = frame.x + frame.width;
                }
                if ((frame.y + frame.height) > stat.maxY) {
                    stat.maxY = frame.y + frame.height;
                }
                
                if (frame.layer > stat.max_layer) {
                    stat.max_layer = frame.layer;
                }
                if (frame.layer < stat.min_layer) {
                    stat.min_layer = frame.layer;
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
        
        play_btn.setDisable(stat.total <= 1);
        play_btn.setText(animatorPlayer.isPlaying() ? "暂停" : "播放");
        prev_btn.setDisable(stat.total <= 1);
        next_btn.setDisable(stat.total <= 1);
        
        layer_chk.setValue(Layer.NONE);
        layer_sld.setMin(stat.min_layer);
        layer_sld.setMax(stat.max_layer);
        layer_sld.setValue(stat.min_layer);
        
        if (this.arg.drawType == ImageDrawType.FULL) {
            map = new AoiMap(stat.width(), stat.height(), stat.width() + 1, stat.height() + 1);
        } else {
            map = new AoiMap(stat.width(), stat.height(), this.arg.gridX, this.arg.gridY);
        }
        for (Element element : elements) {
            map.add(element);
        }
        imageGetter = new AoiImageGetter(map);
        
        stat.onInitial(arg);
        
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
        
        for (Element element : imageGetter.getImage(stat.drawRect)) {
            if (element.visible()) {
                TrackElement trackElement = (TrackElement) element;
                trackElement.setInRange(true);
                
                elements.add(trackElement);
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
        
        Drawer.clearRect(context, stat.fixedRect());
        
        if (frames == null || frames.isEmpty()) {
            return;
        }
        
        Drawer.drawRect(context, stat.fixedRect(), bgColor);
        
        frames.sort(Comparator.comparingInt(x -> x.order));
        for (Frame frame : frames) {
            if (layer_chk.getValue().check(frame.layer, layer.getValue())) {
                showFrame(context, frame, stat.drawRect, Color.BLACK);
            }
        }
        
        Drawer.drawPivot(context, stat.pivot, Color.BLACK);
        
        Drawer.drawFrame(context, stat.borderRect, Color.BLACK);
        Drawer.drawFrame(context, stat.validRect, Color.RED);
        Drawer.drawFrame(context, stat.rangeRect_relative(), Color.GREEN);
        Drawer.drawFrame(context, stat.fixedRect_shrink(), Color.BLACK);
    }
    
    private void showFrame(GraphicsContext context, Frame frame, Rect drawRect, Color color) {
        Image image = creator.createImage(frame, mixedColor);
        if (image == null) {
            return;
        }
        
        double sx = 0;
        double sy = 0;
        double sw = frame.width;
        double sh = frame.height;
        
        double dx = arg.pivotX + frame.x + (frame.flipX ? frame.width : 0);
        double dy = arg.pivotX + frame.y + (frame.flipY ? frame.height : 0);
        double dw = frame.width * (frame.flipX ? -1 : 1);
        double dh = frame.height * (frame.flipY ? -1 : 1);
        
        Rect imageRect = new Rect(dx, dy, dw, dh);
        if (drawRect.contains(imageRect)) {
            dx -= drawRect.getMinX();
            dy -= drawRect.getMinY();
            
            context.save();
            Rotate rotate = new Rotate(frame.rotateZ, frame.x + frame.width / 2, frame.y + frame.height / 2);
            context.setTransform(
                rotate.getMxx(), rotate.getMyx(),
                rotate.getMxy(), rotate.getMyy(),
                rotate.getTx(), rotate.getTy()
            );
            context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
            context.restore();
            
            Drawer.drawText(context, frame.extra, dx, dy, color);
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
            context.setTransform(
                rotate.getMxx(), rotate.getMyx(),
                rotate.getMxy(), rotate.getMyy(),
                rotate.getTx(), rotate.getTy()
            );
            context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
            context.restore();
            
            Drawer.drawText(context, frame.extra, dx, dy, color);
        }
    }
}
