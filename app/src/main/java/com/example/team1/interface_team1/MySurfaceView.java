package com.example.team1.interface_team1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by shinya on 2017/07/31.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private Bitmap dish;
    private int direction;
    private int amount;
    public SurfaceHolder mholder;

    public MySurfaceView(Context context,SurfaceView surfaceView,int direction,int amount) {
        super(context);

        this.mholder = surfaceView.getHolder();

        this.mholder.addCallback(this);

        this.direction = direction;//第何象限に傾いているか
        this.amount = amount;//どれくらい傾いているか
    }

    public void surfaceCreated(SurfaceHolder holder){

        mholder = holder;

        //画像の選択
        dish = BitmapFactory.decodeResource(getResources(),R.drawable.sara);

        //Canvas取得しロックする
        Canvas canvas = mholder.lockCanvas();

        //背景色指定
        canvas.drawBitmap(dish, new Rect(0, 0, dish.getWidth(), dish.getHeight()), new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);

        // LockしたCanvasを解放する
        holder.unlockCanvasAndPost(canvas);
    }
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
        mholder = holder;
    }

    public void surfaceDestroyed(SurfaceHolder holder){}
}
