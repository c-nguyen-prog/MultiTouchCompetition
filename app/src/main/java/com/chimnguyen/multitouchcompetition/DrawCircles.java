package com.chimnguyen.multitouchcompetition;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class DrawCircles extends View {

    public static int RADIUS = 75;
    public int number = 1; //number of circles in a round
    public int state[] = new int[5]; //saves color of each circle
    public float x[] = {0, 0, 0, 0, 0};
    public float y[] = {0, 0, 0, 0, 0};
    public static double average[] = {0, 0};

    private static double tempTotal[] = {0, 0};
    private Game game;
    private Paint paint;
    private int width = game.width; //getContext().getResources().getDisplayMetrics().widthPixels;
    private int height = game.height; //getContext().getResources().getDisplayMetrics().heightPixels;
    private int color[] = {Color.GREEN, Color.YELLOW, Color.RED};
    private int i = 0;
    private int random;
    private boolean[] newCirclesOK = new boolean[5];
    private double distance[] = new double[10];
    private double min[] = new double[10];

    public DrawCircles(Context context) {
        super(context);
        game = (Game) context;
        paint = new Paint();
        if (game.size != 75)
            RADIUS = game.size;
        for (int i = 0; i < average.length; i++)
            average[i] = 0;
    }

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final DecimalFormat df = new DecimalFormat("#.#");
        final DecimalFormat dp = new DecimalFormat("#.##");
        dp.setRoundingMode(RoundingMode.UP);
        df.setRoundingMode(RoundingMode.UP);
        for (int l = 1; l < newCirclesOK.length; l++)
            newCirclesOK[l] = false;

        //changes the number of circles for each round
        if (!game.single) {
            if (i % 6 == 0)
                number = new Random().nextInt(4) + 2;
        } else {
            if (i % 3 == 0) {
                if (number < 5)
                    number++;
                else
                    number = new Random().nextInt(4) + 2; //amount of circles this round
            }
        }

        //initialize values and draw for each player's turn
        if (i % 6 == 0 || i % 6 == 3) {
            //reset new circles statuses to false
            for (int j = 0; j < number; j++) {
                newCirclesOK[j] = false;
            }

            for (int j = 0; j < number; j++) {
                if (game.hardMode == true) {
                    random = new Random().nextInt(3);
                    paint.setColor(color[random]); //set a random color
                    state[j] = random; // saves circle state (color)
                } else {
                    paint.setColor(Color.WHITE);
                }
                x[j] = RADIUS + (float) Math.random() * (width - RADIUS * 2);
                y[j] = RADIUS + (float) Math.random() * (height - RADIUS * 3);

                //checks so that circles aren't on top of each other
                if (j == 0) {
                    canvas.drawCircle(x[j], y[j], RADIUS, paint);
                } else {
                    while (!newCirclesOK[j]) {
                        for (int k = 0; k < j; k++) {
                            float range = (float) RADIUS * 2;
                            if (Math.abs(x[j] - x[k]) < range && Math.abs(y[j] - y[k]) < range) {
                                x[j] = RADIUS + (float) Math.random() * (width - RADIUS * 2);
                                y[j] = RADIUS + (float) Math.random() * (height - RADIUS * 2);
                                continue;
                            }
                            newCirclesOK[j] = true;
                        }
                    }
                    if (newCirclesOK[j])
                        canvas.drawCircle(x[j], y[j], RADIUS, paint);
                }
            }
        // hide circles before user's touch
        } else if (i % 6 == 1 || i % 6 == 4) {
            for (int j = 0; j < number; j++) {
                paint.setColor(Color.WHITE);
                canvas.drawCircle(x[j], y[j], RADIUS, paint);
            }
        // draw where the circles and user touches.
        } else if (i % 6 == 2 || i % 6 == 5) {
            for (int j = 0; j < number; j++) {
                if (game.hardMode) {
                    paint.setColor(color[state[j]]);
                } else {
                    paint.setColor(Color.WHITE);
                }
                canvas.drawCircle(x[j], y[j], RADIUS, paint);
            }

            for (int k = 0; k < game.touchCount; k++) {
                if (game.userX[k] != 0) {
                    paint.setTextSize(50);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(game.userX[k], game.userY[k], RADIUS, paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawText(".", game.userX[k] - 5, game.userY[k] + 5, paint);
                }
            }
            //draw circles borders
            for (int l = 0; l < number; l++) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                paint.setColor(Color.BLACK);
                canvas.drawCircle(x[l], y[l], RADIUS, paint);
            }

            for (int k = 0; k < game.touchCount; k++) { //draw user's touches
                if (game.userX[k] != 0) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.parseColor("#DF49D8"));
                    canvas.drawCircle(game.userX[k], game.userY[k], RADIUS, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.GRAY);
                    canvas.drawText("Tap to continue", 100, 150, paint);
                }
            }

            //calculate distance between finger and circle
            tempTotal[0] = 0;
            tempTotal[1] = 0;
            for (int l = 0; l < game.touchCount; l++)
                min[l] = 0;

            for (int k = 0; k < game.touchCount; k++) { //go through each touch
                for (int l = 0; l < number; l++) {
                    double a = Math.abs(game.userX[k] - x[l]);
                    double b = Math.abs(game.userY[k] - y[l]);
                    distance[l] = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)); //calculate distance between touch and every circle
                }
                min[k] = distance[0];
                for (int l = 0; l < number; l++) {
                    if (distance[l] < min[k])
                        min[k] = distance[l]; //shortest distance between touch and circle is chosen as match
                }
                canvas.drawText(df.format(min[k]), game.userX[k], game.userY[k], paint);
                if (game.devicePressure == 1 && game.hardMode)
                    canvas.drawText(dp.format(game.pressure[k]) + "P", game.userX[k], game.userY[k] - 35, paint);

                tempTotal[game.getPlayer() - 1] += min[k]; //total distance in this round
            }
            paint.setTextSize(70);

            //calculate average distance in this round (total dist/touch count)
            if (!game.single) {
                if (average[game.getPlayer() - 1] == 0)
                    average[game.getPlayer() - 1] = tempTotal[game.getPlayer() - 1] / game.touchCount;
                else
                    average[game.getPlayer() - 1] = (average[game.getPlayer() - 1] + (tempTotal[game.getPlayer() - 1] / game.touchCount)) / 2;
                canvas.drawText("Average distance: " + df.format(DrawCircles.average[game.getPlayer() - 1]), 50, height - 100, paint);
            } else {
                if (average[0] == 0)
                    average[0] = tempTotal[game.getPlayer() - 1] / game.touchCount;
                else
                    average[0] = (average[0] + (tempTotal[game.getPlayer() - 1] / game.touchCount)) / 2;
                canvas.drawText("Average distance: " + df.format(DrawCircles.average[0]), 50, height - 100, paint);
            }
        }
        i++;
    }
}