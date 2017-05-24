package own.gamelearn.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by kakushouwa on 2017/5/24.
 */
public class OneShotEvent implements Runnable {

    private enum State {
        WAITING,
        RUNNING
    }

    private enum Event {
        FIRE,
        DONE
    }

    private BlockingQueue<Event> queue;
    private BlockingHardware hardware;
    private State currentState;
    private Thread consumer;
    private int ms;
    private int slices;


    public OneShotEvent(int ms, int slices) {
        this.ms = ms;
        this.slices = slices;
    }

    public void initialize() {
        hardware = new BlockingHardware("one shout");
        hardware.addListener(getListener());
        queue = new LinkedBlockingQueue<Event>();
        currentState = State.WAITING;
        consumer = new Thread(this);
        consumer.start();
    }

    public void fire() {
        try {
            queue.put(Event.FIRE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void done() {
        try {
            queue.put(Event.DONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutDown() {
        Thread temp = consumer;
        consumer = null;
        try {
            queue.put(Event.DONE);
            temp.join(10000L);
            System.out.println("ShutDown");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processEvent(Event event) throws InterruptedException {
        System.out.println("Got " + event + " event");
        if (currentState == State.WAITING) {
            if (event == Event.FIRE) {
                hardware.turnOn();
                hardware.start(ms, slices);
                currentState = State.RUNNING;
            }
        } else if (currentState == State.RUNNING) {
            if (event == Event.DONE) {
                hardware.turnOff();
                hardware.stop();
                currentState = State.WAITING;
            }
        }
    }

    private BlockingHardwareListener getListener() {
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

    @Override
    public void run() {
        while (Thread.currentThread() == consumer) {
            try {
                processEvent(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
