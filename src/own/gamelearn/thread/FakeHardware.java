package own.gamelearn.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by kakushouwa on 2017/5/24.
 */
public class FakeHardware {
    private static final int SLEEP_MIN = 100;
    private static final int SLEEP_MAX = 1000;

    public enum FakeHardwareEvent {
        START,
        STOP,
        ON,
        OFF
    }

    private volatile boolean on = false;
    private volatile boolean running = false;
    private List<FakeHardwareListener> listeners = Collections.synchronizedList(new ArrayList<>());
    String name;

    public FakeHardware(String name) {
        this.name = name;
    }

    public boolean addListener(FakeHardwareListener listener) {
        return listeners.add(listener);
    }

    public boolean isOn() {
        return on;
    }

    public boolean isRunning() {
        return running;
    }

    private void sleep() {
        int rand = new Random().nextInt(SLEEP_MAX - SLEEP_MIN + 1);
        sleep(SLEEP_MIN + rand);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void turnOn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setOn();
            }
        }).start();
    }

    public void turnOff() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setOff();
            }
        }).start();
    }

    public void start(final int ms, final int slices) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setStart(ms, slices);
            }
        }).start();
    }

    public void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep();
                setStop();
            }
        }).start();
    }

    private synchronized void setOn() {
        if (!on) {
            on = true;
            fireEvent(FakeHardwareEvent.ON);
        }
    }

    private synchronized void setOff() {
        if (on) {
            on = false;
            fireEvent(FakeHardwareEvent.OFF);
        }
    }

    private void setStart(int ms, int slices) {
        synchronized (this) {
            if (on && !running) {
                running = true;
                fireEvent(FakeHardwareEvent.START);
            }
        }
        if (running) {
            runTask(ms, slices);
            running = false;
            fireEvent(FakeHardwareEvent.STOP);
        }
    }

    private synchronized void setStop() {
        if (running) {
            running = false;
        }
    }

    private void runTask(int ms, int slices) {
        int sleep = ms / slices;
        for (int i = 0; i < slices; i++) {
            if (!running) {
                return;
            }
            System.out.println(name + "{" + (i + 1) + "/" + slices + "}");
            sleep(sleep);
        }
    }

    private void fireEvent(FakeHardwareEvent event) {
        synchronized (listeners) {
            for (FakeHardwareListener listener : listeners) {
                listener.event(this, event);
            }
        }
    }
}
