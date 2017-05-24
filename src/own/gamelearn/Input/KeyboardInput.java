package own.gamelearn.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by kakushouwa on 2017/4/26.
 */
public class KeyboardInput implements KeyListener {

    private int[] polled;
    private boolean[] keys;

    public KeyboardInput(){
        keys = new boolean[256];
        polled = new int[keys.length];
    }

    public synchronized void poll(){
        for (int i = 0; i < keys.length; i++){
            if (keys[i]){
                polled[i] ++;
            }else {
                polled[i] = 0;
            }
        }
    }

    public boolean keyDown(int key){
        return polled[key] > 0;
    }

    public boolean keyDownOnce(int key){
        return polled[key] == 1;
    }

    @Override
    public synchronized void keyTyped(KeyEvent e) {

    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key >= 0 && key < keys.length){
            keys[key] = true;
        }
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(key >= 0 && key < keys.length){
            keys[key] = false;
        }
    }
}
