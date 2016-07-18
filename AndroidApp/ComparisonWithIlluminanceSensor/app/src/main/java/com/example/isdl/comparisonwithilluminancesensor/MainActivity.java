package com.example.isdl.comparisonwithilluminancesensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView state;
    private TextView nowlx;

    private SensorManager manager;
    private String deviceName;
    private String udid;
    private double lx;
    private int onoff = 0;

    //IPアドレスの指定
    private final static String IP = "172.20.11.109";
    private final static int PORT = 8080;

    private Socket socket; //ソケット
    private InputStream in;      //入力ストリーム
    private OutputStream out;    //出力ストリーム
    private boolean error;  //エラー

    private final Handler handler = new Handler();//ハンドラ

    private String recieveMessage;
    private String sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Android IDの取得
        udid = Settings.Secure.getString(this.getContentResolver(), Settings.System.ANDROID_ID);
        deviceName = getDeviceNameByUDID(udid);

        //ボタンとテキストの設定
        state = (TextView) findViewById(R.id.state);
        nowlx = (TextView) findViewById(R.id.lx);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    public void sendInterval(double interval){

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // センサーの精度が変更されると呼ばれる
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(onoff==1) {
            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                return;
            }
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_LIGHT) {
                lx = (event.values[0]);
                nowlx.setText("" + lx);
                sendMessage = deviceName+","+System.currentTimeMillis() +","+lx+";";
                onServe(sendMessage);
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
                //データの送信
                size = in.read(w);
                if (size <= 0) continue;
                strBuf = new String(w, 0, size, "UTF-8");
                //strRecive = str;
                final String finalStrBuf = strBuf;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!error) {
                            recieveMessage = finalStrBuf;
                            if(recieveMessage.equals("on")){
                                onoff=1;
                            }
                            else if(recieveMessage.equals("off")){
                                onoff=0;
                            }
                            //様々な処理
                            //someProcess(finalStrBuf);

                        } else {
                            //addText("通信失敗しました");
                            //connectState.setText("通信失敗しました");
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
        //スレッドの生成
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
                            //connectState.setText("通信失敗しました");
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
    public static String getDeviceNameByUDID(String udid) {
        if (udid.equals("a4c7b9190b6bd931")) {
            return "nexus7-2012-hmurakami";
        }
        else if (udid.equals("8e9e784548c0cb6a")) {
            return "nexus7-2013-haida";
        }
        else if (udid.equals("f7196b5116fe5f4d")) {
            return "nexus7-2013-amiyoshi";
        }
        else if (udid.equals("a63f8c393f29b971")) {
            return "Galaxy-S5-atonomura";
        }
        else if (udid.equals("7b2f5bfd497b875f")) {
            return "Xperia-Z5-tyamamoto";
        }
        else if (udid.equals("6834af3a92999f3b")) {
            return "Galaxy-S6edge-dyamashita";
        }
        else if (udid.equals("b58cf0a0466b2ace")) {
            return "Xperia-Z3-smorimura";
        }
        else {
            return "unknown";
        }
    }
}
