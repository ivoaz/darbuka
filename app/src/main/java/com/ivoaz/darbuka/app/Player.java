package com.ivoaz.darbuka.app;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Player {
    protected boolean playing = false;
    protected int currentNote;

    protected ScheduledExecutorService executor;
    protected ScheduledFuture<?> future;
    protected SoundPool soundPool;

    protected HashMap<Character, Integer> sounds;

    public Player(Context context) {
        executor = Executors.newScheduledThreadPool(2);

        initSoundPool(context);
    }

    protected void initSoundPool(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        sounds = new HashMap<Character, Integer>();

        sounds.put('d', soundPool.load(context, R.raw.d, 1));
        sounds.put('D', soundPool.load(context, R.raw.d_big, 1));
        sounds.put('k', soundPool.load(context, R.raw.k, 1));
        sounds.put('K', soundPool.load(context, R.raw.k_big, 1));
        sounds.put('p', soundPool.load(context, R.raw.p, 1));
        sounds.put('P', soundPool.load(context, R.raw.p_big, 1));
        sounds.put('s', soundPool.load(context, R.raw.s, 1));
        sounds.put('S', soundPool.load(context, R.raw.s_big, 1));
        sounds.put('t', soundPool.load(context, R.raw.t, 1));
        sounds.put('T', soundPool.load(context, R.raw.t_big, 1));
    }

    public void play(String rhythm, Bpm bpm) {
        if (playing) return;

        playing = true;
        currentNote = 0;

        final long noteLength = (60 * 1000) / bpm.getValue() / 2;
        final Integer[] ids = parseRhythm(rhythm);

        future = executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (ids[currentNote] != null) {
                    soundPool.play(ids[currentNote], 1, 1, 0, 0, 1);
                }

                if (++currentNote == ids.length) currentNote = 0;
            }
        }, 0, noteLength, TimeUnit.MILLISECONDS);
    }

    protected Integer[] parseRhythm(String rhythm) {
        char[] chars = rhythm.replace("\n", "").toCharArray();
        int length = chars.length;
        Integer[] ids = new Integer[length];

        for (int i = 0; i < length; ++i) {
            ids[i] = sounds.get(chars[i]);
        }

        return ids;
    }

    public void stop() {
        if (!playing) return;

        playing = false;

        future.cancel(false);
    }

    public boolean isPlaying() {
        return playing;
    }
}
