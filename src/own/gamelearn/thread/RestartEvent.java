package own.gamelearn.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kakushouwa on 2017/5/24.
 */
public class RestartEvent implements Runnable {
    private enum State{
        WAITING,
        RESTART,
        RUNNING
    }
    private enum Event{
        FIRE,
        DONE
    }

    private BlockingHardware hardware;
    private LinkedBlockingQueue<Event> queue;
    private State currentState;
    private Thread consumer;
    private int ms;
    private int slices;

    public RestartEvent(int ms,int slices){
        this.ms = ms;
        this.slices = slices;
    }

    public void initialize(){
        hardware = new BlockingHardware("restart");
        hardware.addListener(getListener());
        currentState = State.WAITING;
        consumer = new Thread(this);
        consumer.start();
    }

    public void fire(){
        try {
            queue.put(Event.FIRE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void shutDown(){
        Thread temp = consumer;
        consumer = null;
        try {
            queue.put(Event.DONE);
            temp.join(10000L);
            System.out.println("Shutdown");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BlockingHardwareListener getListener(){
        return new BlockingHardwareListener() {
            @Override
            public void taskFinished() {
                try {
                    queue.put(Event.DONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void processEvent(Event event) throws InterruptedException{
        System.out.println("Got " + event + " event");
        if (currentState == State.WAITING){
            if (Event.FIRE == event){
                hardware.turnOn();
                hardware.start(ms,slices);
                currentState = State.RUNNING;
            }
        }else if (currentState == State.RUNNING){
            if (Event.FIRE == event){
                hardware.stop();
                currentState = State.RESTART;
            }
            if (Event.DONE == event){
                hardware.stop();
                currentState = State.WAITING;
            }
        }else if (currentState == State.RESTART){
            if (Event.DONE == event){
                hardware.start(ms,slices);
                currentState = State.RUNNING;
            }
        }
    }

    @Override
    public void run() {
        while (consumer == Thread.currentThread()){
            try {
                processEvent(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
