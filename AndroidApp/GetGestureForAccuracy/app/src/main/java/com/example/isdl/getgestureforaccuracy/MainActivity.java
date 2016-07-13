package com.example.isdl.getgestureforaccuracy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
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
    private EditText subjectNameGet;
    private TextView version;
    private TextView resolution;

    private int onoff;
    private String macAddress;
    private String deviceName;
    private String subjectName;
    private SensorManager manager;
    private String gesture;
    private String imei;
    private String udid;

    //version: "show version" >> "show resolution" >> "change UDID"
    private String versionNow = "change UDID 3.1";

    private int slashCount;
    private int upCount;
    private int downCount;
    private int rollCount;
    private int hideCount;

    ArrayList<String> illumiLog = new ArrayList< >();
    ArrayList<String> timeDataLog  = new ArrayList< >();
    ArrayList<String> nanotimeDataLog  = new ArrayList< >();

    //IPアドレスの指定
    private final static String IP = "172.20.11.184";
    private final static int PORT = 8080;

    private Socket socket; //ソケット
    private InputStream in;     //入力ストリーム
    private OutputStream out;    //出力ストリーム
    private boolean error;  //エラー

    private final Handler handler = new Handler();//ハンドラ

    private String resieveMessage;
    private String sendMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //macAddressの取得
//        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        macAddress = wifiInfo.getMacAddress();
//        deviceName = getDeviceNameByMacAddress(macAddress);

