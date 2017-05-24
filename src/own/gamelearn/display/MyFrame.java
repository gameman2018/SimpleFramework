package own.gamelearn.display;

import own.gamelearn.Input.KeyboardInput;
import own.gamelearn.Input.MouseInput;
import own.gamelearn.support.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

/**
 * Created by kakushouwa on 2017/4/24.
 */
public class MyFrame extends JFrame implements Runnable{

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private Canvas canvas;
    private Thread gameThread;

    private boolean running;

    private BufferStrategy bs;

    private Vector2f[] vector2fs;

    private KeyboardInput keyboard;
    private MouseInput mouse;

    public MyFrame(){
        vector2fs = new Vector2f[]{
            new Vector2f(WIDTH / 2 , HEIGHT / 2),
            new Vector2f(100,0)
        };

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
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
        canvas.setBackground(Color.BLACK);
        canvas.setSize(new Dimension(WIDTH, HEIGHT));
        canvas.setIgnoreRepaint(true);

        getContentPane().add(canvas);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setIgnoreRepaint(true);

        pack();
        setVisible(true);

        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);
        mouse = new MouseInput();
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();



        gameThread = new Thread(this);
        gameThread.start();
    }



    @Override
    public void run() {
        running = true;
        while (running){
            gameLoop();
        }
    }

    private void gameLoop(){
        keyboard.poll();
        mouse.poll();
        drawFrame();
        sleep(10L);
    }

    private void sleep(long mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawFrame(){
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, WIDTH, HEIGHT);
                    render(g);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    g.dispose();
                }
            }while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }

    private void render(Graphics g){

        if(keyboard.keyDown(KeyEvent.VK_C)){
            System.out.println("按了一次C键!");
        }

        if (mouse.mouseDown(MouseEvent.BUTTON1)){
            System.out.println("左键连续");
        }else if(mouse.mouseDown(MouseEvent.BUTTON3)){
            System.out.println("右键连续");
        }

        Vector2f end = vector2fs[vector2fs.length - 1];
        Vector2f start = null;
        for (int i = 0; i < vector2fs.length; i++){
            start = vector2fs[i];
            g.drawLine((int)start.x, (int)start.y, (int)end.x, (int)end.y);
            end = start;
        }

    }

    private void onWindowClosing(){
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
