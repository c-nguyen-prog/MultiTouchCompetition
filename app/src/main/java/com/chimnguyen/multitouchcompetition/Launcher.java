package com.chimnguyen.multitouchcompetition;

import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import android.support.design.widget.Snackbar;

public class Launcher extends AppCompatActivity {

    public static MediaPlayer mediaPlayer, music;
    public static Typeface custom_font;
    public static boolean musicState;

    private int round = 3;
    private int size = 75;
    private boolean hardMode = false;
    private float pressure;
    private int devicePressure = 2;
    private boolean single = false;
    private float pressureLow, pressureHigh;
    private ImageButton musicOn, musicOff;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        final Intent i1 = new Intent(Launcher.this, Game.class);
        final Intent i2 = new Intent(Launcher.this, Settings.class);
        final Intent i3 = new Intent(Launcher.this, Instruction.class);
        final Bundle bundle = new Bundle();
        final Button startGame = (Button) findViewById(R.id.start_game);
        final Button singleGame = (Button) findViewById(R.id.single_game);
        Button settings = (Button) findViewById(R.id.settings);
        TextView welcome = (TextView)findViewById(R.id.welcome);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        ImageButton exclamation = (ImageButton) findViewById(R.id.exclamation);
        ImageButton question = (ImageButton) findViewById(R.id.question);
        musicOn = (ImageButton) findViewById(R.id.music);
        musicOff = (ImageButton) findViewById(R.id.no_music);
        final Animation animation = AnimationUtils.loadAnimation(Launcher.this, R.anim.shake);
        mediaPlayer = MediaPlayer.create(this, R.raw.button);
        music = MediaPlayer.create(this, R.raw.malathion_edited);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);

        changeMusicState(true);
        music.setLooping(true);
        Glide.with(this).load(R.drawable.dice).into(imageViewTarget);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/FontdinerSwanky.ttf");
        welcome.setTypeface(custom_font);
        startGame.setTypeface(custom_font);
        singleGame.setTypeface(custom_font);
        settings.setTypeface(custom_font);

        musicOn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                if (action == MotionEvent.ACTION_UP) {
                    changeMusicState(false);
                }
                return false;
            }
        });

        musicOff.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                if (action == MotionEvent.ACTION_UP) {
                    changeMusicState(true);
                }
                return false;
            }
        });

        startGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mediaPlayer.start();
                if (pressureHigh == 0 && hardMode) {
                    startGame.startAnimation(animation);
                    broadcast("Please calibrate the pressure limits");
                } else {
                    single = false;
                    bundle.putInt("ROUND", round);
                    bundle.putBoolean("HARD_MODE", hardMode);
                    bundle.putInt("DEVICE_PRESSURE", devicePressure);
                    bundle.putInt("SIZE", size);
                    bundle.putBoolean("SINGLE", single);
                    bundle.putFloat("PRESSURE_LOW", pressureLow);
                    bundle.putFloat("PRESSURE_HIGH", pressureHigh);
                    i1.putExtras(bundle);
                    startActivity(i1);
                }
            }
        });

        singleGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mediaPlayer.start();
                if (pressureHigh == 0 && hardMode) {
                    singleGame.startAnimation(animation);
                    broadcast("Please calibrate the pressure limits");
                } else {
                    single = true;
                    bundle.putInt("ROUND", round);
                    bundle.putInt("SIZE", size);
                    bundle.putBoolean("HARD_MODE", hardMode);
                    bundle.putInt("DEVICE_PRESSURE", devicePressure);
                    bundle.putBoolean("SINGLE", single);
                    bundle.putFloat("PRESSURE_LOW", pressureLow);
                    bundle.putFloat("PRESSURE_HIGH", pressureHigh);
                    i1.putExtras(bundle);
                    startActivity(i1);
                }
            }
        });

        settings.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //check if device has pressure sensor
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                int pointerIndex = motionEvent.getActionIndex();
                if (action == MotionEvent.ACTION_DOWN) {
                    mediaPlayer.start();
                    pressure = motionEvent.getPressure(pointerIndex);
                    if (pressure == 1.0)
                        devicePressure = 0;
                    else
                        devicePressure = 1;
                } else if (action == MotionEvent.ACTION_UP) {
                    bundle.putInt("ROUND", round);
                    bundle.putInt("SIZE", size);
                    bundle.putBoolean("HARD_MODE", hardMode);
                    bundle.putInt("DEVICE_PRESSURE", devicePressure);
                    bundle.putFloat("PRESSURE_LOW", pressureLow);
                    bundle.putFloat("PRESSURE_HIGH", pressureHigh);
                    i2.putExtras(bundle);
                    startActivityForResult(i2, 2);
                }
                return false;
            }
        });

        exclamation.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mediaPlayer.start();
                } else if (action == MotionEvent.ACTION_UP) {
                    showPopup(view);
                }
                return false;
            }
        });

        question.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mediaPlayer.start();
                } else if (action == MotionEvent.ACTION_UP) {
                    bundle.putInt("ROUND", round);
                    bundle.putInt("SIZE", size);
                    bundle.putBoolean("HARD_MODE", hardMode);
                    bundle.putInt("DEVICE_PRESSURE", devicePressure);
                    bundle.putFloat("PRESSURE_LOW", pressureLow);
                    bundle.putFloat("PRESSURE_HIGH", pressureHigh);
                    i3.putExtras(bundle);
                    startActivityForResult(i3, 2);
                }
                return false;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                round = data.getIntExtra("ROUND", 3);
                size = data.getIntExtra("SIZE", 75);
                hardMode = data.getBooleanExtra("HARD_MODE", false);
                pressureLow = data.getFloatExtra("PRESSURE_LOW", 0);
                pressureHigh = data.getFloatExtra("PRESSURE_HIGH", 0);
            }
        }
    }

    public void showPopup(View view) {
        View popupView = getLayoutInflater().inflate(R.layout.about_credits, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(view, Gravity.RELATIVE_LAYOUT_DIRECTION, 50, 200);
    }

    public void changeMusicState(boolean musicState) {
        if (musicState) {
            music.start();
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.GONE);
            this.musicState = true;
        } else {
            music.pause();
            musicOff.setVisibility(View.VISIBLE);
            musicOn.setVisibility(View.GONE);
            this.musicState = false;
        }
    }

    public void setMusicState() {
        if (musicState)
            changeMusicState(true);
        else
            changeMusicState(false);
    }

    public void broadcast(String string) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.launcher), string, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else
            Toast.makeText(Launcher.this, string, Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        setMusicState();
        super.onResume();
    }

    public void onPause() {
        music.pause();
        super.onPause();
    }
}