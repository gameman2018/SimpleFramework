package own.gamelearn.shipgame;

import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Vector2f;

import java.util.ArrayList;

/**
 * Created by kakushouwa on 2017/5/13.
 */
public class Wrapper {

    private float worldWidth;
    private float worldHeight;
    private Vector2f worldMin;
    private Vector2f worldMax;

    public Wrapper(float worldWidth, float worldHeight){
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        worldMax = new Vector2f(worldWidth / 2.0f, worldHeight / 2.0f);
        worldMin = worldMax.inv();
    }

    public boolean hasLeftWorld(Vector2f position){
        return position.x > worldMax.x || position.x < worldMin.x || position.y > worldMax.y || position.y < worldMin.y;
    }

    public Vector2f wrapPosition(Vector2f position){
        Vector2f wrapped = new Vector2f(position);
        if (position.x < worldMin.x){
            wrapped.x += worldWidth;
        }else if (position.x > worldMax.x){
            wrapped.x -= worldWidth;
        }
        if (position.y < worldMin.y){
            wrapped.y += worldHeight;
        }else if (position.y > worldMax.y){
            wrapped.y -= worldHeight;
        }
        return wrapped;
    }

    public void wrapPolygon(Vector2f[] polygon, ArrayList<Vector2f[]> renderList){
        Vector2f min = getPolygonMin(polygon);
        Vector2f max = getPolygonMax(polygon);
        boolean north = max.y > worldMax.y;
        boolean south = min.y < worldMin.y;
        boolean west = min.x < worldMin.x;
        boolean east = max.x > worldMax.x;
        if (north){
            renderList.add(wrapNorth(polygon));
        }
        if (south){
            renderList.add(wrapSouth(polygon));
        }
        if (west){
            renderList.add(wrapWest(polygon));
        }
        if (east){
            renderList.add(wrapEast(polygon));
        }
        if (north && west){
            renderList.add(wrapNorthWest(polygon));
        }
        if (north && east){
            renderList.add(wrapNorthEast(polygon));
        }
        if (south && west){
            renderList.add(wrapSouthWest(polygon));
        }
        if (south && east){
            renderList.add(wrapSouthEast(polygon));
        }

    }

    private Vector2f[] wrapNorth(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(0.0f,worldHeight));
    }

    private Vector2f[] wrapSouth(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(0.0f,-worldHeight));
    }

    private Vector2f[] wrapWest(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(worldWidth,0.0f));
    }

    private Vector2f[] wrapEast(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(-worldWidth,0.0f));
    }

    private Vector2f[] wrapNorthWest(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(worldWidth,worldHeight));
    }

    private Vector2f[] wrapNorthEast(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(worldWidth,-worldHeight));
    }

    private Vector2f[] wrapSouthWest(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(worldWidth,-worldHeight));
    }

    private Vector2f[] wrapSouthEast(Vector2f[] polygon){
        return transform(polygon,Matrix3x3f.translate(-worldWidth,-worldHeight));
    }

    private Vector2f[] transform(Vector2f[] polygon,Matrix3x3f view){
        Vector2f[] cpy = new Vector2f[polygon.length];
        for (int i = 0;i < cpy.length;i++){
            cpy[i] = view.mul(polygon[i]);
        }
        return cpy;
    }

    private Vector2f getPolygonMin(Vector2f[] polygon){
        Vector2f min = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
        for (Vector2f v : polygon){
            if (v.x < min.x) min.x = v.x;
            if (v.y < min.y) min.y = v.y;
        }
        return min;
    }

    private Vector2f getPolygonMax(Vector2f[] polygon){
        Vector2f max = new Vector2f(-Float.MAX_VALUE, -Float.MAX_VALUE);
        for (Vector2f v : polygon){
            if (v.x > max.x) max.x = v.x;
            if (v.y > max.y) max.y = v.y;
        }
        return max;
    }
}