//        //IMEIの取得
//        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//        imei = tm.getDeviceId();

        // Android IDの取得
        udid = Settings.Secure.getString(this.getContentResolver(), Settings.System.ANDROID_ID);
        deviceName = getDeviceNameByUDID(udid);

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
        subjectNameGet = (EditText) findViewById(R.id.subjectName);
        version = (TextView) findViewById(R.id.version);
        version.setText("version: "+versionNow);
        resolution = (TextView) findViewById(R.id.resolution);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        onoffbutton.setOnClickListener(new clickListener());
        slash.setOnClickListener(new clickListenerGesture());
        up.setOnClickListener(new clickListenerGesture());
        down.setOnClickListener(new clickListenerGesture());
        roll.setOnClickListener(new clickListenerGesture());
        hide.setOnClickListener(new clickListenerGesture());
        delete.setOnClickListener(new clickListenerGesture());

    }

    @Override
    public void onStart() {
        super.onStart();

        //スレッドの生成
        Thread thread = new Thread() {
            public void run() {
                try {
                    connect(IP, PORT);
                } catch (Exception e) {
                }
            }
        };
        thread.start();
    }

    //アクティビティの停止時に呼ばれる
    @Override
    public void onStop() {
        super.onStop();
        disconnect();
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
                } else {
                    onoff = 0;
                    onoffbutton.setText("ON");
                    subjectName = subjectNameGet.getText().toString();
                    sendMessage = ""+ getNowTime()+","+deviceName+","+subjectName+","+udid+";";
                    //Log.d("sendMessage", sendMessage);
                    int sendLenge = 64;
                    for(int i = 0;i<timeDataLog.size();i++){
                        if(i%sendLenge==0){
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            onServe(sendMessage);
                            sendMessage ="";
                        }
                        if(timeDataLog.get(i).equals("delete")){
                            Log.d("delete","");
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            onServe(sendMessage);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendMessage ="delete";
                            onServe(sendMessage);
                            sendMessage ="";
                            break;
                        }
                        sendMessage += timeDataLog.get(i)+","+nanotimeDataLog.get(i)+","+illumiLog.get(i)+";";
                        //Log.d("sendMessage",sendMessage);
                    }
                    if(!sendMessage.equals("")){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        onServe(sendMessage);
                        sendMessage ="";
                    }

                    //reset log
                    illumiLog = new ArrayList<String>();
                    timeDataLog  = new ArrayList<String>();
                    nanotimeDataLog  = new ArrayList<String>();
                    slashCount = 0;
                    upCount = 0;
                    downCount = 0;
                    rollCount = 0;
                    hideCount = 0;
                    slash.setText("slash: " + slashCount);
                    up.setText("up: " + upCount);
                    down.setText("down: " + downCount);
                    roll.setText("roll: " + rollCount);
                    hide.setText("hide: " + hideCount);

                    gesture="";
                    state.setText(gesture);

                }
            }
        }
    }

    class clickListenerGesture implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (view == slash) {
                gesture = "slash";
                slashCount++;
                slash.setText("slash: "+slashCount);
            }
            else if(view == up){
                gesture = "up";
                upCount++;
                up.setText("up: " + upCount);
            }
            else if(view == down){
                gesture = "down";
                downCount++;
                down.setText("down: " + downCount);
            }
            else if(view == hide){
                gesture = "hide";
                hideCount++;
                hide.setText("hide: " + hideCount);
            }
            else if(view == roll){
                gesture = "roll";
                rollCount++;
                roll.setText("roll: "+rollCount);
            }

            else if(view == delete){
                //reset log
                gesture = "delete";
//                illumiLog = new ArrayList<String>();
//                timeDataLog  = new ArrayList<String>();
//                nanotimeDataLog  = new ArrayList<String>();
            }

            state.setText(gesture);
            illumiLog.add(gesture);
            timeDataLog.add(gesture);
            nanotimeDataLog.add(gesture);
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
            long millistime = 0;
            long nanotime = 0;

            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                return;
            }

            int type = event.sensor.getType();
            if (type == Sensor.TYPE_LIGHT) {
                lx = (event.values[0]);
                millistime = System.currentTimeMillis();
                nanotime = System.nanoTime();

                nowlx.setText(""+lx);
                if(nanotimeDataLog.size()!=0&&isLong(nanotimeDataLog.get(nanotimeDataLog.size()-1))) {
                    resolution.setText("" + (nanotime - Long.parseLong(nanotimeDataLog.get(nanotimeDataLog.size() - 1))) / 1000000);
                }
                illumiLog.add(String.valueOf(lx));
                timeDataLog.add(String.valueOf(millistime));
                nanotimeDataLog.add(String.valueOf(nanotime));
            }
        }
    }

    //接続
    private void connect(String ip, int port) {
        int size;
        String strBuf = "";
        byte[] w = new byte[1024];
        try {
            //ソケット接続
            //addText("接続中");
            //connectState.setText("接続中");
            //state.setText("connect now");
            socket = new Socket(ip, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            //addText("接続完了");
            //connectState.setText("接続完了");
            //state.setText("connect end");

            while (socket != null && socket.isConnected()) {
                //データの受信
                size = in.read(w);
                if (size <= 0) continue;
                strBuf = new String(w, 0, size, "UTF-8");
                //strRecive = str;
                final String finalStrBuf = strBuf;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!error) {
                            resieveMessage = finalStrBuf;
                            //様々な処理
                            //someProcess(finalStrBuf);

                        } else {
                            //addText("通信失敗しました");
                            //connectState.setText("通信失敗しました2");
                        }
                    }
                });
                //ラベルへの文字列追加
                //connectState.setText("受信");
                //receive.setText(""+str);
            }
        } catch (Exception e) {
            //addText("通信失敗しました");
            //connectState.setText("通信失敗しました\n"+e);
            state.setText("connect fault");
            Log.e("error", String.valueOf(e));
        }
    }

    public void onServe(final String anser) {
        //スレッッドの生成
        Thread thread = new Thread(new Runnable() {
            public void run() {
                error = false;
                try {
                    //データの送信
                    if (socket != null && socket.isConnected()) {
                        //String serveTime = getNowTime();
                        //String write = serveTime +","+ deviceName+";"+anser;
                        String write = anser;
                        byte[] w = write.getBytes("UTF8");
                        out.write(w);
                        out.flush();
                    }
                } catch (Exception e) {
                    error = true;
                }
                //ハンドラの生成
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!error) {
                        } else {
                            //addText("通信失敗しました");
                            //connectState.setText("通信失敗しました2");
                        }
                    }
                });
            }
        });
        thread.start();
    }

    //切断
    private void disconnect() {
        try {
            //onServe("see u");
            socket.close();
            socket = null;
        } catch (Exception e) {
        }
    }

    //--------------接続プログラム終わり

    //recognize device using macAddress
    public static String getDeviceNameByMacAddress(String macAddress) {
        if (macAddress.equals("30:85:a9:2f:00:af")) {
            return "nexus7-2012-hmurakami";
        } else if (macAddress.equals("ac:22:0b:5c:8c:0c")) {
            return "nexus7-2013-haida";
        } else if (macAddress.equals("02:00:00:00:00:00")) {
            return "nexus7-2013-amiyoshi";
        }
        else if (macAddress.equals("FC:C2:DE:BB:1A:62")) {
            return "Galaxy-S5-atonomura";
        }
        else {
            return "unknown";
        }
    }

    public static String getDeviceNameByUDID(String udid) {
        if (udid.equals("a4c7b9190b6bd931")) {
            return "nexus7-2012-hmurakami";
        } else if (udid.equals("8e9e784548c0cb6a")) {
            return "nexus7-2013-haida";
        } else if (udid.equals("f7196b5116fe5f4d")) {
            return "nexus7-2013-amiyoshi";
        }
        else if (udid.equals("a63f8c393f29b971")) {
            return "Galaxy-S5-atonomura";
        }
        else if (udid.equals("7b2f5bfd497b875f")) {
            return "Xperia-Z5-tyamamoto";
        }
        else {
            return "unknown";
        }
    }


    public static String getNowTime() {
        // 時刻取得
        Calendar calendar = Calendar.getInstance();
        String nowTime = "" + calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) + 1 < 10) {
            nowTime += "0" + (calendar.get(Calendar.MONTH) + 1);
        } else {
            nowTime += "" + (calendar.get(Calendar.MONTH) + 1);
        }
        if (calendar.get(Calendar.DAY_OF_MONTH) + 1 < 10) {
            nowTime += "0" + calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            nowTime += "" + calendar.get(Calendar.DAY_OF_MONTH);
        }

        //nowTime+="_";

        if (calendar.get(Calendar.HOUR_OF_DAY) + 1 < 10) {
            nowTime += "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            nowTime += "" + calendar.get(Calendar.HOUR_OF_DAY);
        }
        if (calendar.get(Calendar.MINUTE) + 1 < 10) {
            nowTime += "0" + calendar.get(Calendar.MINUTE);
        } else {
            nowTime += "" + calendar.get(Calendar.MINUTE);
        }
        if (calendar.get(Calendar.SECOND) + 1 < 10) {
            nowTime += "0" + calendar.get(Calendar.SECOND);
        } else {
            nowTime += "" + calendar.get(Calendar.SECOND);
        }
        return nowTime;
    }

    //Longかどうかを確かめる
    static boolean isLong(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
