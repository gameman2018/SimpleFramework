package own.gamelearn.display;

import own.gamelearn.Input.KeyboardInput;
import own.gamelearn.Input.MouseInput;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.RateCreator;
import own.gamelearn.support.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

/**
 * Created by kakushouwa on 2017/5/1.
 */
public class MyCannon extends JFrame implements Runnable{

    private Canvas canvas;
    private Thread gameThread;
    private volatile boolean running;
    private BufferStrategy bs;
    private int screenWidth = 680;
    private int screenHeight = 480;

    private float worldWidth = 2.0f;
    private float worldHeight = 2.0f;

    private RateCreator rate;

    private Vector2f[] cannon;
    private Vector2f[] cannonCpy;

    private Vector2f pao;
    private Vector2f paoCpy;
    private Vector2f velocity;

    private float cannonDelta;
    private float cannonRad;


    private KeyboardInput keyboard;
    private MouseInput mouse;

    private boolean maintain;

    private float borderScale = 0.8f;

    public MyCannon(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
    }

    private void initialize(){
        rate = new RateCreator();
        rate.initialize();

        velocity = new Vector2f();
        cannonRad = 0.0f;
        cannonDelta = (float) Math.toRadians(90.0f);

        cannon = new Vector2f[]{
                new Vector2f(0.0f,0.0f),
                new Vector2f(0.25f,0.0f),
                new Vector2f(0.25f,-0.05f),
                new Vector2f(0.0f,-0.05f)
        };

        cannonCpy = new Vector2f[cannon.length];

//        Matrix3x3f scale = Matrix3x3f.scale(0.75f,0.75f);
//        for (int i = 0; i < cannon.length; i++){
//            cannon[i] = scale.mul(cannon[i]);
//        }
    }

    protected void createAndShowGUI(){
        canvas = new Canvas();
//        canvas.setSize(screenWidth,screenHeight);
        canvas.setIgnoreRepaint(true);
        canvas.setBackground(Color.BLACK);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setLayout(null);
        setSize(640,640);
        getContentPane().add(canvas);


        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //onComResized(e);
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIgnoreRepaint(true);
        setVisible(true);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();

        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);
        mouse = new MouseInput();
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);

        gameThread = new Thread(this);
        gameThread.start();
    }
//
//    private void onComResized(ComponentEvent e){
//        Dimension size = getContentPane().getSize();
//        int vw = (int)(size.width * borderScale);
//        int vh = (int)(size.height * borderScale);
//
//        int vx = (size.width - vw) / 2;
//        int vy = (size.height - vh) / 2;
//
//        int newW = vw;
//        int newH = (int) (vw * worldHeight / worldWidth);
//        if (newH > vh){
//            newW = (int) (vh * worldWidth / worldHeight);
//            newH = vh;
//        }
//
//        vx += (vw - newW) / 2;
//        vy += (vh - newH) / 2;
//
//        canvas.setLocation(vx,vy);
//        canvas.setSize(newW,newH);
//    }

    @Override
    public void run() {
        running = true;
        initialize();
        long cur = System.nanoTime();
        long last = cur;
        double delta = 0;
        while (running){
            cur = System.nanoTime();
            delta = cur - last;
            gameLoop(delta / 1.0E9);
            last = cur;
        }
    }

    private void gameLoop(double delta){
        processInput(delta);
        updateObjects(delta);
        renderFrame();
        sleep(10L);
    }

    private void updateObjects(double delta){
        Matrix3x3f matrix3x3f = Matrix3x3f.identity();
        matrix3x3f = matrix3x3f.mul(Matrix3x3f.rotate(cannonRad));
        matrix3x3f = matrix3x3f.mul(Matrix3x3f.translate(-0.75f, -0.75f));

        for (int i = 0; i < cannon.length;i++){
            cannonCpy[i] = matrix3x3f.mul(cannon[i]);
        }

        if (pao != null){
            velocity.y += -9.8f * delta;
            pao.x += velocity.x * delta;
            pao.y += velocity.y * delta;
            paoCpy = new Vector2f(velocity);

            if (pao.y <= -1.0f){
                pao = null;
            }
        }
    }

    private void renderFrame(){
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
                    render(g);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (g!=null)
                        g.dispose();
                }
            }while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }

    private void render(Graphics g){
        rate.calculate();
        g.setColor(Color.BLACK);
        g.drawString(rate.getRate(),20,20);

        float scaleX = (canvas.getWidth() - 1) / worldWidth;
        float scaleY = (canvas.getHeight() - 1) / worldHeight;
        Matrix3x3f viewport = Matrix3x3f.scale(scaleX, -scaleY);

        float transX = (canvas.getWidth() - 1) / 2.0f;
        float transY = (canvas.getHeight() - 1) / 2.0f;
        viewport = viewport.mul(Matrix3x3f.translate(transX, transY));

        for (int i = 0; i < cannon.length; i++){
            cannonCpy[i] = viewport.mul(cannonCpy[i]);
        }
        drawPloygon(g,cannonCpy);

        if (pao != null){
            paoCpy = viewport.mul(pao);
            g.drawOval((int)paoCpy.x - 1, (int)paoCpy.y - 1, 2,2);
        }
    }

    private void drawPloygon(Graphics g,Vector2f[] vector2fs){
        Vector2f S = null;
        Vector2f E = vector2fs[vector2fs.length - 1];
        for (int i = 0; i < vector2fs.length; ++i){
            S = vector2fs[i];
            g.drawLine((int) S.x,(int) S.y,(int) E.x,(int) E.y);
            E = S;
        }
    }

    private void processInput(double delta){
        mouse.poll();
        keyboard.poll();

        if (keyboard.keyDown(KeyEvent.VK_UP)){
            cannonRad += (cannonDelta * delta);
        }
        if (keyboard.keyDown(KeyEvent.VK_DOWN)){
            cannonRad -= (cannonDelta * delta);
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)){
            Matrix3x3f matrix3x3f = Matrix3x3f.translate(3.5f,0.0f);
            matrix3x3f = matrix3x3f.mul(Matrix3x3f.rotate(cannonRad));
            velocity = matrix3x3f.mul(new Vector2f());

            matrix3x3f = Matrix3x3f.translate(0.25f,0.0f);
            matrix3x3f = matrix3x3f.mul(Matrix3x3f.rotate(cannonRad));
            matrix3x3f = matrix3x3f.mul(Matrix3x3f.translate(-0.75f,-0.75f));

            pao = matrix3x3f.mul(new Vector2f());
        }

    }

    private void sleep(long sleep){
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onWindowClosing(){
        try {
            running = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
