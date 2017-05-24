package own.gamelearn.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by kakushouwa on 2017/5/24.
 */
public class LoopEvent implements Runnable {

    private enum State{
        WAITING,
        RUNNING
    }
    private enum Event{
        FIRE,
        RESTART,
        DONE
    }

    private BlockingHardware hardware;
    private BlockingQueue<Event> queue;
    private State currentState;
    private Thread consumer;
    private int ms;
    private int slices;


    public LoopEvent(int ms, int slices){
        this.ms = ms;
        this.slices = slices;
    }

    public void initialized(){
        hardware = new BlockingHardware("LoopEvent");
        hardware.addListener(getListener());
        queue = new LinkedBlockingDeque<>();
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

    public void done(){
        try {
            queue.put(Event.DONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutDown(){
        Thread tmp = consumer;
        consumer = null;
        try {
            queue.put(Event.DONE);
            tmp.join(10000L);
            System.out.println("Loop shutdown");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void processEvent(Event event) throws InterruptedException{
        System.out.println("Got " + event + " event");
        if (currentState == State.WAITING){
            if (event == Event.FIRE){
                hardware.turnOn();
                hardware.start(ms,slices);
                currentState = State.RUNNING;
            }
        }else if (currentState == State.RUNNING){
            if (event == Event.RESTART){
                hardware.start(ms,slices);
                currentState = State.RUNNING;
            }
            if (event == Event.DONE){
                hardware.stop();
                hardware.turnOff();
                currentState = State.WAITING;
            }
        }
    }

    private BlockingHardwareListener getListener(){
        return new BlockingHardwareListener() {
            @Override
            public void taskFinished() {
                try {
                    queue.put(Event.RESTART);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
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
