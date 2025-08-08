package cn.kizzzy.javafx.display.image;

import cn.kizzzy.helper.MathHelper;

public class Stat {
    
    private double canvas_width;
    private double canvas_height;
    
    private double pivot_x;
    private double pivot_y;
    
    public int id = 0;
    public int total = 0;
    
    public double x = Integer.MAX_VALUE;
    public double y = Integer.MAX_VALUE;
    public double maxX = 0;
    public double maxY = 0;
    
    public int min_layer = 0;
    public int max_layer = 0;
    
    public Point pivot;
    public Rect drawRect;
    public Rect wholeRect;
    public Rect rangeRect;
    public Rect validRect;
    public Rect borderRect;
    
    public Stat(double canvas_width, double canvas_height) {
        this.canvas_width = canvas_width;
        this.canvas_height = canvas_height;
        
        pivot_x = canvas_width / 2;
        pivot_y = canvas_height / 2;
    }
    
    public int width() {
        return (int) (maxX - x);
    }
    
    public int height() {
        return (int) (maxY - y);
    }
    
    public Rect fixedRect() {
        return new Rect(0, 0, drawRect.getWidth(), drawRect.getHeight())
            .move(new Rect(1, 1, -2, -2));
    }
    
    public Rect fixedRect_shrink() {
        return fixedRect()
            .move(new Rect(1, 1, -2, -2));
    }
    
    public Rect wholeRect(double pivotX, double pivotY) {
        return new Rect(x, y, width(), height())
            .move(new Rect(pivotX, pivotY, 0, 0));
    }
    
    public Rect rangeRect(double pivotX, double pivotY, int range) {
        return wholeRect(pivotX, pivotY)
            .move(new Rect(-range, -range, range * 2, range * 2));
    }
    
    public Rect rangeRect_relative() {
        return rangeRect
            .move(new Rect(-drawRect.getX(), -drawRect.getY(), 0, 0))
            .move(new Rect(1, 1, -2, -2));
    }
    
    public void onInitial(ImageArg arg) {
        drawRect = new Rect(0, 0, canvas_width, canvas_height);
        
        pivot = new Point(arg.pivotX, arg.pivotY);
        
        wholeRect = wholeRect(arg.pivotX, arg.pivotY);
        rangeRect = rangeRect(arg.pivotX, arg.pivotY, 200);
        validRect = wholeRect;
        borderRect = new Rect(arg.borderX, arg.borderY, arg.borderW, arg.borderH);
    }
    
    public void onDragging(Rect startRect, Point diff, ImageArg arg) {
        double newX = startRect.getMinX() - diff.getX();
        newX = MathHelper.clamp(newX, rangeRect.getMinX(), rangeRect.getMaxX() - startRect.getWidth());
        
        double newY = startRect.getMinY() - diff.getY();
        newY = MathHelper.clamp(newY, rangeRect.getMinY(), rangeRect.getMaxY() - startRect.getHeight());
        
        drawRect = new Rect(newX, newY, startRect.getWidth(), startRect.getHeight());
        
        pivot = new Point(arg.pivotX - drawRect.getX(), arg.pivotY - drawRect.getY());
        
        validRect = new Rect(
            wholeRect.getX() - newX, wholeRect.getY() - newY,
            validRect.getWidth(), validRect.getHeight()
        );
    }
    
    public void onResizing(double width, double height) {
        canvas_width = width;
        canvas_height = height;
        
        drawRect = new Rect(drawRect.getMinX(), drawRect.getMinY(), width, height);
    }
}
