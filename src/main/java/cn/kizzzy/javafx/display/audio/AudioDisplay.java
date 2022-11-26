package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.helper.LogHelper;
import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.vfs.IInputStreamGetter;
import cn.kizzzy.vfs.IPackage;
import cn.kizzzy.vfs.tree.Leaf;

@DisplayLoaderAttribute(suffix = {
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
public class AudioDisplay implements AudioDisplayLoader {
    
    @Override
    public AudioArg loadAudio(IPackage vfs, Leaf leaf) throws Exception {
        try {
            IInputStreamGetter streamGetter = vfs.getInputStreamGetter(leaf.path);
            if (streamGetter != null) {
                return new AudioArg(
                    leaf.path,
                    true,
                    streamGetter
                );
            }
        } catch (Exception e) {
            LogHelper.error("init failed: ", e);
        }
        return null;
    }
}
