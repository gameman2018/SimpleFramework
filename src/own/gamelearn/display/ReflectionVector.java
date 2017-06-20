package own.gamelearn.display;

import own.gamelearn.framework.GameFrameControls;
import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by 挨踢狗 on 2017/6/20.
 */
public class ReflectionVector extends GameFrameControls {
    private static final float EPSILON = 0.00001f;
    private Vector2f s0, s1, p0, p1;
    private Vector2f intersection;
    private Vector2f reflection;

    public ReflectionVector(){
        appWidth = 640;
        appHeight = 640;
        launchApp(this);
    }

    @Override
    protected void initialize() {
        super.initialize();
        s0 = new Vector2f(-0.75f, -0.3f);
        s1 = new Vector2f(0.75f, 0.3f);
        p0 = new Vector2f();
        p1 = new Vector2f();
    }

    @Override
    protected void processInput(double delta) {
        super.processInput(delta);
        p1 = getWorldMousePosition();
        if (appMouse.mouseDownOnce(MouseEvent.BUTTON1)){
            p0 = new Vector2f(p1);
        }
    }

    @Override
    protected void updateObjects(double delta) {
        super.updateObjects(delta);
        intersection =getIntersection(p0,p1,s0,s1);
        if (intersection != null){
            Vector2f Sv = s1.sub(s0);
            Vector2f n = Sv.perp().norm();
            Vector2f V = p1.sub(intersection);
            Vector2f reflect = getReflectionVector(V , n);
            reflection = intersection.add(reflect);
        }else {
            reflection = null;
        }
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        g.drawString("Left Click to move line",20 ,34);
        drawLine(g, s0, s1);
        drawLine(g, p0, p1);
        if (reflection != null){
            g.setColor(Color.BLUE);
            drawLine(g, intersection, reflection);
        }
    }

    private void drawLine(Graphics g, Vector2f v0, Vector2f v1){
        Matrix3x3f view = getViewportTransform();
        Vector2f v0Cpy = view.mul(v0);
        Vector2f v1Cpy = view.mul(v1);
        g.drawLine((int)v0Cpy.x, (int)v0Cpy.y, (int)v1Cpy.x, (int)v1Cpy.y);
    }

    private Vector2f getIntersection(Vector2f A, Vector2f B, Vector2f C, Vector2f D){
        Vector2f DsubC = D.sub(C);
        Vector2f DsubCperp = DsubC.perp();
        Vector2f AsubB = A.sub(B);
        float f = DsubCperp.dot(AsubB);
        if (Math.abs(f) < EPSILON){
            return null;
        }
        Vector2f AsubC = A.sub(C);
        float d = DsubCperp.dot(AsubC);
        if (f > 0){
            if (d < 0 || d > f){
                return null;
            }
        }else {
            if (d > 0 || d < f){
                return null;
            }
        }
        Vector2f BsubA = B.sub(A);
        Vector2f BsubAperp = BsubA.perp();
        float e = BsubAperp.dot(AsubC);
        if (f > 0){
            if (e < 0 || e > f){
                return null;
            }
        }else {
            if (e > 0 || e < f){
                return null;
            }
        }
        return A.add(BsubA.mul(d / f));
    }

    private Vector2f getReflectionVector(Vector2f V, Vector2f n){
        Vector2f Vn = n.mul(V.dot(n));
        Vector2f Vp = V.sub(Vn);
        return Vp.sub(Vn);
    }
}
