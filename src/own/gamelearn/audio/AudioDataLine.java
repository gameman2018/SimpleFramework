package own.gamelearn.audio;

import own.gamelearn.exceptions.SoundException;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 挨踢狗 on 2017/6/2.
 */
public class AudioDataLine implements Runnable {

    private static final int BUFFER_SIZE_MS = 50;
    private List<LineListener> lineListeners = Collections.synchronizedList(new ArrayList<LineListener>());
    private Thread writer;
    private AudioFormat audioFormat;
    private SourceDataLine dataLine;
    private byte[] rawData;
    private byte[] soundData;
    private int bufferSize;
    private int loopCount;
    private volatile boolean restart = false;


    public AudioDataLine(byte[] rawData) {
        this.rawData = rawData;
    }

    public void initialize() {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(rawData);
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            audioFormat = ais.getFormat();
            bufferSize = computeBufferSize(BUFFER_SIZE_MS);
            soundData = readSoundData(ais);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readSoundData(AudioInputStream ais) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long chunk = audioFormat.getFrameSize();
            byte[] buf = new byte[(int) chunk];
            while (ais.read(buf) != -1) {
                out.write(buf);
            }
            ais.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addLineListener(LineListener lineListener) {
        lineListeners.add(lineListener);
    }

    private int computeBufferSize(int ms) {
        double sampleRate = audioFormat.getSampleRate();
        double bitSize = audioFormat.getSampleSizeInBits();
        double channels = audioFormat.getChannels();
        System.out.println("Sample Rate: " + sampleRate);
        System.out.println("Bit Size: " + bitSize);
        System.out.println("Channels: " + channels);
        System.out.println("Milliseconds: " + ms);
        if (bitSize == AudioSystem.NOT_SPECIFIED ||
                sampleRate == AudioSystem.NOT_SPECIFIED ||
                channels == AudioSystem.NOT_SPECIFIED) {
            System.out.println("BufferSize: " + -1);
            return -1;
        } else {
            double temp = ms;
            double frames = sampleRate * temp / 1000.0;
            while (frames != Math.floor(frames)) {
                temp++;
                frames = sampleRate * temp / 1000.0;
            }
            double bytesPerFrame = bitSize / 8.0;
            double size = (int) (frames * bytesPerFrame * channels);
            System.out.println("BufferSize: " + size);
            return (int) size;
        }
    }

    public void open() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            synchronized (lineListeners) {
                for (LineListener lineListener : lineListeners) {
                    dataLine.addLineListener(lineListener);
                }
            }
            dataLine.open(audioFormat, bufferSize);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new SoundException(e.getMessage(), e);
        }
    }

    public void close() {
        dataLine.close();
    }

    public void start() {
        loopCount = 0;
        dataLine.flush();
        dataLine.start();
        writer = new Thread(this);
        writer.start();
    }

    public void restart() {
        restart = true;
    }

    public void loop(int count) {
        loopCount = count;
        dataLine.flush();
        dataLine.start();
        writer = new Thread(this);
        writer.start();
    }

    public void stop() {
        if (writer != null) {
            Thread tmp = writer;
            writer = null;
            try {
                tmp.join(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Line getLine() {
        return dataLine;
    }

    @Override
    public void run() {
        System.out.println("Run Stream");
        try {
            while (true) {
                int written = 0;
                int length = bufferSize == -1 ? dataLine.getBufferSize() : bufferSize;
                while (written < soundData.length) {
                    if (Thread.currentThread() != writer) {
                        System.out.println("Stream Canceled");
                        loopCount = 0;
                        break;
                    } else if (restart) {
                        restart = false;
                        System.out.println("Stream canceled");
                        if (loopCount != AudioStream.LOOP_CONTINUOUSLY) {
                            loopCount++;
                        }
                        break;
                    }
                    int byteLeft = soundData.length - written;
                    int toWrite = byteLeft > length * 2 ? length : byteLeft;
                    written += dataLine.write(soundData, written, toWrite);
                }
                if (loopCount == 0) {
                    break;
                } else if (loopCount != AudioStream.LOOP_CONTINUOUSLY) {
                    loopCount--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Stream finished");
            dataLine.drain();
            dataLine.stop();
        }
    }
}
