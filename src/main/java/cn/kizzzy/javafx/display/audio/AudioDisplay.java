package cn.kizzzy.javafx.display.audio;

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
}/*, magic = {
    Magic.MP3,
    Magic.OGG,
    Magic.WAV,
}*/)
public class AudioDisplay implements AudioDisplayLoader {
    
    @Override
    public AudioArg loadAudio(IPackage vfs, Leaf leaf) throws Exception {
        IInputStreamGetter streamGetter = vfs.getInputStreamGetter(leaf.path);
        if (streamGetter != null) {
            AudioArg.Track track = new AudioArg.Track();
            track.name = leaf.path;
            track.getter = streamGetter;
            
            AudioArg arg = new AudioArg();
            arg.auto = true;
            arg.loop = false;
            arg.tracks.add(track);
            return arg;
        }
        return null;
    }
}
