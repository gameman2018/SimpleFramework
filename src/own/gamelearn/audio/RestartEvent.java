package own.gamelearn.audio;

/**
 * Created by kakushouwa on 2017/6/3.
 */
public class RestartEvent extends SoundEvent {

    public static final String STATE_WAITING = "waiting";
    public static final String STATE_RUNNING = "running";
    public static final String EVENT_FIRE = "fire";
    public static final String EVENT_DONE = "done";
    private String currentState;

    public RestartEvent(AudioStream audio) {
        super(audio);
        currentState = STATE_WAITING;
    }

    public void fire(){
        put(EVENT_FIRE);
    }

    @Override
    protected void processEvent(String event) throws InterruptedException {
        System.out.println("GOt " + event + " Event");
        if (currentState.equals(STATE_WAITING)){
            if (event.equals(EVENT_FIRE)){
                currentState = STATE_RUNNING;
                audio.open();
                audio.start();
            }
        } else if (currentState.equals(STATE_RUNNING)){
            if (event.equals(EVENT_FIRE)){
                audio.restart();
            }
            if (event.equals(EVENT_DONE)){
                currentState = STATE_WAITING;
                audio.close();
            }
        }
    }

    @Override
    protected void onAudioFinished() {
        put(EVENT_DONE);
    }
}
