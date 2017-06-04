package own.gamelearn.audio;

/**
 * Created by kakushouwa on 2017/6/3.
 */
public class LoopEvent extends SoundEvent {

    public static final String STATE_WAITING = "waiting";
    public static final String STATE_RUNNING = "running";
    public static final String EVENT_FIRE = "fire";
    public static final String EVENT_DONE = "done";
    private String currentState;

    public LoopEvent(AudioStream audio) {
        super(audio);
        currentState = STATE_WAITING;
    }

    public void fire() {
        put(EVENT_FIRE);
    }

    public void done() {
        put(EVENT_DONE);
    }

    @Override
    protected void processEvent(String event) throws InterruptedException {
        System.out.println("Got " + event + " Event");
        if (currentState.equals(STATE_WAITING)){
            if (event.equals(EVENT_FIRE)){
                audio.open();
                audio.loop(AudioStream.LOOP_CONTINUOUSLY);
                currentState = STATE_RUNNING;
            }
        }else if (currentState.equals(STATE_RUNNING)){
            if (event.equals(EVENT_DONE)){
                audio.stop();
                audio.close();
                currentState = STATE_WAITING;
            }
        }
    }
}
