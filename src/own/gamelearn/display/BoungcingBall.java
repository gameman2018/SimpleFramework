package own.gamelearn.display;

import own.gamelearn.framework.GameFrameControls;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Created by kakushouwa on 2017/6/7.
 */
public class BoungcingBall extends GameFrameControls {
    private static final float WORLD_LEFT = -1.0f;
    private static final float WORLD_RIGHT = 1.0f;
    private static final float WORLD_TOP = 1.0f;
    private static final float WORLD_BOTTOM = -1.0f;
    private static final int MAX_BALLS = 4096;
    private static final int MIN_BALLS = 5;
    private static final int MULTIPLE = 2;


    private class Ball {
        Vector2f positon;
        Vector2f velocity;
        float radius;
        Color color;
    }

    private Ball[] balls;
    private int ballCount;

    public BoungcingBall() {
        appFPSColor = Color.WHITE;
        appWidth = 640;
        appHeight = 640;
        appBackgroundColor = Color.ORANGE;
        appCanvasBackgroundColor = Color.DARK_GRAY;
        appWorldHeight = 2.0f;
        appWorldWidth = 2.0f;
        appScreenMaintain = true;
        appSleepLong = 1L;
        appBorderScale = 0.9f;
        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        balls = new Ball[0];
        ballCount = 64;
    }

    private void createBalls() {
        Random random = new Random();
        balls = new Ball[ballCount];
        for (int i = 0; i < balls.length; i++) {
            Ball ball = new Ball();
            ball.velocity = new Vector2f(random.nextFloat(), random.nextFloat());
            float r = 0.025f + random.nextFloat() / 8.0f;
            ball.positon = new Vector2f(WORLD_LEFT - r, WORLD_TOP - r);
            ball.radius = r;
            float color = random.nextFloat();
            ball.color = new Color(color, color, color);
            balls[i] = ball;
        }
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        while (appKeyboard.processKey()) {
            if (appKeyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
                createBalls();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_UP)) {
                ballCount *= MULTIPLE;
                if (ballCount > MAX_BALLS) {
                    ballCount = MAX_BALLS;
                }
                createBalls();
            }
            if (appKeyboard.keyDownOnce(KeyEvent.VK_DOWN)) {
                ballCount /= MULTIPLE;
                if (ballCount < MIN_BALLS) {
                    ballCount = MIN_BALLS;
                }
                createBalls();
            }
        }
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
        for (Ball ball : balls) {
            ball.positon = ball.positon.add(ball.velocity.mul((float) delta));
            if (ball.positon.x - ball.radius < WORLD_LEFT) {
                ball.positon.x = WORLD_LEFT + ball.radius;
                ball.velocity.x = -ball.velocity.x;
            } else if (ball.positon.x + ball.radius > WORLD_RIGHT) {
                ball.positon.x = WORLD_RIGHT - ball.radius;
                ball.velocity.x = -ball.velocity.x;
            }
            if (ball.positon.y + ball.radius > WORLD_TOP) {
                ball.positon.y = WORLD_TOP - ball.radius;
                ball.velocity.y = -ball.velocity.y;
            } else if (ball.positon.y - ball.radius < WORLD_BOTTOM) {
                ball.positon.y = WORLD_BOTTOM + ball.radius;
                ball.velocity.y = -ball.velocity.y;
            }
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        for (Ball ball : balls) {
            g.setColor(ball.color);
            drawOval(g, ball);
        }

    }

    private void drawOval(Graphics g, Ball ball) {
        Matrix3x3f view = getViewportTransform();
        Vector2f center = ball.positon;
        float radius = ball.radius;
        Vector2f topLeft = new Vector2f(center.x - radius, center.y + radius);
        topLeft = view.mul(topLeft);
        Vector2f bottomRight = new Vector2f(center.x + radius, center.y - radius);
        bottomRight = view.mul(bottomRight);
        int cx = (int) topLeft.x;
        int cy = (int) topLeft.y;
        int cw = (int) (bottomRight.x - topLeft.x);
        int ch = (int) (bottomRight.y - topLeft.y);
        g.fillOval(cx, cy, cw, ch);
    }
}
