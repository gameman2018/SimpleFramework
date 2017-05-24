package own.gamelearn.support;

/**
 * Created by kakushouwa on 2017/4/24.
 */
public class Vector2f {

    public float x;
    public float y;
    public float w;

    public Vector2f(){
        x = 0.0f;
        y = 0.0f;
        w = 1.0f;
    }

    public Vector2f(Vector2f v){
        x = v.x;
        y = v.y;
        w = v.w;
    }

    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
        w = 1.0f;
    }

    public Vector2f(float x, float y, float w){
        this.x = x;
        this.y = y;
        this.w = w;
    }

    public void translate(float x, float y){
        this.x += x;
        this.y += y;
    }

    public void scale(float x, float y){
        this.x *= x;
        this.y *= y;
    }

    public void rotate(float rad){
        float tmp = (float) (x * Math.cos(rad) - y * Math.sin(rad));
        this.x = tmp;
    }

    public void shear(float sx, float sy){
        float tmp = x + sx * y;
        y = y + sy * x;
        x = tmp;
    }

    public Vector2f inv(){
        return new Vector2f(-x,-y);
    }

    public Vector2f add(Vector2f v){
        return new Vector2f(x + v.x,y + v.y);
    }

    public Vector2f sub(Vector2f v){
        return new Vector2f(x - v.x,y - v.y);
    }

    public Vector2f mul(float scalar){
        return new Vector2f(x * scalar,y * scalar);
    }
    public Vector2f div(float scalar){
        return new Vector2f(x / scalar, y / scalar);
    }

    public float len(){
        return (float)Math.sqrt(x*x + y*y);
    }

    public float lenSqr(){
        return x*x + y*y;
    }

    public Vector2f norm(){
        return div(len());
    }

    public Vector2f perp(){
        return new Vector2f(-y, x);
    }

    public float dot(Vector2f v){
        return x * v.x + y * v.y;
    }

    public float angle(){
        return (float)Math.atan2(y,x);
    }

    public static Vector2f polar(float angle, float radius){
        return new Vector2f(radius * (float)Math.cos(angle),radius * (float)Math.sin(angle));
    }
}
