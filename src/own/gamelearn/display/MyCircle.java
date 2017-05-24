package own.gamelearn.display;

import own.gamelearn.Input.KeyboardInput;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.RateCreator;
import own.gamelearn.support.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Random;

/**
 * Created by kakushouwa on 2017/4/28.
 */
public class MyCircle extends JFrame implements Runnable {

    private Canvas canvas;
    private Thread gameThread;
    private BufferStrategy bs;
    private float earthDelta = (float) Math.toRadians(5.0f);
    private float earthRot;
    private float moonDelta = (float) Math.toRadians(5.2f);
    private float moonRot;
    private volatile boolean running;

    private final int WIDTH = 640;
    private final int HEIGHT = 480;

    private int[] stars;
    private boolean showStar;
    private KeyboardInput keyboard;

    private Random random;
    private RateCreator rateCreator;

    public MyCircle(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    protected void createAndShowGUI(){
        canvas = new Canvas();
        canvas.setSize(WIDTH,HEIGHT);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIgnoreRepaint(true);
        pack();

        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);

        setVisible(true);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        initialize();
        gameThread = new Thread(this);
        gameThread.start();


    }
    private void onWindowClosing(){
        try {
            running = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void initialize(){
        showStar = true;
        rateCreator = new RateCreator();

        random = new Random();
        starsCreator();
    }

    private void starsCreator(){
        stars = new int[1000];
        for (int i = 0; i < stars.length; i+=2){
            stars[i] = random.nextInt(WIDTH);
            stars[i+1] = random.nextInt(HEIGHT);
        }
    }
    @Override
    public void run() {
        running = true;
        rateCreator.initialize();
        while (running){
            gameLoop();
        }
    }
    private void gameLoop(){
        processKeyboard();
        frameRender();
        sleep(10L);
    }
    private void processKeyboard(){
        keyboard.poll();
        if(keyboard.keyDownOnce(KeyEvent.VK_SPACE)){
            showStar = !showStar;
        }
    }
    private void frameRender(){
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0,0,WIDTH,HEIGHT);
                    render(g);
                } finally {
                    g.dispose();
                }
            }while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }
    private void sleep(long sleep){
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void render(Graphics g)
    {
        rateCreator.calculate();
        g.setColor(Color.BLACK);
        g.drawString(rateCreator.getRate(),20,20);

        g.setColor(Color.BLUE);
        if(showStar){
            for (int i = 0; i < stars.length; i+=2){
                g.fillOval(stars[i],stars[i+1],1,1);
            }
        }

        Matrix3x3f sunMat = Matrix3x3f.identity();
        sunMat = sunMat.mul(Matrix3x3f.translate(WIDTH / 2, HEIGHT / 2));
        Vector2f sun = sunMat.mul(new Vector2f());
        g.setColor(Color.YELLOW);
        g.fillOval((int) sun.x - 50, (int)sun.y - 50,100,100);

        g.setColor(Color.BLACK);
        g.drawOval((int)sun.x - WIDTH / 4, (int)sun.y - WIDTH / 4,WIDTH / 2,WIDTH / 2);


        Matrix3x3f earthMat = Matrix3x3f.identity().mul(Matrix3x3f.translate(0, -WIDTH / 4 ));
        earthMat = earthMat.mul(Matrix3x3f.rotate(earthRot));
        earthMat = earthMat.mul(sunMat);
        Vector2f earth = earthMat.mul(new Vector2f());
        earthRot += earthDelta;
        g.setColor(Color.GRAY);
        g.fillOval((int)earth.x - 15,(int)earth.y - 15, 30, 30);
    }}
