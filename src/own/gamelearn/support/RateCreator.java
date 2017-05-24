package own.gamelearn.support;

/**
 * Created by kakushouwa on 2017/4/28.
 */
public class RateCreator {
    private long current;
    private long last;
    private long delta;
    private int count;
    private int fps;

    public void initialize(){
        last = System.currentTimeMillis();
        count = 0;
        fps = 0;
        delta = 0;
    }

    public void calculate(){
        current = System.currentTimeMillis();
        delta += current - last;
        last = current;
        count++;
        if (delta > 1000){
            delta -= 1000;
            fps = count;
            count = 0;
        }
    }

    public String getRate(){
        return "FPS " + fps;
    }
}
