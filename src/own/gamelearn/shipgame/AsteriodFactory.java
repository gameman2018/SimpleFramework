package own.gamelearn.shipgame;

import own.gamelearn.support.Matrix3x3f;
import own.gamelearn.support.Vector2f;

import java.util.Random;

/**
 * Created by kakushouwa on 2017/5/13.
 */
public class AsteriodFactory {
    private static final Vector2f[][] LARGE = {
            {
                    new Vector2f(-0.26760566f, 0.39593112f),
                    new Vector2f(-0.07981223f, 0.53051645f),
                    new Vector2f(0.1267606f, 0.4428795f),
                    new Vector2f(0.23630667f, 0.2832551f),
                    new Vector2f(0.16431928f, 0.07042253f),
                    new Vector2f(-0.039123654f, 0.039123654f),
                    new Vector2f(-0.21752739f, 0.1267606f)
            },
            {
                    new Vector2f(-0.13615024f, 0.18935835f),
                    new Vector2f(-0.22378719f, -0.16744912f),
                    new Vector2f(0.07355237f, -0.40845072f),
                    new Vector2f(0.23943663f, -0.23943663f),
                    new Vector2f(0.23317683f, 0.0015649796f)
            },
            {
                    new Vector2f(-0.08920187f, 0.16118938f),
                    new Vector2f(-0.20500785f, -0.017214417f),
                    new Vector2f(-0.13615024f, -0.21439743f),
                    new Vector2f(-0.0015649796f, -0.2707355f),
                    new Vector2f(0.1205008f, -0.15492952f),
                    new Vector2f(0.1205008f, -0.0046948195f),
                    new Vector2f(0.029733896f, 0.104851365f),
            }
    };
    private static final Vector2f[][] MEDIUM = {
            {
                    new Vector2f(-0.07355243f, 0.13615024f),
                    new Vector2f(-0.14553994f, 0.020344317f),
                    new Vector2f(-0.1173709f, -0.098591566f),
                    new Vector2f(0.026604056f, -0.12989044f),
                    new Vector2f(0.12989044f, -0.07668233f),
                    new Vector2f(0.13302028f, 0.035993755f),
                    new Vector2f(0.104851365f, 0.15179968f),
                    new Vector2f(0.039123654f, 0.15492958f),
                    new Vector2f(0.014084458f, 0.16744918f),
            },
            {
                    new Vector2f(-0.09233177f, 0.12989044f),
                    new Vector2f(-0.17996871f, -0.035993695f),
                    new Vector2f(-0.1236307f, -0.12989044f),
                    new Vector2f(0.048513293f, -0.17996871f),
                    new Vector2f(0.14241004f, -0.1205008f),
                    new Vector2f(0.19561815f, -0.020344257f),
                    new Vector2f(0.08920181f, 0.029733956f),
                    new Vector2f(0.13615024f, 0.07355243f),
                    new Vector2f(0.08920181f, 0.114241004f),
                    new Vector2f(-0.026604056f, 0.14553994f),
            },
            {
                    new Vector2f(-0.05790299f, 0.1236307f),
                    new Vector2f(-0.101721466f, 0.026604056f),
                    new Vector2f(-0.12989044f, -0.054773092f),
                    new Vector2f(-0.014084518f, -0.17057908f),
                    new Vector2f(0.104851365f, -0.15179968f),
                    new Vector2f(0.114241004f, -0.051643133f),
                    new Vector2f(0.19561815f, 0.023474216f),
                    new Vector2f(0.18935835f, 0.1205008f),
                    new Vector2f(0.107981205f, 0.15179968f),
                    new Vector2f(0.035993695f, 0.15179968f),

            }
    };
    private static final Vector2f[][] SMALL = {
            {
                    new Vector2f(-0.029733956f, 0.07981223f),
                    new Vector2f(-0.07042253f, 0.029733956f),
                    new Vector2f(-0.07668233f, -0.023474216f),
                    new Vector2f(-0.048513293f, -0.07042253f),
                    new Vector2f(0.023474216f, -0.08294213f),
                    new Vector2f(0.06729269f, -0.08294213f),
                    new Vector2f(0.07042253f, -0.039123654f),
                    new Vector2f(0.114241004f, -0.023474216f),
                    new Vector2f(0.117370844f, 0.014084518f),
                    new Vector2f(0.101721406f, 0.048513293f),
                    new Vector2f(0.07042253f, 0.07042253f),
                    new Vector2f(0.08294213f, 0.09233177f),
                    new Vector2f(0.029733896f, 0.08294213f),
            },
            {
                    new Vector2f(-0.048513293f, 0.07981223f),
                    new Vector2f(-0.09233177f, 0.017214417f),
                    new Vector2f(-0.09546167f, -0.042253494f),
                    new Vector2f(-0.029733956f, -0.107981205f),
                    new Vector2f(0.045383453f, -0.054773092f),
                    new Vector2f(0.051643133f, 0.007824719f),
                    new Vector2f(0.045383453f, 0.051643193f),
                    new Vector2f(0.07042253f, 0.06103289f),
                    new Vector2f(0.010954618f, 0.07355243f),
            },
            {
                    new Vector2f(-0.035993755f, 0.06729263f),
                    new Vector2f(0.045383453f, 0.06416279f),
                    new Vector2f(0.054773092f, 0.020344317f),
                    new Vector2f(0.06416273f, -0.042253494f),
                    new Vector2f(0.045383453f, -0.054773092f),
                    new Vector2f(-0.0015649796f, -0.051643133f),
                    new Vector2f(-0.020344317f, -0.07355237f),
                    new Vector2f(-0.07668233f, -0.06416273f),
                    new Vector2f(-0.07355243f, -0.029733896f),
                    new Vector2f(-0.08607197f, -0.014084458f),
                    new Vector2f(-0.035993755f, 0.007824719f),
                    new Vector2f(-0.08294213f, 0.032863855f),
                    new Vector2f(-0.051643193f, 0.05790299f),
                    new Vector2f(-0.035993755f, 0.06103289f),
            }
    };
    private Wrapper wrapper;
    private Random random;

