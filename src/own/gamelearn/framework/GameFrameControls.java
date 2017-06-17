package own.gamelearn.framework;

import own.gamelearn.Input.KeyZtest;
import own.gamelearn.Input.KeyboardInput;
import own.gamelearn.Input.MouseInput;
import own.gamelearn.Input.SafeKeyboardInput;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.RateCreator;
import own.gamelearn.support.Utility;
import own.gamelearn.support.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.security.Key;

/**
 * Created by kakushouwa on 2017/5/6.
 */
public class GameFrameControls extends JFrame implements Runnable {

    private Thread gameThread;
    private volatile boolean running;
    private BufferStrategy bs;

    protected Color appFPSColor = Color.RED;
    protected Color appBackgroundColor = Color.LIGHT_GRAY;
    protected Color appCanvasBackgroundColor = Color.WHITE;
    protected Color appFontColor = Color.BLACK;
    protected Font appFontStyle = new Font("Courier New", Font.PLAIN, 14);

    protected int appWidth = 640;
    protected int appHeight = 640;
    protected float appWorldWidth = 2.0f;
    protected float appWorldHeight = 2.0f;
    protected float appBorderScale = 0.8f;
    protected long appSleepLong = 10L;

    protected String appTitle = "GameFrameControls";
    protected boolean appScreenMaintain = false;

    protected Canvas appCanvas;
    protected KeyZtest appKeyboard;
    protected MouseInput appMouse;
    protected RateCreator appRate;

    protected void initialize(){
        appRate = new RateCreator();
        appRate.initialize();
    }

    protected void createAndShowGUI(){
        appCanvas = new Canvas();
        appCanvas.setBackground(appCanvasBackgroundColor);
        appCanvas.setIgnoreRepaint(true);
        getContentPane().add(appCanvas);
        setLocationByPlatform(true);
        if (appScreenMaintain){
            getContentPane().setBackground(appBackgroundColor);
            setSize(appWidth, appHeight);
            setLayout(null);
            getContentPane().addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    onComponentResize(e);
                }
            });
        }else {
            appCanvas.setSize(appWidth, appHeight);
            pack();
        }
        setTitle(appTitle);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIgnoreRepaint(true);
        appKeyboard = new KeyZtest();
        appCanvas.addKeyListener(appKeyboard);
        appMouse = new MouseInput();
        appCanvas.addMouseMotionListener(appMouse);
        appCanvas.addMouseWheelListener(appMouse);
        appCanvas.addMouseListener(appMouse);
        setVisible(true);
        appCanvas.createBufferStrategy(2);
        bs = appCanvas.getBufferStrategy();
        appCanvas.requestFocus();
        gameThread = new Thread(this);
        gameThread.start();
    }

    protected void onComponentResize(ComponentEvent e){
        Dimension size = getContentPane().getSize();
        int vw = (int)(size.width * appBorderScale);
        int vh = (int)(size.height * appBorderScale);
        int vx = (size.width - vw) / 2;
        int vy = (size.height - vh) / 2;
        int newW = vw;
        int newH = (int)(vw * appWorldHeight / appWorldWidth);
        if (newH > vh){
            newW = (int)(vh * appWorldWidth / appWorldHeight);
            newH = vh;
        }
        vx += (vw - newW) / 2;
        vy += (vh - newH) / 2;
        appCanvas.setLocation(vx,vy);
        appCanvas.setSize(newW ,newH);
    }

    protected void terminate(){

    }
    @Override
    public void run() {
        initialize();
        running = true;
        long cur = System.nanoTime();
        long last = cur;
        double delta;
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
        renderFrame(delta);
        sleep(appSleepLong);
    }
    protected void processInput(double delta){
        appKeyboard.poll();
        appMouse.poll();
    }
    protected void updateObjects(double delta){

    }
    private void renderFrame(double delta){
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0,0,appCanvas.getWidth(),appCanvas.getHeight());
                    render(g);
                } finally {
                    g.dispose();
                }
            }while (bs.contentsRestored());
            bs.show();
        }while (bs.contentsLost());
    }
    protected void render(Graphics g){
        appRate.calculate();
        g.setColor(appFontColor);
        g.setFont(appFontStyle);
        g.drawString(appRate.getRate(),20, 20);
    }

    protected Vector2f getWorldMousePosition(){
        Matrix3x3f view = getViewportReverseTransform();
        return new Vector2f(view.mul(new Vector2f(appMouse.getCurPos().x, appMouse.getCurPos().y)));
    }

    private void sleep(long sleep){
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void onWindowClosing(){
        try{
            running = false;
            terminate();
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    protected Matrix3x3f getViewportTransform(){
        return Utility.createViewport(appWorldWidth,appWorldHeight,appCanvas.getWidth(),appCanvas.getHeight());
    }

    protected Matrix3x3f getViewportReverseTransform(){
        return Utility.createReverseViewport(appWorldWidth,appWorldHeight,appCanvas.getWidth(),appCanvas.getHeight());
    }

    public static void launchApp(final GameFrameControls gameFrameControls){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameFrameControls.createAndShowGUI();
            }
        });
        gameFrameControls.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameFrameControls.onWindowClosing();
            }
        });
    }
}
