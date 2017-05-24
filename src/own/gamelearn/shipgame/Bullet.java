package own.gamelearn.shipgame;

import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Vector2f;

import java.awt.*;

/**
 * Created by kakushouwa on 2017/5/13.
 */
public class Bullet {
    private Vector2f position;
    private Vector2f velocity;
    private float radius;
    private Color color;

    public Bullet(Vector2f position,float angle){
        this.position = position;
        radius = 0.006f;
        velocity = Vector2f.polar(angle,1.0f);
        color = Color.GREEN;
    }

    public Vector2f getPosition(){
        return position;
    }

    public void draw(Graphics2D g, Matrix3x3f view){
        g.setColor(color);
        Vector2f topLeft = new Vector2f(position.x - radius,position.y + radius);
        topLeft = view.mul(topLeft);
        Vector2f bottomRight = new Vector2f(position.x + radius, position.y - radius);
        bottomRight = view.mul(bottomRight);
        int circleX = (int) topLeft.x;
        int circleY = (int) topLeft.y;
        int circleWidth = (int) (bottomRight.x - topLeft.x);
        int circleHeight = (int) (bottomRight.y - topLeft.y);
        g.drawOval(circleX,circleY,circleWidth,circleHeight);
    }

    public void update(float delta){
        position = position.add(velocity.mul(delta));
    }
}
