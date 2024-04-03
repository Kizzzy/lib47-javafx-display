package cn.kizzzy.javafx.display.audio;

import cn.kizzzy.javafx.display.DisplayLoaderAttribute;
import cn.kizzzy.javafx.display.Magic;
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
}, magic = {
    Magic.MP3,
    Magic.OGG,
    Magic.WAV,
})
public class AudioDisplay implements AudioDisplayLoader {
    
    @Override
    public AudioArg loadAudio(IPackage vfs, Leaf leaf) throws Exception {
        IInputStreamGetter streamGetter = vfs.getInputStreamGetter(leaf.path);
        if (streamGetter != null) {
            return new AudioArg(
                leaf.path,
                true,
                streamGetter
            );
        }
        return null;
    }
}
