package com.example.team1.interface_team1;

        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.TextView;
        import java.util.List;
        import android.view.SurfaceView;


public class SensorActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private TextView values;
    private SurfaceView surfaceView;

    // センサーの値
    float[] magneticValues  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //テキスト
        values = (TextView) findViewById(R.id.text_view);

//        //ゲーム画面
//        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        //センサー・マネージャーを取得する
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }


    //アプリが起動する時
    @Override
    protected void onStart() {

        super.onResume();

        //全センサーの取得
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        //センサマネージャーへリスナーを登録
        for (Sensor sensor : sensors) {

            //Gyroの登録
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }

            //加速器の登録
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }

            if(sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                mSensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
            }

            if(sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                mSensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
            }


        }
    }

    //アプリ終了時
    protected void onStop(){

        super.onPause();

        mSensorManager.unregisterListener( this );

    }


    // 回転行列
    float[]    I= new float[9];
    float[] accelerometerValues = new float[3];
    float step[];

    //センサーの値が変更された時に呼び出される
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                break;
            case Sensor.TYPE_STEP_COUNTER:
                step = event.values.clone();
                break;
        }

        if (magneticValues != null && accelerometerValues != null) {

            float[]  inR = new float[9];
            float[]    I= new float[9];
            SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);//Gyroセンサーと加速どセンサーの値を行列に出力

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);//座標系に変換

            float[] orientationValues   = new float[3];
            SensorManager.getOrientation(outR, orientationValues);//値を得る

            /** debug用
             * x:方位角
             * y:前後の傾斜
             * z:左右の傾斜
             **/
            //計算クラスを呼び出す
            Calc calc = new Calc(radianToDegree(orientationValues[0]),radianToDegree(orientationValues[1]),radianToDegree(orientationValues[2]));
            //ここで,溢れる方向を得る
            int direction = calc.getDirection();
            //ここで,溢れる量を得る
            int amount = calc.getAmount();



            //ここで画像をはる処理を行う
//            MySurfaceView canvas = new MySurfaceView(this,surfaceView,direction,amount);
//            canvas.surfaceCreated(canvas.mholder);

            String r =      "--------------------------------\n" +
                    "3-axis Magnetic field sensor\n"+
                    "--------------------------------\n" +
                    "x-axis :" + radianToDegree(orientationValues[0]) + "\n" +
                    "y-axis :" + radianToDegree(orientationValues[1]) + "\n" +
                    "z-axis :" + radianToDegree(orientationValues[2]) + "\n" +
                    "direction :" + direction + "\n" +
                    "amount :" + amount + "\n" ;

            values.setText(r);

        } else if(step != null){

            String r = "hosuu  :" + step[0] + "\n";
            values.setText(r);

        }else{

            String r = "error";
            values.setText(r);
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    //ラジアンから角度に変換
    int radianToDegree(float rad){
        return (int) Math.floor( Math.toDegrees(rad));
    }

}
