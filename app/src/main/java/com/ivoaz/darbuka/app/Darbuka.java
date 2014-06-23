package com.ivoaz.darbuka.app;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Darbuka {
    private static final int SAMPLE_RATE = 48000;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int NUM_CHANNELS = 1;
    private static final int SHORTS_PER_MS = SAMPLE_RATE * BITS_PER_SAMPLE * NUM_CHANNELS / 8 / 1000 / 2;

    private Context context;

    private AudioTrack track;

    private HashMap<Character, Wave> sounds;
    private short[] tail;

    public Darbuka(Context context) {
        this.context = context;

        sounds = new HashMap<Character, Wave>();
        loadSounds();
    }

    private void loadSounds() {
        try {
            sounds.put('d', new Wave(context.getResources().openRawResource(R.raw.d)));
            sounds.put('D', new Wave(context.getResources().openRawResource(R.raw.d)));
            sounds.put('k', new Wave(context.getResources().openRawResource(R.raw.k)));
            sounds.put('K', new Wave(context.getResources().openRawResource(R.raw.k_big)));
            sounds.put('p', new Wave(context.getResources().openRawResource(R.raw.p)));
            sounds.put('P', new Wave(context.getResources().openRawResource(R.raw.p_big)));
            sounds.put('s', new Wave(context.getResources().openRawResource(R.raw.s)));
            sounds.put('S', new Wave(context.getResources().openRawResource(R.raw.s_big)));
            sounds.put('t', new Wave(context.getResources().openRawResource(R.raw.t)));
            sounds.put('T', new Wave(context.getResources().openRawResource(R.raw.t_big)));
        } catch (IOException e) {

        }

        int tailSize = 0;

        for (HashMap.Entry<Character, Wave> entry : sounds.entrySet()) {
            Wave wave = entry.getValue();

            if (wave.getNumChannels() != NUM_CHANNELS || wave.getSampleRate() != SAMPLE_RATE || wave.getBitsPerSample() != BITS_PER_SAMPLE) {
                sounds.remove(entry.getKey());
                continue;
            }

            int size = wave.getData().length;
            if (size > tailSize) {
                tailSize = size;
            }
        }

        tail = new short[tailSize];
    }

    public void start() {
        track = new AudioTrack(
                AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, SAMPLE_RATE, AudioTrack.MODE_STREAM
        );
        track.play();
        Arrays.fill(tail, (short) 0);
    }

    public void stop() {
        track.pause();
        track = null;
    }

    public void play(String rhythm, int bpm) {
        char[] chars = rhythm.toCharArray();
        int length = chars.length;

        int hitLength = (60000) / bpm / 2;

        int size = hitLength * length * SHORTS_PER_MS;

        final short[] data = new short[size];
        Arrays.fill(data, (short)0);

        short[] lastTail = tail.clone();
        Arrays.fill(tail, (short)0);
        addData(data, 0, lastTail, tail);

        for (int i = 0; i < length; ++i) {
            if (chars[i] == '-') {
                continue;
            }

            Wave sound = sounds.get(chars[i]);

            if (null == sound) {
                continue;
            }

            int offset = i * hitLength * SHORTS_PER_MS;

            addData(data, offset, sound.getData(), tail);
        }


        track.write(data, 0, data.length);

    }

    private static void addData(short[] data, int offset, short[] sound, short[] tail) {
        int size = data.length;
        int end = offset + sound.length;

        if (end > size) {
            for (int i = size - offset, j = 0; i < sound.length; ++i, ++j) {
                tail[j] = add(tail[j], sound[i]);
            }

            end = size;
        }

        for (int i = 0; offset < end; ++i, ++offset) {
            data[offset] = add(data[offset], sound[i]);
        }
    }

    private static short add(short val1, short val2) {
        int mixed = val1 + val2;

        if (mixed > 32767) {
            mixed = 32767;
        }
        else if (mixed < -32768) {
            mixed = -32768;
        }

        return (short)mixed;
    }
}
