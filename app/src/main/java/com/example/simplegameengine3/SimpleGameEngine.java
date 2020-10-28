package com.example.simplegameengine3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleGameEngine extends Activity {
    GameView gameView;
    int screenX;
    int screenY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        gameView = new GameView(this);
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;
        setContentView(gameView);
    }

     class GameView extends SurfaceView implements Runnable {
         Thread gameThread = null;

         SurfaceHolder ourHolder;
         volatile boolean playing;
         Canvas canvas;
         Paint paint;
         long fps;
         private long timeThisFrame;
         Bitmap bitmapMario;
         boolean isMoving = false;
         float walkSpeedPerSecond = 150;
         float marioXPosition =10;
         float bobXPosition = 10;
         float walkSpeedPerSeconds = 150;

         public GameView (Context context){
             super(context);
             ourHolder = getHolder();
             paint = new Paint();
             bitmapMario = BitmapFactory.decodeResource(this.getResources(), R.drawable.mario);
//            Log.d('test',bitmapMario);
            playing = true;
         }
         @Override
         public void run() {
             while(playing){
                 long startFrameTime = System.currentTimeMillis();

                 update();
                 draw();
                 timeThisFrame = System.currentTimeMillis() - startFrameTime;
                 if(timeThisFrame > 0){
                     fps = 1000/timeThisFrame;
                 }
             }
         }

         private void update() {
             if(isMoving){
                 if (bobXPosition > screenX-100 || bobXPosition < 0) {
                     walkSpeedPerSeconds = -walkSpeedPerSeconds;
                 }
                 marioXPosition = marioXPosition + (walkSpeedPerSecond/fps);
             }
         }
         public void draw(){

             if (ourHolder.getSurface().isValid()){
                 canvas = ourHolder.lockCanvas();
                 canvas.drawColor(Color.argb(255,26,128,182));

                 paint.setColor(Color.argb(255,249,129,0));

                 paint.setTextSize(45);

                 canvas.drawText("Fps: "+fps,20,40, paint);
                 canvas.drawText("Screen X: " + screenX, 20, 75, paint);
                 canvas.drawText("Character Position: " + bobXPosition, 20, 115, paint);

                 canvas.drawBitmap(bitmapMario, 10,10, paint);
//                 surface.release();
                 ourHolder.unlockCanvasAndPost(canvas);
             }
         }

         public void pause() {
             playing = false;
             try {
                 gameThread.join();
             } catch (InterruptedException e) {
                 Log.e("Eror:","joining thread");
             }
         }
         public void resume(){
             playing = true;
             gameThread = new Thread(this);
             gameThread.start();
         }

         @Override
         public boolean onTouchEvent(MotionEvent motionEvent) {
             switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                 case MotionEvent.ACTION_DOWN:
                     isMoving = true;
                     break;

                 case MotionEvent.ACTION_UP:
                     isMoving = false;
                     break;
             }
             return true;
         }

     }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}