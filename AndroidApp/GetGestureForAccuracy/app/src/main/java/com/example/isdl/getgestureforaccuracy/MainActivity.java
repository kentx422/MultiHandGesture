package com.example.isdl.getgestureforaccuracy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView state;
    private TextView nowlx;
    private Button onoffbutton;
    private Button slash;
    private Button up;
    private Button down;
    private Button roll;
    private Button hide;
    private Button delete;

    private int onoff;
    private String macAddress;
    private String deviceName;
    private SensorManager manager;
    private String gesture;

    ArrayList<Double> illumiLog = new ArrayList<Double>();
    ArrayList<Long> timeDataLog  = new ArrayList<Long>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();
        deviceName = getDeviceName(macAddress);

        //ボタンとテキストの設定
        state = (TextView) findViewById(R.id.state);
        nowlx = (TextView) findViewById(R.id.lx);
        onoffbutton = (Button) findViewById(R.id.onoff);
        slash = (Button) findViewById(R.id.slash);
        up = (Button) findViewById(R.id.up);
        down = (Button) findViewById(R.id.down);
        roll = (Button) findViewById(R.id.roll);
        hide = (Button) findViewById(R.id.hide);
        delete = (Button) findViewById(R.id.delete);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        onoffbutton.setOnClickListener(new clickListener());
        slash.setOnClickListener(new clickListenerGesture());
        up.setOnClickListener(new clickListenerGesture());
        down.setOnClickListener(new clickListenerGesture());
        roll.setOnClickListener(new clickListenerGesture());
        hide.setOnClickListener(new clickListenerGesture());
        delete.setOnClickListener(new clickListenerGesture());

        gesture = "slash";
    }

    public void onResume() {
        super.onResume();
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_LIGHT);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view == onoffbutton) {
                if (onoff == 0) {
                    onoff = 1;
                    onoffbutton.setText("OFF");
                    state.setText(gesture);
                    //reset log
                    illumiLog = new ArrayList<Double>();
                    timeDataLog  = new ArrayList<Long>();
                } else {
                    onoff = 0;
                    onoffbutton.setText("ON");

                }
            }
        }
    }

    class clickListenerGesture implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (view == slash) {
                gesture = "slash";
            }
            else if(view == up){
                gesture = "up";
            }
            else if(view == down){
                gesture = "down";
            }
            else if(view == hide){
                gesture = "hide";
            }
            else if(view == roll){
                gesture = "roll";
            }
            else if(view == delete){
                //reset log
                illumiLog = new ArrayList<Double>();
                timeDataLog  = new ArrayList<Long>();
            }
            state.setText(gesture);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // センサーの精度が変更されると呼ばれる
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (onoff == 1) {

            double lx = 0;
            long nanotime = 0;

            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                return;
            }

            int type = event.sensor.getType();
            if (type == Sensor.TYPE_LIGHT) {
                lx = (event.values[0]);
                nanotime = System.nanoTime();
                nowlx.setText(""+lx);

                illumiLog.add(lx);
                timeDataLog.add(nanotime);
            }
        }
    }
    //recognize device using macAddress
    public static String getDeviceName(String macAddress) {
        if (macAddress.equals("30:85:a9:2f:00:af")) {
            return "nexus7_2012";
        } else if (macAddress.equals("ac:22:0b:5c:8c:0c")) {
            return "nexus7_2013_haida";
        } else if (macAddress.equals("02:00:00:00:00:00")) {
            return "nexus7_2013_amiyoshi";
        } else {
            return "unknown";
        }
    }
}
