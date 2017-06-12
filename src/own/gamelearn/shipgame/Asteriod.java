package own.gamelearn.shipgame;


import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Utility;
import own.gamelearn.support.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kakushouwa on 2017/5/13.
 */
public class Asteriod {

    public enum Size{
        Large,
        Medium,
        Samll
    }

    private Size size;
    private Vector2f position;
    private Vector2f velocity;
    private Vector2f[] polygon;
    private float angle;
    private float rotateDelta;
    private Wrapper wrapper;
    private ArrayList<Vector2f[]> renderList;

    public Asteriod(Wrapper wrapper){
        this.wrapper = wrapper;
        rotateDelta = getRandomRotateDelta();
        velocity = getRandomVelocity();
        renderList = new ArrayList<Vector2f[]>();
    }

    public Size getSize(){
        return this.size;
    }

    public void setSize(Size size){
        this.size = size;
    }

    public Vector2f getPosition(){
        return position;
    }

    public void setPosition(Vector2f position){
        this.position = position;
    }

    public void setPolygon(Vector2f[] polygon){
        this.polygon = polygon;
    }

    public Vector2f[] getPolygon(){
        return polygon;
    }

    private float getRandomFloat(float min, float max){
        return new Random().nextFloat() * (max - min) + min;
    }

    private float getRandomRotateDelta(){
        float delta = getRandomAngle(5,45);
        return new Random().nextBoolean() ? -delta : delta;
    }

    private float getRandomAngle(int min, int max){
        float angle = new Random().nextInt(max - min + 1);
        return (float) Math.toRadians(angle + min);
    }

    private Vector2f getRandomVelocity(){
        float angle = getRandomAngle(0,360);
        float radius = getRandomFloat(0.06f,0.3f);
        return Vector2f.polar(angle,radius);
    }

    public void update(float delta){
        position = position.add(velocity.mul(delta));
        position = wrapper.wrapPosition(position);
        angle += rotateDelta * delta;
        renderList.clear();
        Vector2f[] world = transformPolygon(polygon);
        renderList.add(world);
        wrapper.wrapPolygon(world,renderList);
    }

    private Vector2f[] transformPolygon(Vector2f[] polygon){
        Matrix3x3f matrix3x3f = Matrix3x3f.rotate(angle);
        matrix3x3f = matrix3x3f.mul(Matrix3x3f.translate(position));
        return transform(polygon, matrix3x3f);
    }

    private Vector2f[] transform(Vector2f[] polygon, Matrix3x3f view){
        Vector2f[] cpy = new Vector2f[polygon.length];
        for (int i = 0;i < polygon.length;i++){
            cpy[i] = view.mul(polygon[i]);
        }
        return cpy;
    }

    public void draw(Graphics2D g, Matrix3x3f view){
        for (Vector2f[] vs : renderList){
            for (int i = 0;i < vs.length;i++){
                vs[i] = view.mul(vs[i]);
            }
            g.setColor(Color.LIGHT_GRAY);
            Utility.drawFill(g, vs);
            g.setColor(Color.BLACK);
            Utility.drawPolygon(g, vs);
        }
    }

    public boolean contains(Vector2f point){
        for (Vector2f[] v : renderList){
            if (pointInPolygon(point,v)){
                return true;
            }
        }
        return false;
    }

    private boolean pointInPolygon(Vector2f point, Vector2f[] polygon){
        boolean inside = false;
        Vector2f start = polygon[polygon.length - 1];
        boolean startAbove = start.y >= point.y;
        for (int i = 0;i < polygon.length;i++){
            Vector2f end = polygon[i];
            boolean endAbove = end.y >= point.y;
            if (startAbove != endAbove){
                float m = (end.y - start.y) / (end.x - start.x);
                float x = start.x + (point.y - start.y) / m;
                if (x >= point.x){
                    inside = !inside;
                }
            }
            startAbove = endAbove;
            start = end;
        }
        return inside;
    }
}
