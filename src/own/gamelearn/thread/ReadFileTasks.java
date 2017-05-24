package own.gamelearn.thread;

import own.gamelearn.framework.GameFrameControls;
import own.gamelearn.support.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by kakushouwa on 2017/5/23.
 */
public class ReadFileTasks extends GameFrameControls {

    private static final int FILE_COUNTS = 10000;

    private ExecutorService oneThread;
    private ExecutorService twoThread;
    private ExecutorService threeThread;

    private ArrayList<Callable<Boolean>> callables;
    private ArrayList<Future<Boolean>> futures;

    private boolean isLoading;

    public ReadFileTasks() {
        appWidth = 600;
        appHeight = 420;
        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        isLoading = false;
        oneThread = Executors.newSingleThreadExecutor();
        twoThread = Executors.newFixedThreadPool(36);
        threeThread = Executors.newCachedThreadPool();

        callables = new ArrayList<Callable<Boolean>>();
        futures = new ArrayList<Future<Boolean>>();

        for (int i = 0; i < FILE_COUNTS; i++) {
            final int taskNum = i;
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        Thread.sleep(new Random().nextInt(1000));
                        System.out.println("Task number: " + taskNum);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    return Boolean.TRUE;
                }
            });
        }
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        while (appKeyboard.processKey()){
            if (appKeyboard.keyDownOnce(KeyEvent.VK_1)) {
                if (!isLoading) {
                    for (Callable<Boolean> task : callables) {
                        futures.add(oneThread.submit(task));
                    }
                }
            }

            if (appKeyboard.keyDownOnce(KeyEvent.VK_2)) {
                if (!isLoading) {
                    for (Callable<Boolean> task : callables) {
                        futures.add(twoThread.submit(task));
                    }
                }
            }

            if (appKeyboard.keyDownOnce(KeyEvent.VK_3)) {
                if (!isLoading) {
                    for (Callable<Boolean> task : callables) {
                        futures.add(threeThread.submit(task));
                    }
                }
            }
        }
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
        Iterator<Future<Boolean>> its = futures.iterator();
        while (its.hasNext()){
            Future<Boolean> result = its.next();
            if (result.isDone()){
                try {
                    if (result.get()){
                        its.remove();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        isLoading = !futures.isEmpty();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        int textPos = Utility.drawString(g,20,20,"Test: ");
        double percent = (FILE_COUNTS - futures.size()) / (double)FILE_COUNTS;
        String process = String.format("File progress: %.2f", percent * 100);
        Utility.drawString(g,20,textPos,process);
    }

    @Override
    protected void terminate() {
        super.terminate();
        terminateExecutor(oneThread);
        terminateExecutor(twoThread);
        terminateExecutor(threeThread);
    }

    private void terminateExecutor(ExecutorService executorService){
        executorService.shutdown();
        try {
            executorService.awaitTermination(10,TimeUnit.SECONDS);
            System.out.println("Executor shutdown");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
