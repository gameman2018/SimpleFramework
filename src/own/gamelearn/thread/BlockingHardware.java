package own.gamelearn.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kakushouwa on 2017/5/24.
 */
public class BlockingHardware {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile boolean on = false;
    private volatile boolean started = false;
    private FakeHardware fakeHardware;
    private List<BlockingHardwareListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public BlockingHardware(String name) {
        fakeHardware = new FakeHardware(name);
        fakeHardware.addListener(new FakeHardwareListener() {
            @Override
            public void event(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
                handleHardwareEvent(source, event);
            }
        });
    }

    public boolean addListener(BlockingHardwareListener listener){
        return listeners.add(listener);
    }

    public void start(int ms, int slices){
        lock.lock();
        try {
            fakeHardware.start(ms,slices);
            while (!started){
                condition.await();
            }
            System.out.println("Started");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void stop(){
        lock.lock();
        try {
            fakeHardware.stop();
            while (started){
                condition.await();
            }
            System.out.println("Stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void turnOn(){
        lock.lock();
        try {
            fakeHardware.turnOn();
            while (!on){
                condition.await();
            }
            System.out.println("Turn on");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void turnOff(){
        lock.lock();
        try {
            fakeHardware.turnOff();
            while (on){
                condition.await();
            }
            System.out.println("Turn off");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    protected void handleHardwareEvent(FakeHardware source, FakeHardware.FakeHardwareEvent event) {
        boolean wasStarted = started;
        lock.lock();
        try {
            if (event == FakeHardware.FakeHardwareEvent.ON) {
                on = true;
            } else if (event == FakeHardware.FakeHardwareEvent.OFF) {
                on = false;
            } else if (event == FakeHardware.FakeHardwareEvent.START) {
                started = true;
            } else if (event == FakeHardware.FakeHardwareEvent.STOP) {
                started = false;
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
        if (wasStarted && !started) {
            fireTaskFinished();
        }
    }

    private void fireTaskFinished() {
        synchronized (listeners) {
            for (BlockingHardwareListener listener : listeners) {
                listener.taskFinished();
            }
        }
    }
}
