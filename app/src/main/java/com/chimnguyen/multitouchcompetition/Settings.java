package com.chimnguyen.multitouchcompetition;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.*;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.app.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Settings extends AppCompatActivity {

    private boolean hardMode;
    private int round, size;
    private int devicePressure;
    private float pressureLow, pressureHigh;
    private float min, max;
    private Button calibration;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        TextView textMode = (TextView) findViewById(R.id.text_mode);
        TextView textRound = (TextView) findViewById(R.id.text_round);
        TextView subtext = (TextView) findViewById(R.id.sub_text);
        final TextView textView = (TextView) findViewById(R.id.number_round);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_round);
        final TextView circleSize = (TextView) findViewById(R.id.circle_size);
        final TextView circleInput = (TextView) findViewById(R.id.circle_input);
        SeekBar seekbar_size = (SeekBar) findViewById(R.id.seekbar_circle);
        Button button = (Button) findViewById(R.id.button);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewS);
        calibration = (Button) findViewById(R.id.calibration);
        final Button calibrationL = (Button) findViewById(R.id.calibration_low);
        final Button calibrationH = (Button) findViewById(R.id.calibration_high);
        final Animation animation = AnimationUtils.loadAnimation(Settings.this, R.anim.shake);
        final Animation animation2 = AnimationUtils.loadAnimation(Settings.this, R.anim.shake_big);
        final DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.UP);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.drawable.dice).into(imageViewTarget);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/FontdinerSwanky.ttf");
        textView.setTypeface(custom_font);
        button.setTypeface(custom_font);
        textMode.setTypeface(custom_font);
        subtext.setTypeface(custom_font);
        textRound.setTypeface(custom_font);
        circleSize.setTypeface(custom_font);
        circleInput.setTypeface(custom_font);
        calibration.setTypeface(custom_font);
        calibrationL.setTypeface(custom_font);
        calibrationH.setTypeface(custom_font);


        final Bundle extras = getIntent().getExtras();
        round = extras.getInt("ROUND", 3);
        size = extras.getInt("SIZE", 75);
        hardMode = extras.getBoolean("HARD_MODE");
        devicePressure = extras.getInt("DEVICE_PRESSURE", 2);
        pressureLow = extras.getFloat("PRESSURE_LOW", 0);
        pressureHigh = extras.getFloat("PRESSURE_HIGH", 0);

        //initialize view
        setMusicState();

        checkBox.setChecked(hardMode);
        seekBar.setProgress(round - 3);
        seekbar_size.setProgress(size - 75);
        circleInput.setText(Integer.toString(size));
        textView.setText(Integer.toString(round));
        showCalibration();
        switch (devicePressure) {
            case 0: subtext.setText(R.string.not_available); break;
            case 1: subtext.setText(R.string.available); break;
            default: break;
        }

        textMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (checkBox.isChecked())
                    checkBox.setChecked(false);
                else
                    checkBox.setChecked(true);
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) hardMode = true;
                    else hardMode = false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 3;
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i + 3;
                textView.setText(Integer.toString(progress));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(Integer.toString(progress));
                round = progress;
            }
        });

        seekbar_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 75;
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i + 75;
                circleInput.setText(Integer.toString(progress));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                circleInput.setText(Integer.toString(progress));
                size = progress;
            }
        });

        calibration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                calibration.setVisibility(View.GONE);
                calibrationL.setVisibility(View.VISIBLE);
            }
        });

        calibrationL.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                int pointerIndex = motionEvent.getActionIndex();
                if (action == MotionEvent.ACTION_DOWN) {
                    calibrationL.startAnimation(animation);
                    min = motionEvent.getPressure(pointerIndex);
                    if (pressureLow == 0)
                        pressureLow = min;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (min < pressureLow)
                        pressureLow = min;
                    broadcast("Set " + df.format(pressureLow) + " as min");
                    calibrationL.setVisibility(View.GONE);
                    calibrationH.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        calibrationH.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                int pointerIndex = motionEvent.getActionIndex();
                if (action == MotionEvent.ACTION_DOWN) {
                    calibrationH.startAnimation(animation2);
                    max = motionEvent.getPressure(pointerIndex);
                    if (pressureHigh == 0)
                        pressureHigh = max;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (max > pressureHigh)
                        pressureHigh = max;
                    broadcast("Set " + df.format(pressureHigh) + " as max");
                    calibrationH.setVisibility(View.GONE);
                    showCalibration();
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Launcher.mediaPlayer.start();
                returnData();
            }
        });
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
        String toast = "Rounds: " + Integer.toString(round) + ", Size: " + Integer.toString(size) + ", Pressure Mode: ";
        if (hardMode)
            toast += "On";
        else
            toast += "Off";
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showCalibration() {
        if (pressureHigh != 0) {
            calibration.setVisibility(View.VISIBLE);
            calibration.setText(R.string.recalibrate);
        } else if (devicePressure == 0) {
            calibration.setVisibility(View.GONE);
        }
    }

    private void broadcast(String string) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.settings), string, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else
            Toast.makeText(Settings.this, string, Toast.LENGTH_SHORT).show();
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

    //back button pressed on the bezel
    public void onBackPressed() {
        returnData();
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
