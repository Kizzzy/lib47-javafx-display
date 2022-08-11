package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.helper.LogHelper;
import cn.kizzzy.javafx.display.Display;
import cn.kizzzy.javafx.display.DisplayAAA;
import cn.kizzzy.javafx.display.DisplayAttribute;
import cn.kizzzy.javafx.display.DisplayType;
import cn.kizzzy.vfs.IInputStreamGetter;
import cn.kizzzy.vfs.IPackage;

@DisplayAttribute(suffix = {
    "aiff",
    "ape",
    "au",
    "flac",
    "mp3",
    "ogg",
    "snd",
    "spx",
    "wav",
})
public class AudioDisplay extends Display<IPackage> {
    
    public AudioDisplay(IPackage context, String path) {
        super(context, path);
    }
    
    @Override
    public DisplayAAA load() {
        try {
            IInputStreamGetter streamGetter = context.getInputStreamGetter(path);
            if (streamGetter != null) {
                return new DisplayAAA(DisplayType.SHOW_AUDIO, new AudioArg(
                    path,
                    true,
                    streamGetter
                ));
            }
        } catch (Exception e) {
            LogHelper.error("init failed: ", e);
        }
        return null;
    }
}
