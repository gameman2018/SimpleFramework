package own.gamelearn.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

/**
 * Created by kakushouwa on 2017/5/21.
 */
public class KeyZtest implements KeyListener {

    enum Type{
        PRESSED,
        RELESSED,
        TYPED
    }

    class Event{
        KeyEvent event;
        Type type;
        public Event(KeyEvent event, Type type){
            this.event = event;
            this.type = type;
        }
    }

    private LinkedList<Event> eventThread = new LinkedList<Event>();
    private LinkedList<Event> gameThread = new LinkedList<Event>();
    private Event event;
    private int[] polled;

    public KeyZtest(){
        polled = new int[256];
    }

    public boolean keyDown(int keyCode){
        return keyCode == event.event.getKeyCode() && polled[keyCode] > 0;
    }

    public boolean keyDownOnce(int keyCode){
        return keyCode == event.event.getKeyCode() && polled[keyCode] == 1;
    }

    public boolean processKey(){
        event = gameThread.poll();
        if (event != null){
            if (event.event.getKeyCode() > 0 && event.event.getKeyCode() < polled.length) {
                if (event.type == Type.PRESSED) {
                    polled[event.event.getKeyCode()] += 1;
                } else if (event.type == Type.RELESSED) {
                    polled[event.event.getKeyCode()] = 0;
                }
            }
        }
        return event!=null;
    }

    public void poll(){
        LinkedList<Event> tmp = eventThread;
        eventThread = gameThread;
        gameThread = tmp;
    }

    public Character getChar(){
        if (event.type != Type.TYPED){
            return null;
        }
        return event.event.getKeyChar();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        eventThread.add(new Event(e,Type.TYPED));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        eventThread.add(new Event(e,Type.PRESSED));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        eventThread.add(new Event(e,Type.RELESSED));
    }
}
