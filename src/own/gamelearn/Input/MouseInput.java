package own.gamelearn.Input;

import java.awt.*;
import java.awt.event.*;

/**
 * Created by kakushouwa on 2017/5/1.
 */
public class MouseInput implements MouseListener, MouseMotionListener, MouseWheelListener {

    private int[] BUTTONS = new int[3];
    private boolean[] STATES = new boolean[BUTTONS.length];
    private Point curPos;

    public MouseInput(){
        curPos = new Point(0,0);
    }

    public synchronized void poll(){
        for (int i = 0; i < BUTTONS.length; i++){
            if (STATES[i]){
                BUTTONS[i]++;
            }else {
                BUTTONS[i] = 0;
            }
        }
    }

    public boolean mouseDown(int button){
        return BUTTONS[button - 1] > 0;
    }

    public boolean mouseDownOnce(int button){
        return BUTTONS[button - 1] == 1;
    }

    public Point getCurPos(){
        return curPos;
    }
    @Override
    public synchronized void mouseClicked(MouseEvent e) {

    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        int mouseCode = e.getButton();
        STATES[mouseCode - 1] = true;
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        int mouseCode = e.getButton();
        STATES[mouseCode - 1] = false;
    }

    @Override
    public synchronized void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public synchronized void mouseMoved(MouseEvent e) {
        curPos = e.getPoint();
    }

    @Override
    public synchronized void mouseWheelMoved(MouseWheelEvent e) {

    }
}
