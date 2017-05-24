package own.gamelearn.shipgame;

import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Utility;
import own.gamelearn.support.Vector2f;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by kakushouwa on 2017/5/13.
 */
public class Ship {
    private Vector2f velocity;
    private Vector2f position;
    private float acceleration;
    private float friction;
    private float angle;
    private float radius;
    private float turnDelta;
    private float curAcc;
    private float maxVelocity;
    private Wrapper wrapper;
    private Vector2f[] plane;
    private boolean isDamaged;
    private ArrayList<Vector2f[]> renderList;

    public Ship(Wrapper wrapper){
        this.wrapper = wrapper;
        friction = 0.25f;
        turnDelta = (float) Math.toRadians(180.0);
        acceleration = 1.0f;
        maxVelocity = 0.5f;
        velocity = new Vector2f();
        position = new Vector2f();
        plane = new Vector2f[]{
                new Vector2f(0.0325f,0.0f),
                new Vector2f(-0.0325f,-0.0325f),
                new Vector2f(0.0f,0.0f),
                new Vector2f(-0.0325f,0.0325f)
        };
        renderList = new ArrayList<Vector2f[]>();
    }

    public void setDamaged(boolean damaged){
        this.isDamaged = damaged;
    }

    public boolean isDamaged(){
        return isDamaged;
    }

    public void rotateLeft(float delta){
        angle += turnDelta * delta;
    }

    public void rotateRight(float delta){
        angle -= turnDelta * delta;
    }

    public void setThrusting(boolean thrusting, boolean backing){
        if (thrusting){
            curAcc = acceleration;
        }else if (backing){
            curAcc = -acceleration;
        }else {
            curAcc = 0.0f;
        }
    }

    public void setAngle(float angle){
        this.angle = angle;
    }

    public Bullet luanchBullet(){
        Vector2f bullutPos = position.add(Vector2f.polar(angle,0.0325f));
        return new Bullet(bullutPos,angle);
    }

    public void update(float delta){
        updatePosition(delta);
        renderList.clear();
        Vector2f[] world = transformPolygon();
        renderList.add(world);
        wrapper.wrapPolygon(world,renderList);
    }

    public void draw(Graphics2D g,Matrix3x3f view){
        g.setColor(new Color(50,50,50));
        for (Vector2f[] poly : renderList){
            for (int i = 0;i < poly.length;i++){
                poly[i] = view.mul(poly[i]);
            }
            g.setColor(Color.DARK_GRAY);
            Utility.drawFill(g,poly);
            g.setColor(isDamaged() ? Color.RED : Color.GREEN);
            Utility.drawPolygon(g,poly);
        }
    }

    private void updatePosition(float delta){
        Vector2f accel = Vector2f.polar(angle, curAcc);
        velocity = velocity.add(accel.mul(delta));
        float maxSpeed = Math.min(maxVelocity / velocity.len(),1.0f);
        velocity = velocity.mul(maxSpeed);
        float slowDown = 1.0f - friction * delta;
        velocity = velocity.mul(slowDown);
        position = position.add(velocity.mul(delta));
        position = wrapper.wrapPosition(position);
    }

    private Vector2f[] transformPolygon(){
        Matrix3x3f matrix3x3f = Matrix3x3f.rotate(angle);
        matrix3x3f = matrix3x3f.mul(Matrix3x3f.translate(position));
        return transform(plane, matrix3x3f);
    }

    private Vector2f[] transform(Vector2f[] polygon,Matrix3x3f view){
        Vector2f[] cpy = new Vector2f[polygon.length];
        for (int i = 0;i < polygon.length;i++){
            cpy[i] = view.mul(polygon[i]);
        }
        return cpy;
    }

    public boolean isTouching(Asteriod asteriod){
        for (Vector2f[] poly : renderList){
            for (Vector2f v : poly){
                if (asteriod.contains(v)){
                    return true;
                }
            }
        }
        return false;
    }
}
