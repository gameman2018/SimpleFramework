package own.gamelearn.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by kakushouwa on 2017/6/1.
 */
public class BlockingClip extends AudioStream {
    private Clip clip;
    private boolean restart;

    public BlockingClip(byte[] soundData){
        super(soundData);
    }


    @Override
    public void open() {
        lock.lock();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(soundData);
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            clip = AudioSystem.getClip();
            clip.open(ais);
            while (!open){
                condition.await();
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            clip.close();
            while (open){
                condition.await();
            }
            clip = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void start() {
        lock.lock();
        try {
            clip.flush();
            clip.setFramePosition(0);
            clip.start();
            while (!started){
                condition.await();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void loop(int count) {
        lock.lock();
        try {
            clip.flush();
            clip.setFramePosition(0);
            clip.loop(count);
            while (!started){
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void restart() {
        restart = true;
        stop();
        restart = false;
        start();
    }

    @Override
    protected void fireTaskFinised() {
        if (!restart){
            super.fireTaskFinised();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            clip.stop();
            while (started){
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
