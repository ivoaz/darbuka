package com.ivoaz.darbuka.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity {

    protected Spinner rhythmSpinner;
    protected Spinner bpmSpinner;
    protected EditText rhythmInput;
    protected Button playButton;

    protected Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rhythmInput = (EditText)findViewById(R.id.rhythmInput);

        player = new Player(this);

        initRhythmSpinner();
        initBpmSpinner();
        initPlayButton();
    }

    protected void initRhythmSpinner() {
        rhythmSpinner = (Spinner)findViewById(R.id.rhythmSpinner);

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, new Rhythm[] {
                new Rhythm("Ayoub", "D--kD-T-"),
                new Rhythm("Fellahi", "DK-KD-K-"),
                new Rhythm("Khaleeji", "D--D--T-"),
                new Rhythm("Karachi", "T--kT-D-"),
                new Rhythm("Bayou", "D--DD-T-"),
                new Rhythm("Malfuf", "D--T--T-"),
                new Rhythm("Saidi", "D-T---D-D---T---"),
                new Rhythm("Super Saidi", "D-D-tkD-D-tkS-tk\n" +
                                          "-kS-tkD-D-tkS-tk\n" +
                                          "tkD-tkD-D-tkS-kS\n" +
                                          "-kS-tkD-D-tkS-tk"),
                new Rhythm("Maksoum", "D-T---T-D---T---"),
                new Rhythm("Beledi", "D-D---T-D---T---"),
                new Rhythm("Cifteteli", "D---K-T---K-T---D---D---T-------"),
                new Rhythm("Masmoudi", "D---D---tktkT---D-tktkT-tktkT---"),
                new Rhythm("Custom", "")
        });
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rhythmSpinner.setAdapter(spinnerArrayAdapter);

        rhythmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Rhythm rhythm = (Rhythm)rhythmSpinner.getSelectedItem();

                rhythmInput.setText(rhythm.getString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rhythmSpinner.setSelection(0);
    }

    protected void initBpmSpinner() {
        bpmSpinner = (Spinner)findViewById(R.id.bpmSpinner);

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, new Bpm[] {
                new Bpm(40),
                new Bpm(60),
                new Bpm(80),
                new Bpm(120),
                new Bpm(140),
                new Bpm(180),
                new Bpm(200),
                new Bpm(220),
                new Bpm(240),
                new Bpm(300)
        });
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bpmSpinner.setAdapter(spinnerArrayAdapter);
        bpmSpinner.setSelection(3);
    }

    protected void initPlayButton() {
        playButton = (Button)findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.stop();

                    playButton.setText(getString(R.string.play_button));
                }
                else {
                    player.play(
                            rhythmInput.getText().toString(),
                            (Bpm)bpmSpinner.getSelectedItem());

                    playButton.setText(getString(R.string.play_button_stop));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        player.stop();

        super.onDestroy();
    }
}
