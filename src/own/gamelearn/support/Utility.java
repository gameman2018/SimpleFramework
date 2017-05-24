package own.gamelearn.support;


import java.awt.*;
import java.util.List;

/**
 * Created by kakushouwa on 2017/5/6.
 */
public class Utility {
    public static Matrix3x3f createReverseViewport(float worldWidth, float worldHeight, float screenWidth, float screenHeight){
        float sx = worldWidth / (screenWidth - 1.0f);
        float sy = worldHeight / (screenHeight - 1.0f);
        float tx = (screenWidth - 1.0f) / 2.0f;
        float ty = (screenHeight - 1.0f) / 2.0f;
        Matrix3x3f viewport = Matrix3x3f.translate(-tx, -ty);
        viewport = viewport.mul(Matrix3x3f.scale(sx,-sy));
        return viewport;
    }
    public static Matrix3x3f createViewport(float worldWidth, float worldHeight, float screenWidth, float screenHeight){
        float sx = (screenWidth - 1.0f) / worldWidth;
        float sy = (screenHeight - 1.0f) / worldHeight;
        float tx = (screenWidth - 1.0f) / 2.0f;
        float ty = (screenHeight - 1.0f) / 2.0f;
        Matrix3x3f viewport = Matrix3x3f.scale(sx,-sy);
        viewport = viewport.mul(Matrix3x3f.translate(tx,ty));
        return viewport;
    }

    public static void drawPolygon(Graphics g, Vector2f[] vector2fs){
        Vector2f S = null;
        Vector2f E = vector2fs[vector2fs.length - 1];
        for (int i = 0;i < vector2fs.length;i++){
            S = vector2fs[i];
            g.drawLine((int)S.x,(int)S.y,(int)E.x,(int) E.y);
            E = S;
        }
    }

    public static void drawPolygon(Graphics g, List<Vector2f> vector2fList){
        Vector2f S = null;
        Vector2f E = vector2fList.get(vector2fList.size() - 1);
        for (Vector2f v : vector2fList){
            S = v;
            g.drawLine((int)S.x,(int)S.y,(int)E.x,(int) E.y);
            E = S;
        }
    }

    public static void drawFill(Graphics g,Vector2f[] polygon){
        Graphics2D g2d = (Graphics2D)g;
        Polygon polygons = new Polygon();
        for (Vector2f v : polygon){
            polygons.addPoint((int)v.x,(int)v.y);
        }
        g2d.fill(polygons);
    }

    public static void drawFill(Graphics g,List<Vector2f> polygon){
        Graphics2D g2d = (Graphics2D)g;
        Polygon polygons = new Polygon();
        for (Vector2f v : polygon){
            polygons.addPoint((int)v.x,(int)v.y);
        }
        g2d.fill(polygons);
    }

    public static int drawString(Graphics g, int x, int y, String str){
        return drawString(g,x,y,new String[]{str});
    }
    public static int drawString(Graphics g, int x, int y, List<String> str){
        return drawString(g,x,y,str.toArray(new String[0]));
    }
    public static int drawString(Graphics g, int x, int y, String... str){
        FontMetrics fm =  g.getFontMetrics();
        int height = fm.getAscent() + fm.getDescent() + fm.getLeading();
        for (String s : str){
            g.drawString(s,x,y+fm.getAscent());
            y += height;
        }
        return y;
    }
}