    public AsteriodFactory(Wrapper wrapper){
        this.wrapper = wrapper;
        random = new Random();
    }

    public Asteriod createLargeAsteriod(Vector2f position){
        Asteriod asteriod = new Asteriod(wrapper);
        asteriod.setPosition(position);
        asteriod.setPolygon(getRandomAsteriod(LARGE));
        asteriod.setSize(Asteriod.Size.Large);
        return asteriod;
    }

    public Asteriod createMediumAsteriod(Vector2f position){
        Asteriod asteriod = new Asteriod(wrapper);
        asteriod.setPosition(position);
        asteriod.setPolygon(getRandomAsteriod(MEDIUM));
        asteriod.setSize(Asteriod.Size.Medium);
        return asteriod;
    }

    public Asteriod createSmallAsteriod(Vector2f position){
        Asteriod asteriod = new Asteriod(wrapper);
        asteriod.setPosition(position);
        asteriod.setPolygon(getRandomAsteriod(SMALL));
        asteriod.setSize(Asteriod.Size.Samll);
        return asteriod;
    }

    private Vector2f[] getRandomAsteriod(Vector2f[][] asteroids){
        return mirror(asteroids[random.nextInt(asteroids.length)]);
    }

    private Vector2f[] mirror(Vector2f[] polygon){
        Vector2f[] mirror = new Vector2f[polygon.length];
        float x = random.nextBoolean() ? -1.0f : 1.0f;
        float y = random.nextBoolean() ? -1.0f : 1.0f;
        Matrix3x3f matrix3x3f = Matrix3x3f.scale(x,y);//因为这个是乘法
        for (int i = 0;i < polygon.length;i++){
            mirror[i] = matrix3x3f.mul(polygon[i]);
        }
        return mirror;
    }
}
