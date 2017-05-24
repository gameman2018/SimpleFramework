package own.gamelearn.display;

import own.gamelearn.Input.SafeKeyboardInput;
import own.gamelearn.framework.GameFrameControls;
import own.gamelearn.support.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by kakushouwa on 2017/5/21.
 */
public class SafeKey extends GameFrameControls {
    private int spaceCount;
    private float blink;
    private boolean drawCursor;
    private ArrayList<String> strings = new ArrayList<String>();

    public SafeKey() {
        appSleepLong = 10L;
        strings.add("");
        launchApp(this);
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        while (appKeyboard.processKey()) {
            if (appKeyboard.keyDownOnce(KeyEvent.VK_UP)) {
                appSleepLong += Math.min(appSleepLong / 2, 1000L);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
                appSleepLong -= Math.min(appSleepLong / 2, 1000L);
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_ESCAPE)) {
                spaceCount = 0;
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
                spaceCount++;
            }
            processTypedChar();
        }
    }

    private void processTypedChar(){
        Character typedChar = appKeyboard.getChar();
        if (typedChar != null){
            if (Character.isISOControl(typedChar)){
                if (KeyEvent.VK_BACK_SPACE == typedChar){
                    removeCharacter(typedChar);
                }
                if (KeyEvent.VK_ENTER == typedChar){
                    strings.add("");
                }
            }else {
                addCharacter(typedChar);
            }
            drawCursor = true;
            blink = 0.0f;
        }
    }

    private void addCharacter(Character c){
        strings.add(strings.remove(strings.size() - 1) + c);
    }

    private void removeCharacter(Character c){
        String line = strings.remove(strings.size() - 1);
        if (!line.isEmpty()){
            strings.add(line.substring(0,line.length()-1));
        }
        if (strings.isEmpty()){
            strings.add("");
        }
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
        blink += delta;
        if (blink > 0.5f){
            blink -= 0.5f;
            drawCursor = !drawCursor;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        int textPos = Utility.drawString(g,20,20,"",
                "Sleep value: " + appSleepLong,
                "Space count: " + spaceCount,
                "Press up to increase sleep",
                "Press down to decrease sleep",
                "Press esc to clear spaces count",
                "",
                "",
                ""
        );
        FontMetrics fontMetrics = g.getFontMetrics();
        int height = fontMetrics.getAscent() + fontMetrics.getDescent() + fontMetrics.getLeading();
        int x = 20 + fontMetrics.stringWidth(strings.get(strings.size() - 1));
        if (x > appCanvas.getWidth() - 30){
            strings.add("");
        }
        textPos = Utility.drawString(g,20,textPos,strings);
        if (drawCursor){
            int y = textPos - height;
            g.drawString("|",x,y + fontMetrics.getAscent());
        }
    }
}
