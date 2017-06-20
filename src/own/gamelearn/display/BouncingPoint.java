package own.gamelearn.display;

import own.gamelearn.framework.GameFrameControls;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Utility;
import own.gamelearn.support.Vector2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Created by 挨踢狗 on 2017/6/20.
 */
public class BouncingPoint extends GameFrameControls {
    private static final float EPSILON = 0.000001f;
    private static final int MAX_POINTS = 4096;
    private static final int MIN_POINTS = 8;
    private static final int MULTIPLE = 2;
    private int pointCount;
    private Vector2f[] points;
    private Vector2f[] velocities;
    private Vector2f[] polygon;
    private Vector2f[] polygonCpy;

    public BouncingPoint(){
        appWidth = 640;
        appHeight = 640;
        appSleepLong = 10L;
        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        polygon = new Vector2f[]{
          new Vector2f(0.09233177f, -0.22378719f),
                new Vector2f(-0.21752739f, 0.16431928f),
                new Vector2f(-0.37089205f, 0.14553994f)
        };
        polygonCpy = new Vector2f[polygon.length];
        pointCount = MIN_POINTS;
        createPoints();
    }

    private void createPoints(){
        Random rand = new Random();
        points = new Vector2f[pointCount];
        velocities = new Vector2f[points.length];
        for (int i = 0; i < points.length; i++){
            points[i] = new Vector2f();
            double rad = Math.toRadians(rand.nextInt(360));
            double distancePerSecond = rand.nextFloat() + 0.5f;
            float vx = (float)(distancePerSecond * Math.cos(rad));
            float vy = (float)(distancePerSecond * Math.sin(rad));
            velocities[i] = new Vector2f(vx, vy);
        }
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);

        while (appKeyboard.processKey()){
            if (appKeyboard.keyDownOnce(KeyEvent.VK_SPACE)){
                createPoints();
            }

            if (appKeyboard.keyDownOnce(KeyEvent.VK_UP)){
                pointCount = clamp(pointCount * MULTIPLE);
                createPoints();
            }

            if (appKeyboard.keyDownOnce(KeyEvent.VK_DOWN)){
                pointCount = clamp(pointCount / MULTIPLE);
                createPoints();
            }
        }

    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        Matrix3x3f view = getViewportTransform();
        for (int i = 0; i < polygon.length; i++){
            polygonCpy[i] = view.mul(polygon[i]);
        }
        Utility.drawPolygon(g, polygonCpy);
        for (int i = 0; i < points.length; i++){
            Vector2f copy = view.mul(points[i]);
            g.drawRect((int)copy.x, (int)copy.y, 1, 1);
        }
    }

    private int clamp(int count){
        if (count < MIN_POINTS)return MIN_POINTS;
        if (count > MAX_POINTS)return MAX_POINTS;
        return count;
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
        for (int i = 0; i < points.length; i++){
            Vector2f velocity = velocities[i];
            Vector2f start = points[i];
            Vector2f end = start.add(velocity.mul((float) delta));
            Vector2f newVelocity =getNewVelocity(start, end, velocity);
            if (newVelocity != null){
                velocities[i] = newVelocity;
            }else {
                points[i] = end;
            }
        }
    }

    private Vector2f getNewVelocity(Vector2f start, Vector2f end, Vector2f velocity){
        Vector2f P = null;
        Vector2f S = polygon[polygon.length - 1];
        for (int j = 0; j < polygon.length; j++){
            P = polygon[j];
            if (hitPolygon(start, end, S, P)){
                return calculateReflection(velocity, S, P);
            }
            S = P;
        }
        return null;
    }

    private boolean hitPolygon(Vector2f start, Vector2f end, Vector2f S, Vector2f P){
        if (isZero(getPointLineDistance(end, S, P))){
            return true;
        }else if (!isZero(getPointLineDistance(start, S, P))){
            return lineLineIntersection(start, end, S, P);
        }else {
            return false;
        }
    }

    private boolean isZero(float value){
        return Math.abs(value) < EPSILON;
    }

    private float getPointLineDistance(Vector2f P, Vector2f S, Vector2f Q){
        Vector2f v = Q.sub(S);
        Vector2f n = v.perp().norm();
        return n.dot(P.sub(Q));
    }

    private boolean lineLineIntersection(Vector2f A, Vector2f B, Vector2f C, Vector2f D){
        Vector2f DsubC = D.sub(C);
        Vector2f DsubCperp = DsubC.perp();
        Vector2f AsubB = A.sub(B);
        float f = DsubCperp.dot(AsubB);
        if (Math.abs(f) < EPSILON){
            return false;
        }
        Vector2f AsubC = A.sub(C);
        float d = DsubCperp.dot(AsubC);
        if (f > 0){
            if (d < 0 || d > f)return false;
        }else {
            if (d > 0 || d < f)return false;
        }
        Vector2f BsubA = B.sub(A);
        Vector2f BsubAperp = BsubA.perp();
        float e = BsubAperp.dot(AsubC);
        if (f > 0){
            if (e < 0 || e > f)return false;
            if (e > 0 || e < f) return false;
        }
        return true;
    }

    private Vector2f calculateReflection(Vector2f V, Vector2f p0, Vector2f P1){
        Vector2f Pv = p0.sub(P1);
        Vector2f n = Pv.perp().norm();
        Vector2f Vn = n.mul(V.dot(n));
        Vector2f Vp = V.sub(Vn);
        return Vp.sub(Vn);
    }
}
