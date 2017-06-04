package own.gamelearn.display;

import com.sun.org.apache.regexp.internal.RE;
import own.gamelearn.audio.*;
import own.gamelearn.file.ResourceLoader;
import own.gamelearn.framework.GameFrameControls;

import own.gamelearn.thread.OneShotEvent;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.SimpleTimeZone;

/**
 * Created by kakushouwa on 2017/6/3.
 */
public class SoundPlayer extends GameFrameControls {
    private OneEventShot oneShotEvent;
    private LoopEvent loopEvent;
    private RestartEvent restartEvent;

    private OneEventShot oneShotStream;
    private LoopEvent loopStream;
    private RestartEvent restartStream;
    private byte[] weaponBytes;
    private byte[] rainBytes;
    private String loaded;

    public SoundPlayer(){
        appWidth = 340;
        appHeight = 340;
        appSleepLong = 10L;
        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        InputStream in = ResourceLoader.load(this.getClass(),"res/assets/sound/magic_1.wav","asdf");
        weaponBytes = readBytes(in);
        in = ResourceLoader.load(this.getClass(),"res/assets/sound/magic_2.wav","asdf");
        rainBytes = readBytes(in);
        loadWaveFile(weaponBytes);
        loaded = "weapon";
    }

    private byte[] readBytes(InputStream in){
        try{
            BufferedInputStream buf = new BufferedInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            while ((read = buf.read()) != -1){
                out.write(read);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadWaveFile(byte[] rawData){
        shutDownClips();
        oneShotEvent = new OneEventShot(new BlockingClip(rawData));
        oneShotEvent.initialize();
        loopEvent = new LoopEvent(new BlockingClip(rawData));
        loopEvent.initialize();
        restartEvent = new RestartEvent(new BlockingClip(rawData));
        restartEvent.initialize();
        oneShotStream = new OneEventShot(new BlockingDateLine(rawData));
        oneShotStream.initialize();
        loopStream = new LoopEvent(new BlockingDateLine(rawData));
        loopStream.initialize();
        restartStream = new RestartEvent(new BlockingDateLine(rawData));
        restartStream.initialize();
    }

    private void shutDownClips(){
        if (oneShotEvent != null) oneShotEvent.shutDown();
        if (loopEvent != null) loopEvent.shutDown();
        if (restartEvent != null) restartEvent.shutDown();
        if (oneShotStream != null) oneShotStream.shutDown();
        if (loopStream != null) loopStream.shutDown();
        if (restartEvent != null) restartStream.shutDown();
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        while(appKeyboard.processKey()){
            if (appKeyboard.keyDownOnce(KeyEvent.VK_A)){
                loadWaveFile(weaponBytes);
                loaded = "weapon";
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_B)){
                loadWaveFile(rainBytes);
                loaded = "rain";
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD1)){
                oneShotEvent.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD2)){
                oneShotEvent.done();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD3)){
                loopEvent.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD4)){
                loopEvent.done();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD5)){
                restartEvent.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD6)){
                oneShotStream.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD7)){
                oneShotStream.done();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD8)){
                loopStream.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD9)){
                loopStream.done();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_NUMPAD0)){
                restartStream.fire();
            }
        }
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
    }

    @Override
    protected void terminate() {
        super.terminate();
        shutDownClips();
    }
}
