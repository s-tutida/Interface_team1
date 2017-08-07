package com.example.team1.interface_team1;

        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;


public class SensorActivity extends AppCompatActivity implements SensorEventListener,StepListener{

    //センサーの変数宣言
    private SensorManager mSensorManager;
    private SimpleStepDetector simpleStepDetector;
    private Sensor accel,magitic;
    float[] magneticValues  = null;

    //歩数の変数宣言
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final String GAME_NUM_STEPS = "Your record: ";
    private int numSteps;
    private TextView hosu;//歩数用のテキストビュー

    //ゲーム画面の変数宣言
    private ImageView game;

    //開始ボタンの変数宣言
    private  ImageButton button;

    //画面遷移モード
    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //歩数テキストビューオブジェクトの取得
        hosu = (TextView) findViewById(R.id.hosu);

        //Game画面(お皿)オブジェクトの取得
        game = (ImageView) findViewById(R.id.imageView);

        //戻るボタン、開始ボタンの取得
        button = (ImageButton) findViewById(R.id.imageButton);

        //歩数計算のクラスを用意
        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(this);

        //センサー・マネージャーを取得する
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //センサー・マネージャーを使用してセンサーを取得
        accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magitic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //イメージボタンを設定
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode == 1){//START buttonが押された時
                    button.setVisibility(View.INVISIBLE);
                    mode = 2;
                    numSteps = 0;
                }else if(mode == 3){//終了ボタンが押された時
                    button.setVisibility(View.INVISIBLE);
                    mode = 0;
                }
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        numSteps = 0;//再開時は歩数をゼロにセット
        mode = 0;

        //センサーのリスナー登録
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magitic, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onStart() {

        super.onStart();
        this.mode = 0;

    }

    //アプリ終了時
    protected void onStop(){

        super.onStop();
        mSensorManager.unregisterListener( this );

    }


    // 回転行列
    float[]    I= new float[9];
    float[] accelerometerValues = new float[3];

    //センサーの値が変更された時に呼び出される
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(this.mode ==0 || this.mode == 1 || this.mode == 2) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);//歩数の更新処理
                    accelerometerValues = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magneticValues = event.values.clone();
                    break;
            }

            if (magneticValues != null && accelerometerValues != null) {

                //-------  傾きの計算 ---------
                float[] inR = new float[9];
                float[] I = new float[9];
                SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);//Gyroセンサーと加速どセンサーの値を行列に出力
                float[] outR = new float[9];
                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);//座標系に変換
                float[] orientationValues = new float[3];
                SensorManager.getOrientation(outR, orientationValues);//値を得る
                //------ 傾きの計算終了 ------　x:方位角,y:前後の傾斜,z:左右の傾斜

                //傾きから現在の状況を取得
                Calc calc = new Calc(radianToDegree(orientationValues[0]), radianToDegree(orientationValues[1]), radianToDegree(orientationValues[2]));//計算クラスを呼び出す
                int direction = calc.getDirection();//ここで,溢れる方向を得る
                int amount = calc.getAmount();//ここで,溢れる量を得る

                //傾きから開始ボタンのセット
                if (amount < 1  && mode == 0 ){
                    mode = 1;
                    //ボタンの設定
                    int resId = getResources().getIdentifier("start", "drawable", this.getPackageName());
                    button.setImageResource(resId);
                    button.setVisibility(View.VISIBLE);
                }else if(amount > 1 && mode == 1){
                    mode = 0;
                    //ボタンの設定
                    button.setVisibility(View.INVISIBLE);
                }

                //画像のセット
                if (amount == 4 && mode == 2) {
                    int resId = getResources().getIdentifier("gameover", "drawable", this.getPackageName());
                    game.setImageResource(resId);
                    reset();
                } else if (amount == 4 && mode != 2){
                    int number = direction * 10 + (amount-1);
                    int resId = getResources().getIdentifier("d" + number, "drawable", this.getPackageName());
                    game.setImageResource(resId);
                }else{
                    int number = direction * 10 + amount;
                    int resId = getResources().getIdentifier("d" + number, "drawable", this.getPackageName());
                    game.setImageResource(resId);

                    if(mode == 2) {
                        //歩数のセット
                        String r = TEXT_NUM_STEPS + numSteps + "\n";
                        hosu.setGravity(Gravity.RIGHT);
                        hosu.setText(r);
                    }else{//game中以外
                        //歩数のセット
                        String r = "水平にしてください";
                        hosu.setGravity(Gravity.CENTER);
                        hosu.setText(r);
                    }
                }
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    //ラジアンから角度に変換
    int radianToDegree(float rad){
        return (int) Math.floor( Math.toDegrees(rad));
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
    }

    public void reset(){

        //モード設定
        this.mode = 3;

        //ボタンの設定
        int resId = getResources().getIdentifier("restart", "drawable", this.getPackageName());
        button.setImageResource(resId);
        button.setVisibility(View.VISIBLE);

        //ゲーム終了画面の設定
        String r = GAME_NUM_STEPS + numSteps + "\n";
        hosu.setGravity(Gravity.CENTER);
        hosu.setText(r);

        //歩数のリセット
        this.numSteps = 0;

    }

}
