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
import cn.kizzzy.javafx.display.image.aoi.Vector4f;
import cn.kizzzy.javafx.display.image.getter.AoiImageGetter;
import cn.kizzzy.javafx.display.image.getter.IImageGetter;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    
    private Rect drawRect;
    private Vector4f range;
    private CanvasDraggingThread draggingThread;
    
    private AoiMap map;
    private IImageGetter<Element> imageGetter;
    
    private AnimatorPlayer animatorPlayer;
    private TrackFrameProcessor frameProcessor;
    
    private List<TrackElement> elements;
    private List<Frame> frames;
    
    private ImageArg arg;
    private Map<Frame, Image> imageKvs;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        elements = new LinkedList<>();
        frames = new LinkedList<>();
        
        frameProcessor = new TrackFrameProcessor(this::showFrames);
        
        Animator animator = new Animator();
        animator.getStateInfo().before = frameProcessor::clearFrame;
        animator.getStateInfo().after = frameProcessor::flushFrame;
        animatorPlayer = new AnimatorPlayer(animator);
        
        setColors(bg_color_hld, color -> bgColor = color, DEFAULT_COLORS);
        
        export_button.setOnAction(this::doExport);
        
        layer = new SimpleIntegerProperty();
        layer.addListener((observable, oldValue, newValue) -> {
            if (layer_cob.getValue() != Layer.NONE) {
                showImpl();
            }
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
    
    private void doExport(ActionEvent actionEvent) {
        BufferedImage image = new BufferedImage(0, 0, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D context = image.createGraphics();
        // todo
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
    
    private void resetAll() {
        imageKvs = null;
        
        canvas.setWidth(canvas_hld.getWidth());
        canvas.setHeight(canvas_hld.getHeight());
        
        drawRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void show(ImageArg tracks) {
        Platform.runLater(() -> {
            showImpl(tracks);
        });
    }
    
    public void showImpl(ImageArg arg) {
        resetAll();
        
        this.arg = arg;
        
        setColors(mixed_color_hld, color -> mixedColor = color, this.arg.colors);
        
        List<Element> elements = new LinkedList<>();
        List<CurveBinding<TrackFrame>> curves = new LinkedList<>();
        
        int id = 0, total = 0;
        float width = 0, height = 0;
        int min_layer = 0, max_layer = 0;
        for (Track track : this.arg.tracks) {
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
                
                if (frame.layer > max_layer) {
                    max_layer = frame.layer;
                }
                if (frame.layer < min_layer) {
                    min_layer = frame.layer;
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
        
        if (this.arg.drawType == ImageDrawType.FULL) {
            map = new AoiMap((int) width, (int) height, (int) width + 1, (int) height + 1);
        } else {
            map = new AoiMap((int) width, (int) height, this.arg.gridX, this.arg.gridY);
        }
        for (Element element : elements) {
            map.add(element);
        }
        imageGetter = new AoiImageGetter(map);
        
        Point p1 = new Point(this.arg.pivotX - 200, this.arg.pivotY - 200);
        Point p2 = new Point(this.arg.pivotX + 200, this.arg.pivotY + 200);
        Point p3 = new Point(
            this.arg.pivotX + width + 200 - drawRect.getWidth(),
            this.arg.pivotY + height + 200 - drawRect.getHeight()
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
        
        for (Element element : imageGetter.getImage(drawRect.getX(), drawRect.getY(),
            drawRect.getWidth(), drawRect.getHeight())) {
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
        context.clearRect(0, 0, drawRect.getWidth(), drawRect.getHeight());
        
        if (frames == null || frames.isEmpty()) {
            return;
        }
        
        drawRect(context, 0, 0, drawRect.getWidth(), drawRect.getHeight());
        drawFrame(context, 1, 1, drawRect.getWidth() - 2, drawRect.getHeight() - 2);
        
        frames.sort(Comparator.comparingInt(x -> x.order));
        for (Frame frame : frames) {
            if (layer_cob.getValue().check(frame.layer, layer.getValue())) {
                showFrame(context, frame);
            }
        }
        
        drawPivot(context, arg.pivotX, arg.pivotY);
        drawFrame(context, arg.borderX, arg.borderY, arg.borderW, arg.borderH);
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
            context.setTransform(
                rotate.getMxx(), rotate.getMyx(),
                rotate.getMxy(), rotate.getMyy(),
                rotate.getTx(), rotate.getTy()
            );
            context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
            context.restore();
            
            drawText(context, frame.extra, dx, dy);
        }
    }
    
    private Image createImage(Frame frame) {
        if (imageKvs == null && arg.cacheSize > 0) {
            imageKvs = new LinkedHashMap<Frame, Image>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    return size() > arg.cacheSize;
                }
            };
        }
        
        Image image = imageKvs != null ? imageKvs.get(frame) : null;
        if (image == null) {
            if (frame.image != null) {
                image = SwingFXUtils.toFXImage(frame.image, null);
            } else if (frame.getter != null) {
                image = frame.getter.getImage();
            }
            if (image != null && imageKvs != null) {
                imageKvs.put(frame, image);
            }
        }
        
        if (image != null && mixedColor != null && frame.mixed) {
            WritableImage blendImage = new WritableImage((int) frame.width, (int) frame.height);
            
            for (int i = 0; i < frame.height; ++i) {
                for (int j = 0; j < frame.width; ++j) {
                    int argb_old = image.getPixelReader().getArgb(j, i);
                    int a = (int) (((argb_old >> 24) & 0xFF) * mixedColor.getOpacity());
                    int r = (int) (((argb_old >> 16) & 0xFF) * mixedColor.getRed());
                    int g = (int) (((argb_old >> 8) & 0xFF) * mixedColor.getGreen());
                    int b = (int) (((argb_old >> 0) & 0xFF) * mixedColor.getBlue());
                    int argb_new = (a << 24) | (r << 16) | (g << 8) | b;
                    blendImage.getPixelWriter().setArgb(j, i, argb_new);
                }
            }
            image = blendImage;
        }
        
        return image;
    }
    
    private void drawText(GraphicsContext context, String text, double x, double y) {
        context.setFill(bgColor != null ? Color.WHITE : Color.BLACK);
        context.fillText(text, x, y);
    }
    
    private void drawRect(GraphicsContext context, int x, int y, double width, double height) {
        if (bgColor != null) {
            context.setFill(bgColor);
            context.fillRect(x, y, width, height);
        }
    }
    
    private void drawFrame(GraphicsContext context, double x, double y, double w, double h) {
        context.setStroke(bgColor != null ? Color.WHITE : Color.BLACK);
        context.strokeLine(x, y, x + w, y);
        context.strokeLine(x + w, y, x + w, y + h);
        context.strokeLine(x + w, y + h, x, y + h);
        context.strokeLine(x, y + h, x, y);
    }
    
    private void drawPivot(GraphicsContext context, float pivotX, float pivotY) {
        context.setStroke(bgColor != null ? Color.WHITE : Color.BLACK);
        context.strokeLine(pivotX - 50, pivotY, pivotX + 50, pivotY);
        context.strokeLine(pivotX, pivotY - 20, pivotX, pivotY + 20);
    }
}
