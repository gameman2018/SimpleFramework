package own.gamelearn.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kakushouwa on 2017/6/3.
 */
public class SoundEvent implements Runnable {

    public static final String SHUT_DOWN = "shutdown";
    protected AudioStream audio;
    protected BlockingQueue<String> queue;
    private Thread consumer;

    public SoundEvent(AudioStream audio){
        this.audio = audio;
    }

    public void initialize(){
        audio.addListener(getListener());
        queue = new LinkedBlockingQueue<String>();
        consumer = new Thread(this);
        consumer.start();
    }

    public void put(String event){
        try {
            queue.put(event);
        }catch (InterruptedException e){}
    }

    public void shutDown(){
        Thread tmp = consumer;
        consumer = null;
        try {
            queue.put(SHUT_DOWN);
            tmp.join(10000L);
            System.out.println("Event shut down");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BlockingAudioListener getListener(){
        return new BlockingAudioListener() {
            @Override
            public void audioFinished() {
                onAudioFinished();
            }
        };
    }

    protected void onAudioFinished(){

    }
    protected void processEvent(String event) throws InterruptedException{

    }

    @Override
    public void run() {
        while (Thread.currentThread() == consumer){
            try {
                processEvent(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
