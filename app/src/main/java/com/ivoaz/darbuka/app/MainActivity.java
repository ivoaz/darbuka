package com.ivoaz.darbuka.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {

    private final static int DEFAULT_RHYTHM = 0;
    private final static int DEFAULT_BPM = 4;

    private Button playButton;
    private EditText rhythmInput;
    private Player player;

    public interface OnRhythmChangeListener {

        public void onRhythmChange(String rhythm);

        public void onBpmChangeListener(Bpm bpm);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = new Player(this, new Darbuka(this));

        initRhythmInput();
        initRhythmSpinner();
        initBpmSpinner();
        initPlayButton();
    }

    private void initRhythmInput() {
        rhythmInput = (EditText) findViewById(R.id.rhythmInput);
        rhythmInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                player.onRhythmChange(editable.toString());
            }
        });
    }

    private void initRhythmSpinner() {
        Spinner rhythmSpinner = (Spinner) findViewById(R.id.rhythmSpinner);

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, new Rhythm[] {
                new Rhythm("Ayoub", "D--kD-T-"),
                new Rhythm("Fellahi", "DK-KD-K-"),
                new Rhythm("Khaleeji", "D--D--T-"),
                new Rhythm("Karachi", "T--kT-D-"),
                new Rhythm("Bayou", "D--DD-T-"),
                new Rhythm("Malfuf", "D--T--T-"),
                new Rhythm("Saidi", "D-T---D-D---T---"),
                new Rhythm("Maksoum", "D-T---T-D---T---"),
                new Rhythm("Beledi", "D-D---T-D---T---"),
                new Rhythm("Cifteteli", "D---K-T---K-T---D---D---T-------"),
                new Rhythm("Masmoudi", "D---D-------T---D-----T-----T---"),
                new Rhythm("Masmoudi filled", "D---D---t-k-T-k-D---t-k-T---T-k-"),
                new Rhythm("Masmoudi filled 2", "D---D---tktkT-tkD-tktkt-TktkT-tk"),
                new Rhythm("Cifteteli filled", "D-tkt-t-TKD-T-TKD-tkD-D-T-------"),
                new Rhythm("Muhajjar", "D-tkD-tkD-tkt-k-T-tkt-k-D-tkt-T-tktkT-k-T-tkt-k-T---T---"),
                new Rhythm("Super Saidi", "D-D-tkD-D-tkS-tk\n" +
                        "-kS-tkD-D-tkS-tk\n" +
                        "tkD-tkD-D-tkS-kS\n" +
                        "-kS-tkD-D-tkS-tk"),
        });
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rhythmSpinner.setAdapter(spinnerArrayAdapter);

        rhythmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Rhythm rhythm = (Rhythm) adapterView.getItemAtPosition(position);

                rhythmInput.setText(rhythm.getString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rhythmSpinner.setSelection(DEFAULT_RHYTHM);
    }

    private void initBpmSpinner() {
        Spinner bpmSpinner = (Spinner) findViewById(R.id.bpmSpinner);

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
        bpmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                player.onBpmChangeListener((Bpm)adapterView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bpmSpinner.setSelection(DEFAULT_BPM);
    }

    private void initPlayButton() {
        playButton = (Button)findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.stop();

                    playButton.setText(getString(R.string.play_button));
                }
                else {
                    player.play();

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
