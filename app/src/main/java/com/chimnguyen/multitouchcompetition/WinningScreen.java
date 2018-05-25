package com.chimnguyen.multitouchcompetition;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public class WinningScreen extends View {

    private Game game;
    private Paint paint;
    private int width = game.width;
    private int height = game.height;

    public WinningScreen(Context context) {
        super(context);
        game = (Game) context;
        paint = new Paint();
    }

    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(130);
        paint.setTypeface(Launcher.custom_font);

        canvas.drawText("Tap to continue", width / 2,  height - 150, paint);

        if (!game.single) {
            if (game.point[0] > game.point[1])
                canvas.drawText("P1 VICTORY!", width / 2, height / 2, paint);
            else if (game.point[1] > game.point[0])
                canvas.drawText("P2 VICTORY!", width / 2, height / 2, paint);
            else if (game.point[0] == game.point[1] && DrawCircles.average[0] < DrawCircles.average[1])
                canvas.drawText("P1 VICTORY!", width / 2, height / 2, paint);
            else
                canvas.drawText("P2 VICTORY!", width / 2, height / 2, paint);
        } else
            canvas.drawText("TRAINING DONE!", width / 2, height / 2, paint);
    }
}
