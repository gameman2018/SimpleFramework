package own.gamelearn.audio;

/**
 * Created by kakushouwa on 2017/6/3.
 */
public class BlockingDateLine extends AudioStream {

    private AudioDataLine stream;
    public BlockingDateLine(byte[] soundData){
        super(soundData);
    }

    @Override
    public void open() {
        lock.lock();
        try{
            stream = new AudioDataLine(soundData);
            stream.initialize();
            stream.addLineListener(this);
            stream.open();
            while (!open){
                condition.await();
            }
            System.out.println("open");
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
            stream.close();
            while (open){
                condition.await();
            }
            System.out.println("close");
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
            stream.start();
            while (!started){
                condition.await();
            }
            System.out.println("started");
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
            stream.loop(count);
            while (!started){
                condition.await();
            }
            System.out.println("started");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void restart() {
        stream.restart();
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            stream.stop();
            while (started){
                condition.await();
            }
            System.out.println("stop");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
