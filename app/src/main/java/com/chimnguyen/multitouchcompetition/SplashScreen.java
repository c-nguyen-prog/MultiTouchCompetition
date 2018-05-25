package com.chimnguyen.multitouchcompetition;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.*;
import android.view.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class SplashScreen extends View {

    public Game game;
    public Paint paint;
    public int round;

    private int width =  game.width;
    private int height =  game.height;

    public SplashScreen(Context context) {
        super(context);
        game = (Game) context;
        paint = new Paint();
    }

    protected void onDraw(final Canvas canvas) {
        final DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.UP);
        round = (game.counter + 1) / 2;
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(130);
        paint.setTypeface(Launcher.custom_font);

        if (!game.single) { //Multi player game
            game.getPlayer();
            if (round <= game.roundTotal) {
                //transition screen
                canvas.drawText("Player " + Integer.toString(game.getPlayer()) + ":   " + Integer.toString(game.point[game.getPlayer() - 1]), width / 2, height / 3, paint);
                canvas.drawText("Round " + Integer.toString(round) + " of " + Integer.toString(game.roundTotal), width / 2, 2* height / 3, paint);
            } else {
                //end game score screen
                canvas.drawText("P1: " + Integer.toString(game.point[0]), width / 2, height / 3, paint);
                canvas.drawText("Average: " + df.format(DrawCircles.average[0]), width / 2, height / 3 + 200, paint);
                canvas.drawText("P2: " + Integer.toString(game.point[1]), width / 2, 2 * height / 3, paint);
                canvas.drawText("Average: " + df.format(DrawCircles.average[1]), width / 2, 2 * height / 3 + 200, paint);
            }
        } else { //Single player game
            if (round <= game.roundTotal) {
                canvas.drawText("Round " + Integer.toString(game.counter), width / 2, height / 3, paint);
                canvas.drawText("Ready!", width / 2, 2 * height / 3, paint);
            } else {
                canvas.drawText("Game Over!", width / 2, height / 3, paint);
                canvas.drawText("Average: " + df.format(DrawCircles.average[0]), width / 2, 2 * height / 3, paint);
            }
        }
    }
}