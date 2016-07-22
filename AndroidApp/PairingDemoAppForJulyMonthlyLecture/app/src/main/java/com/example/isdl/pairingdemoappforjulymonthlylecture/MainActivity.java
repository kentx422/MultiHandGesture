package com.example.isdl.pairingdemoappforjulymonthlylecture;

import android.hardware.SensorManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private Button button;
    private RelativeLayout relativeLayout;
    private TextView backNumber;
    private TextView receivedMessage;

    private String deviceName;
    private String udid;


    //IPアドレスの指定
    private final static String IP = "172.20.11.109";
    private final static int PORT = 8080;

    private Socket socket; //ソケット
    private InputStream in;     //入力ストリーム
    private OutputStream out;    //出力ストリーム
    private boolean error;  //エラー

    private final Handler handler = new Handler();//ハンドラ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタンを設定
        button = (Button) findViewById(R.id.buttonA);
        receivedMessage = (TextView) findViewById(R.id.textViewD);
        //relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutBackGround);
        backNumber = (TextView) findViewById(R.id.textViewE);

        // Android IDの取得
        udid = Settings.Secure.getString(this.getContentResolver(), Settings.System.ANDROID_ID);
        deviceName = getDeviceNameByUDID(udid);

        button.setOnClickListener(new clickListener());
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
    class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //onServe(macAddress);
            if (view == button) {
                onServe("test");
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
            socket = new Socket(ip, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            //addText("接続完了");
            //connectState.setText("接続完了");

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
                            receivedMessage.setText(finalStrBuf);
                            //様々な処理
                            someProcess(finalStrBuf);

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
                        String serveTime = getNowTime();
                        String write = serveTime +","+ udid +","+anser;
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
            onServe("see u");
            socket.close();
            socket = null;
        } catch (Exception e) {
        }
    }

    //--------------接続プログラム終わり

    public void someProcess(String message){

    }

    public String getDeviceNameByUDID(String udid) {
        if (udid.equals("a4c7b9190b6bd931")) {
            // backNumber.setText("A");
            return "nexus7-2012-hmurakami";
        }
        else if (udid.equals("8e9e784548c0cb6a")) {
            backNumber.setText("B");
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
}
