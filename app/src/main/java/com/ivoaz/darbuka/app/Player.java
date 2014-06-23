package com.ivoaz.darbuka.app;

import android.content.Context;

import com.ivoaz.darbuka.app.MainActivity.OnRhythmChangeListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Player implements OnRhythmChangeListener {

    private Context context;

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> future;

    private boolean playing = false;
    private int position;
    private double mod;

    private Darbuka darbuka;
    private char[] rhythm;
    private int bpm;

    public Player(Context context, Darbuka darbuka) {
        this.context = context;
        this.darbuka = darbuka;

        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void play() {
        if (playing) return;

        playing = true;
        darbuka.start();
        position = 0;

        mod = 1000 * bpm * 2 / 60000;
        future = executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                playDarbuka();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void playDarbuka() {
        StringBuilder builder = new StringBuilder();

        double realLength = (double)1000 * bpm * 2 / 60000 + mod;
        int length = (int)realLength;
        mod = realLength - (double)length;

        int n;
        while (length > 0) {
            if (position >= rhythm.length) {
                position = 0;
            }

            n = rhythm.length - position;
            if (length < n) {
                n = length;
            }

            builder.append(rhythm, position, n);

            length -= n;
            position += n;
        }

        darbuka.play(builder.toString(), bpm);
    }

    public void stop() {
        if (!playing) return;

        playing = false;
        darbuka.stop();
        future.cancel(false);
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void onRhythmChange(String rhythm) {
        this.rhythm = rhythm.replace("\n", "").toCharArray();

    }

    @Override
    public void onBpmChangeListener(Bpm bpm) {
        this.bpm = bpm.getValue();
    }

}
