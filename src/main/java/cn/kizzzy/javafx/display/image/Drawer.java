package cn.kizzzy.javafx.display.image;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class Drawer {
    
    public static void clearRect(GraphicsContext context, Rect r) {
        context.clearRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    public static void drawPivot(GraphicsContext context, Point p, Color color) {
        if (color != null) {
            context.setStroke(color);
        }
        context.strokeLine(p.getX() - 50, p.getY(), p.getX() + 50, p.getY());
        context.strokeLine(p.getX(), p.getY() - 20, p.getX(), p.getY() + 20);
    }
    
    public static void drawFrame(GraphicsContext context, Rect r, Color color) {
        if (color != null) {
            context.setStroke(color);
        }
        context.strokeLine(r.getMinX(), r.getMinY(), r.getMaxX(), r.getMinY());
        context.strokeLine(r.getMaxX(), r.getMinY(), r.getMaxX(), r.getMaxY());
        context.strokeLine(r.getMaxX(), r.getMaxY(), r.getMinX(), r.getMaxY());
        context.strokeLine(r.getMinX(), r.getMaxY(), r.getMinX(), r.getMinY());
    }
    
    public static void drawRect(GraphicsContext context, Rect r, Color color) {
        if (color != null) {
            context.setFill(color);
            context.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
    }
    
    public static void drawText(GraphicsContext context, String text, float x, float y, Color color) {
        context.setFill(color);
        context.fillText(text, x, y);
    }
    
    public static void drawElement(GraphicsContext context, TrackElement element, Rect drawRect, ImageCreator creator, Color mixedColor, boolean showTips, ImageArg arg) {
        Track.StaticFrame sf = element.getStaticFrame();
        Track.DynamicFrame df = element.getDynamicFrame();
        
        Image image = creator.createImage(sf, mixedColor);
        if (image == null) {
            return;
        }
        if (df != null) {
            sf.mixed = true;
            image = creator.createImage(sf,
                new Color(df.color_r / 255.0, df.color_g / 255.0, df.color_b / 255.0, df.color_a));
        }
        
        float df_x = (df == null ? 0 : df.x) + arg.pivotX;
        float df_y = (df == null ? 0 : df.y) + arg.pivotY;
        
        float df_scale_x = df == null ? 1.0f : df.scale_x;
        float df_scale_y = df == null ? 1.0f : df.scale_y;
        
        boolean flip_x = df != null && df.scale_x < 0;
        boolean flip_y = df != null && df.scale_y < 0;
        
        float sx = 0;
        float sy = 0;
        float sw = sf.width;
        float sh = sf.height;
        
        float diff_x = sf.width * (1 - Math.abs(df_scale_x)) / 2f;
        float diff_y = sf.height * (1 - Math.abs(df_scale_y)) / 2f;
        
        float dx = df_x + sf.x + (flip_x ? 1 : 0) * sf.width + (flip_x ? -1 : 1) * diff_x;
        float dy = df_y + sf.y + (flip_y ? 1 : 0) * sf.height + (flip_y ? -1 : 1) * diff_y;
        float dw = sf.width * df_scale_x;
        float dh = sf.height * df_scale_y;
        
        Rect imageRect = new Rect(dx, dy, dw, dh);
        if (drawRect.contains(imageRect)) {
            dx -= drawRect.getMinX();
            dy -= drawRect.getMinY();
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
        }
        
        context.save();
        
        float angle = df == null ? 0 : df.rotate_z;
        float pivot_x = dx + (flip_x ? -1 : 1) * dw / 2f;
        float pivot_y = dy + (flip_y ? -1 : 1) * dh / 2f;
        
        Rotate rotate = new Rotate(angle, pivot_x, pivot_y);
        context.setTransform(
            rotate.getMxx(), rotate.getMyx(),
            rotate.getMxy(), rotate.getMyy(),
            rotate.getTx(), rotate.getTy()
        );
        
        context.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
        context.restore();
        
        if (showTips) {
            drawText(context, sf.extra, dx, dy, Color.BLACK);
        }
    }
}
