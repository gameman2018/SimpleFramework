package own.gamelearn.audio;

import own.gamelearn.file.ResourceLoader;
import own.gamelearn.framework.GameFrameControls;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Created by kakushouwa on 2017/6/4.
 */
public class SoundControl extends GameFrameControls {
    private BlockingClip clip;
    private LoopEvent loopClip;
    private BlockingDateLine dataLine;
    private LoopEvent loopStream;
    private byte[] rawSound;

    public SoundControl(){
        appWidth = 340;
        appHeight = 340;
        appSleepLong = 10L;
        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        InputStream in = ResourceLoader.load(SoundControl.class,"res/assets/sound/magic_4.wav","what the fuck");
        rawSound = readBytes(in);
        clip = new BlockingClip(rawSound);
        loopClip = new LoopEvent(clip);
        loopClip.initialize();
        dataLine = new BlockingDateLine(rawSound);
        loopStream = new LoopEvent(dataLine);
        loopStream.initialize();
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

    private void shutDownClips(){
        if (loopClip != null) loopClip.shutDown();
        if (loopStream != null) loopStream.shutDown();
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        while (appKeyboard.processKey()){
            if (appKeyboard.keyDownOnce(KeyEvent.VK_1)){
                loopClip.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_2)){
                loopClip.done();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_3)){
                loopStream.fire();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_4)){
                loopStream.done();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_W)){
                increaseGain(clip);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_S)){
                decreaseGain(clip);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_A)){
                panLeft(clip);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_D)){
                panRight(clip);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_I)){
                increaseGain(dataLine);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_K)){
                decreaseGain(dataLine);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_J)){
                panLeft(dataLine);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_L)){
                panRight(dataLine);
            }
        }
    }

    private void increaseGain(AudioStream audioStream){
        float current = audioStream.getGain();
        if (current < 10.0f){
            audioStream.setGain(current + 3.0f);
        }
    }

    private void decreaseGain(AudioStream audioStream){
        float current = audioStream.getGain();
        if (current > 20.0f){
            audioStream.setGain(current - 3.0f);
        }
    }

    private void panLeft(AudioStream audioStream){
        float current = audioStream.getPan();
        float precision = audioStream.getPrecision();
        audioStream.setPan(current - precision * 10.0f);
    }

    private void panRight(AudioStream audioStream){
        float current = audioStream.getPan();
        float precision = audioStream.getPrecision();
        audioStream.setPan(current + precision * 10.0f);
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
