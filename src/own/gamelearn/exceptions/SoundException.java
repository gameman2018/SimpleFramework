package own.gamelearn.exceptions;

/**
 * Created by kakushouwa on 2017/6/1.
 */
public class SoundException extends RuntimeException {
    public SoundException(String message) {
        super(message);
    }

    public SoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
