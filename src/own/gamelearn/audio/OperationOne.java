package own.gamelearn.audio;




import own.gamelearn.file.ResourceLoader;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Created by kakushouwa on 2017/6/1.
 */
public class OperationOne implements LineListener{
    private volatile boolean open = false;
    private volatile boolean started = false;

    public OperationOne(){
        try {
            runTestWithWaiting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update(LineEvent event) {
        System.out.println("Got Event : " + event.getType());
        LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.OPEN){
            open = true;
        }else if (type == LineEvent.Type.START){
            started = true;
        }else if (type == LineEvent.Type.STOP){
            started = false;
        }else if (type == LineEvent.Type.CLOSE){
            open = false;
        }
        //this.notifyAll();
    }

    public byte[] readBytes(InputStream in){
        try {
            BufferedInputStream buf= new BufferedInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            while((read = buf.read()) != -1){
                out.write(read);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void runTestWithoutWaiting() throws Exception{
        System.out.println("without");
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(this);
        InputStream resource = ResourceLoader.load(OperationOne.class,"res/assets/sound/magic_1.wav","notneeded");
        byte[] rawBytes = readBytes(resource);
        ByteArrayInputStream in = new ByteArrayInputStream(rawBytes);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
        clip.open(audioInputStream);
        for (int i = 0;i < 10;i++){
            clip.start();
            while (!clip.isActive()){
                Thread.sleep(100);
            }
            clip.stop();
            clip.flush();
            clip.setFramePosition(0);
            clip.start();
            clip.drain();
        }
        clip.close();
    }

    public void runTestWithWaiting() throws Exception{
        System.out.println("with");
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(this);
        InputStream resource = ResourceLoader.load(OperationOne.class,"res/assets/sound/magic_1.wav","notneeded");
        byte[] rawBytes = readBytes(resource);
        ByteArrayInputStream in = new ByteArrayInputStream(rawBytes);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
        synchronized (this){
            clip.open(audioInputStream);
            while (open){
                notify();
            }
        }
        for (int i = 0; i < 10; i ++){
            clip.setFramePosition(0);
            synchronized (this){
                clip.start();
                while (!started){
                    notify();
                }
            }
            clip.drain();
            synchronized (this){
                clip.stop();
                while (started){
                    notify();
                }
            }
        }
        synchronized (this){
            clip.close();
            while (open){
                notify();
            }
        }
    }
}
