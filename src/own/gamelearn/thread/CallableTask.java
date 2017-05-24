package own.gamelearn.thread;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by kakushouwa on 2017/5/23.
 */
public class CallableTask implements Callable<Boolean> {

    public CallableTask() {
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < 50; i++){
                try {
                    Future<Boolean> result = service.submit(this);
                    System.out.println("Result: " + result.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }finally {
            try {
                service.shutdown();
                service.awaitTermination(10, TimeUnit.SECONDS);
                System.out.println("Thread Pool Shut Down!");
            }catch (InterruptedException ex){
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }

    @Override
    public Boolean call() throws Exception {
        Random random = new Random();
        int seconds = random.nextInt(6);
        if (seconds == 0) {
            throw new RuntimeException("0秒设置抛出的异常!");
        }
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return seconds % 2 == 0;
    }
}
