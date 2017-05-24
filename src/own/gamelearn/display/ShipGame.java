package own.gamelearn.display;


import own.gamelearn.framework.GameFrameControls;
import own.gamelearn.shipgame.*;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kakushouwa on 2017/5/13.
 */
public class ShipGame extends GameFrameControls {

    private ArrayList<Bullet> bullets;

    private ArrayList<Asteriod> asteriods;
    private AsteriodFactory factory;
    private Ship ship;
    private Wrapper wrapper;
    private Random random;

    public ShipGame(){
        appBorderScale = 0.8f;
        appCanvasBackgroundColor = Color.WHITE;
        appBackgroundColor = Color.LIGHT_GRAY;
        appWidth = 680;
        appHeight = 680;
        appWorldWidth = 2.0f;
        appWorldHeight = 2.0f;
        appScreenMaintain = true;
        appSleepLong = 1L;

        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        wrapper = new Wrapper(appWorldWidth,appWorldHeight);
        asteriods = new ArrayList<Asteriod>();
        bullets = new ArrayList<Bullet>();
        factory = new AsteriodFactory(wrapper);
        ship = new Ship(wrapper);
        random = new Random();
        createAsteriods();
    }

    private void createAsteriods(){
        asteriods.clear();
        for (int i = 0;i < 10;i++){
            Vector2f position = getAsteroidStartPosition();
            asteriods.add(factory.createLargeAsteriod(position));
        }
    }

    private Vector2f getAsteroidStartPosition(){
        float angle = (float) Math.toRadians(random.nextInt(360));
        float minimum = appWorldWidth / 4.0f;
        float extra = random.nextFloat() * minimum;
        float radius = minimum + extra;
        return Vector2f.polar(angle, radius);
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        if (appKeyboard.keyDown(KeyEvent.VK_LEFT)){
            ship.rotateLeft((float) delta);
        }
        if (appKeyboard.keyDown(KeyEvent.VK_RIGHT)){
            ship.rotateRight((float) delta);
        }
        if (appKeyboard.keyDown(KeyEvent.VK_SPACE)){
            bullets.add(ship.luanchBullet());
        }
        ship.setThrusting(appKeyboard.keyDown(KeyEvent.VK_UP),appKeyboard.keyDown(KeyEvent.VK_DOWN));
        if (appKeyboard.keyDownOnce(KeyEvent.VK_ESCAPE)){
            createAsteriods();
        }
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
        updateAsteriod((float) delta);
        updateBullets((float) delta);
        updateShip((float) delta);
    }

    private void updateAsteriod(float delta){
        for (Asteriod asteriod : asteriods){
            asteriod.update(delta);
        }
    }

    private void updateShip(float delta){
        ship.update(delta);
        boolean isHit = false;
        for (Asteriod asteriod : asteriods){
            if (ship.isTouching(asteriod)){
                isHit = true;
            }
        }
        ship.setDamaged(isHit);
    }

    private void updateBullets(float delta){
        ArrayList<Bullet> cpy = new ArrayList<Bullet>(bullets);
        for (Bullet bullet : cpy){
            updateBullet(bullet,delta);
        }
    }

    private void updateBullet(Bullet bullet,float delta){
        bullet.update(delta);
        if (wrapper.hasLeftWorld(bullet.getPosition())){
            bullets.remove(bullet);
        }else {
            ArrayList<Asteriod> ast = new ArrayList<Asteriod>(asteriods);
            for (Asteriod asteriod : ast){
                if (asteriod.contains(bullet.getPosition())){
                    bullets.remove(bullet);
                    asteriods.remove(asteriod);
                    spawnBabies(asteriod);
                }
            }
        }
    }

    private void spawnBabies(Asteriod asteriod){
        switch (asteriod.getSize()){
            case Large:
                asteriods.add(factory.createMediumAsteriod(asteriod.getPosition()));
                asteriods.add(factory.createMediumAsteriod(asteriod.getPosition()));
                break;
            case Medium:
                asteriods.add(factory.createSmallAsteriod(asteriod.getPosition()));
                asteriods.add(factory.createSmallAsteriod(asteriod.getPosition()));
                break;
        }
    }
    @Override
    protected void render(Graphics g) {
        super.render(g);
        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        Matrix3x3f view = getViewportTransform();
        drawAsteroid(graphics2D,view);
        drawBullet(graphics2D,view);
        drawShip(graphics2D,view);
    }

    private void drawAsteroid(Graphics2D g,Matrix3x3f view){
        for (Asteriod asteriod : asteriods){
            asteriod.draw(g,view);
        }
    }
    private void drawBullet(Graphics2D g,Matrix3x3f view){
        for (Bullet bullet : bullets){
            bullet.draw(g,view);
        }
    }
    private void drawShip(Graphics2D g,Matrix3x3f view){
        ship.draw(g,view);
    }

}
