package com.chimnguyen.multitouchcompetition;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.*;
import android.view.*;

public class Game extends Activity {

    public static int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static int height = Resources.getSystem().getDisplayMetrics().heightPixels;

    public int touchCount = 0; //number of touches user performed
    public float userX[] = new float[10];
    public float userY[] = new float[10]; //array of 10 so user can't cheat
    public float pressure[] = new float[10]; //pressure of each touch
    public int roundTotal, devicePressure; //total rounds, device pressure sensor - 0 = no, 1 = yes, 2 = unknown.
    public boolean hardMode;
    public int size; //circle size
    public int point[] = {0,0}; // point[0] = player1, point[1] = player2
    public int counter = 1; //count rounds
    public boolean single; //single play game

    private DrawCircles drawCircles;
    private SplashScreen splashScreen;
    private WinningScreen winningScreen;
    private float pressureLow, pressureHigh;
    private boolean correctPressure;
    private int player;
    private boolean touch = false;
    private MediaPlayer winning;
    private int timeWait = 1500;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        //get the values from settings
        final Bundle extras = getIntent().getExtras();
        roundTotal = extras.getInt("ROUND", 3);
        size = extras.getInt("SIZE", 75);
        hardMode = extras.getBoolean("HARD_MODE");
        devicePressure = extras.getInt("DEVICE_PRESSURE", 2);
        single = extras.getBoolean("SINGLE", false);
        pressureLow = extras.getFloat("PRESSURE_LOW", 0);
        pressureHigh = extras.getFloat("PRESSURE_HIGH", 0);

        //construct
        splashScreen = new SplashScreen(this);
        drawCircles = new DrawCircles(this);
        winningScreen = new WinningScreen(this);
        winning = MediaPlayer.create(this, R.raw.winning);


        //initialize
        setMusicState();
        setContentView(splashScreen);
        splashScreen.postDelayed(new Runnable() {
            public void run() {
               runTime();
            }
        }, 1500);
    }

    private void runTime() {
        //reset user's saved touches
        touchCount = 0;
        for (int i = 0; i < touchCount + 1; i++) {
            userX[i] = 0;
            userY[i] = 0;
            pressure[i] = 0;
        }

        //show BLACK screen + dots
        setContentView(drawCircles);
        drawCircles.setBackgroundColor(Color.BLACK);
        drawCircles.invalidate();

        //wait and make WHITE screen, redraw WHITE dot
        drawCircles.postDelayed(new Runnable() {
            public void run() {
                drawCircles.setBackgroundColor(Color.WHITE);
                drawCircles.invalidate();
                touch = true;
            }
        }, 1000);

        //start listening to user's touches
        drawCircles.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;
                int pointerIndex = motionEvent.getActionIndex();

                if (touch) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN: { //one or more fingers touch
                            PointF f = new PointF();
                            f.x = motionEvent.getX(pointerIndex);
                            f.y = motionEvent.getY(pointerIndex);
                            if (devicePressure == 1) {
                                pressure[touchCount] = motionEvent.getPressure(pointerIndex);
                            }
                            userX[touchCount] = f.x; //saves user's touches
                            userY[touchCount] = f.y;
                            score(f.x, f.y);
                            touchCount++;
                            break;
                        }
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP: //when the touching is finished
                            touch = false;
                            counter++;
                            drawCircles.setBackgroundColor(Color.BLACK);
                            drawCircles.invalidate(); //show result
                            drawCircles.setOnTouchListener(new View.OnTouchListener() {
                                public boolean onTouch(View view, MotionEvent motionEvent) { //wait for tap before continuing
                                    setContentView(splashScreen); //create score screen/transition before next player's turn
                                    splashScreen.setBackgroundColor(Color.BLACK);
                                    if ((counter - 1) / 2 == roundTotal)
                                        timeWait = 5000;
                                    splashScreen.postDelayed(new Runnable() {
                                        public void run() {
                                            if (splashScreen.round <= roundTotal)
                                                runTime();
                                            else {
                                                setContentView(winningScreen);
                                                winning.start();
                                                winningScreen.setBackgroundColor(Color.BLACK);
                                                winningScreen.setOnTouchListener(new View.OnTouchListener() {
                                                    public boolean onTouch(View view, MotionEvent motionEvent) {
                                                        finish();
                                                        return false;
                                                    }
                                                });
                                            }
                                        }
                                    }, timeWait);
                                    return false;
                                }
                            });
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                        case MotionEvent.ACTION_CANCEL:
                    }
                }
                return true;
            }
        });
    }

    private void score(float touchX, float touchY) {
        getPlayer();
        //check if user's touches is within radius of the drawn circles
        for (int j = 0; j < drawCircles.number; j++) {
            if (hardMode && devicePressure == 1) {
                if ((Math.abs(touchX - drawCircles.x[j]) < drawCircles.RADIUS) && (Math.abs(touchY - drawCircles.y[j]) < drawCircles.RADIUS)) {
                    point[player - 1]++;
                    float oneThird = ((pressureLow - pressureHigh) / 3) + pressureLow;
                    float twoThird = ((pressureLow - pressureHigh) * 2 / 3) + pressureLow;
                    switch (drawCircles.state[j]) { //calculate pressure Min>|--low--|--mid--|--high--|<Max
                        case 0:
                            if (pressure[j] < oneThird || pressure[j] < pressureLow)
                                correctPressure = true;
                            break;
                        case 1:
                            if (pressure[j] >= oneThird && pressure[j] <= twoThird)
                                correctPressure = true;
                            break;
                        case 2:
                            if (pressure[j] > twoThird && pressure[j] < 1.0 || pressure[j] > pressureHigh && pressure[j] < 1.0)
                                correctPressure = true;
                            break;
                    }
                    if (correctPressure){
                        point[player - 1]++;
                        correctPressure = false;
                    }
                }
            } else {
                if ((Math.abs(touchX - drawCircles.x[j]) < drawCircles.RADIUS) && (Math.abs(touchY - drawCircles.y[j]) < drawCircles.RADIUS))
                    point[player - 1]++;
            }
        }
    }

    public int getPlayer() {
        if (counter % 2 == 0)
            player = 2; //check who
        else
            player = counter % 2;
        return player;
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

    public void onStop() {
        super.onStop();
        finish();
    }

    public void onPause() {
        Launcher.music.pause();
        super.onPause();
    }
}