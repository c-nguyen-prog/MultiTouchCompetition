package com.chimnguyen.multitouchcompetition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Instruction extends AppCompatActivity {

    private boolean hardMode;
    private int round, size, devicePressure;
    private float pressureLow, pressureHigh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        //get data settings to send back later
        final Bundle extras = getIntent().getExtras();
        round = extras.getInt("ROUND", 3);
        size = extras.getInt("SIZE", 75);
        hardMode = extras.getBoolean("HARD_MODE");
        devicePressure = extras.getInt("DEVICE_PRESSURE", 2);
        pressureLow = extras.getFloat("PRESSURE_LOW", 0);
        pressureHigh = extras.getFloat("PRESSURE_HIGH", 0);
        
        setMusicState();

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/IndieFlower.ttf");
        TextView instructionText = (TextView) findViewById(R.id.instruction_text);
        TextView instructionText1 = (TextView) findViewById(R.id.instruction_text1);
        TextView instructionText2 = (TextView) findViewById(R.id.instruction_text2);
        TextView instructionMode = (TextView) findViewById(R.id.instruction_mode);
        TextView instructionMultiplay = (TextView) findViewById(R.id.instruction_multiplay);
        TextView instructionSingleplay = (TextView) findViewById(R.id.instruction_single_play);
        TextView instructionPressure1 = (TextView) findViewById(R.id.instruction_pressure1);
        TextView instructionPressure2 = (TextView) findViewById(R.id.instruction_pressure2);

        instructionText.setTypeface(custom_font);
        instructionText1.setTypeface(custom_font);
        instructionText2.setTypeface(custom_font);
        instructionMode.setTypeface(custom_font);
        instructionMultiplay.setTypeface(custom_font);
        instructionSingleplay.setTypeface(custom_font);
        instructionPressure1.setTypeface(custom_font);
        instructionPressure2.setTypeface(custom_font);
    }

    private void returnData() {
        Intent i = new Intent();
        i.putExtra("HARD_MODE", hardMode);
        i.putExtra("ROUND", round);
        i.putExtra("SIZE", size);
        i.putExtra("DEVICE_PRESSURE", devicePressure);
        i.putExtra("PRESSURE_LOW", pressureLow);
        i.putExtra("PRESSURE_HIGH", pressureHigh);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    public void changeMusicState(boolean musicState) {
        if (musicState) {
            Launcher.music.start();
            Launcher.musicState = true;
        } else {
            Launcher.music.pause();
            Launcher.musicState = false;
        }
    }

    public void setMusicState() {
        if (Launcher.musicState)
            changeMusicState(true);
        else
            changeMusicState(false);
    }

    public void onBackPressed() {
        returnData();
        super.onBackPressed();
    }

    public void onPause() {
        Launcher.music.pause();
        super.onPause();
    }

    public void onResume() {
        setMusicState();
        super.onResume();
    }
}
