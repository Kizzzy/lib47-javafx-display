package cn.kizzzy.javafx.display.image;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    
    public static void drawText(GraphicsContext context, String text, double x, double y, Color color) {
        context.setFill(color);
        context.fillText(text, x, y);
    }
}
