package cn.kizzzy.javafx.display.image;

public enum DisplayLayer {
    NONE("无", (l, r) -> true),
    EQUAL("等于", (l, r) -> l == r),
    NOT_EQUAL("不等于", (l, r) -> l != r),
    LESS("小于", (l, r) -> l < r),
    LESS_EQUAL("小于等于", (l, r) -> l <= r),
    GREATER("大于", (l, r) -> l > r),
    GREATER_EQUAL("大于等于", (l, r) -> l >= r),
    ;
    
    public interface Callback {
        boolean check(int left, int right);
    }
    
    private final String desc;
    
    private Callback callback;
    
    DisplayLayer(String desc, Callback func) {
        this.desc = desc;
        this.callback = func;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public boolean check(int left, int right) {
        return callback.check(left, right);
    }
    
    @Override
    public String toString() {
        return desc;
    }
}
