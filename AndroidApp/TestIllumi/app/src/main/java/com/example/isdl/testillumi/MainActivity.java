package com.example.isdl.testillumi;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {
    //    private Button btn;
//    private TextView result;
//    private TextView data;
    private SensorManager manager;

    int onoff = 0;
    private TextView illumi;
    private TextView result;
    private TextView timeStamp;
    private TextView receivedMessage;
    private Button button;
    private ImageView testImage;

    private boolean flag = true;
    private String illumiAndTimeData = "";
    private String macAddress;
    private int imageFFRK   = R.drawable.ffrk;
    private int imageLive2D = R.drawable.katoroku;
    private int imageMARIO  = R.drawable.mario;
    private int imageSazae  = R.drawable.sazaesan;
    private int imageID = 0;
    private int[] imageList = {imageFFRK,imageLive2D,imageMARIO,imageSazae};

    int BILL[] = new int[10];
    int first = 1;
    int check = 0;
    long time[] = new long[10];
    long start = 0;
    long end = 0;
    String str;
    int num = 0;

    private final static String BR = System.getProperty("line.separator");
    //IPアドレスの指定
    private final static String IP = "172.20.11.191";
    private final static int PORT = 8081;

    private TextView lblReceive;//受信ラベル
    private EditText edtSend;   //送信エディットテキスト
    private Button btnSend;   //送信ボタン

    private Socket socket; //ソケット
    private InputStream in;     //入力ストリーム
    private OutputStream out;    //出力ストリーム
    private boolean error;  //エラー

    private final Handler handler = new Handler();//ハンドラ

    private String messageForSending = "";
    private String messageForReciving = "";
    String startTimeStamp = "";

    double calibrationDevice = 1.1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--
//        LinearLayout ll = new LinearLayout(this);
//        ll.setOrientation(LinearLayout.VERTICAL);
//        setContentView(ll);

//        result = new TextView(this);
//        result.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        result.setGravity(Gravity.CENTER);
//        result.setTextSize(100);
//        //result.setText("HGI/LI");
//
//        data = new TextView(this);
//        data.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        data.setGravity(Gravity.CENTER);
//        data.setTextSize(50);


//        btn = new Button(this);
//        btn.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        btn.setText("ON");
//
//        btn.setEnabled(true);
//
//        result.setText("wait...");
//        data.setText("no data");
//        ll.addView(btn);
//        ll.addView(result);
//        ll.addView(data);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();

        // ボタンを設定
        button = (Button) findViewById(R.id.buttonA);
        illumi = (TextView) findViewById(R.id.textViewA);
        result = (TextView) findViewById(R.id.textViewB);
        timeStamp = (TextView) findViewById(R.id.textViewC);
        receivedMessage = (TextView) findViewById(R.id.textViewD);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        testImage = (ImageView) findViewById(R.id.imageViewA);


        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        btn.setOnClickListener(new clickListener());

        button.setOnClickListener(new clickListener());

        setCalibrationByMacAdress();

        testImage.setOnClickListener(new imageClickListener());

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

            //受信ループ
            /*if(socket.isConnected()){
                connectState.setText("受信できそう");
            }
            else{
                connectState.setText("受信できなさそう");
            }*/
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
//                            connectState.setText("***"+macAddress+"<=>"+strSplit[0]+"***");
//                            if(macAddress.equals(strSplit[0])){
//                                device[0].setText("macAddress\t: "+strSplit[0]+"\nLastType\t: "+strSplit[1]+"\nDoneTime\t: "+strSplit[2]);
//                            }
//                            else{
//                                device[1].setText("macAddress\t: "+strSplit[0]+"\nLastType\t: "+strSplit[1]+"\nDoneTime\t: "+strSplit[2]);
//                            }

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
                        //String write = serveTime +","+ macAddress +","+anser;
                        byte[] w = anser.getBytes("UTF8");
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

    public void onResume() {
        super.onResume();
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_LIGHT);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void onPause() {
        super.onPause();
    }

    class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //onServe(macAddress);
            if (view == button) {
                if (onoff == 0) {
                    onoff = 1;
                    button.setText("OFF");
                } else {
                    onoff = 0;
                    button.setText("ON");
                }
            }
        }
    }

    class imageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            testImage.setImageResource(imageList[(++imageID)%4]);
        }
    }

    //--
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (onoff == 1) {

            float lx = 0;
            long timeMillis = 0;

            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                return;
            }
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_LIGHT) {
                lx = (float) (event.values[0] * calibrationDevice);
                timeMillis = System.currentTimeMillis();
                illumi.setText("" + (int) lx);
                illumiAndTimeData += timeMillis + "," + lx + "\n";
            }
//            try {
//                save2SD("anone");
//            } catch (IOException e) {
//
//            }
            for (int i = 0; i < 10; i++) {
                if (i == 9) {
                    BILL[i] = (int) lx;
                    time[i] = timeMillis;
                } else {
                    BILL[i] = BILL[i + 1];
                    time[i] = time[i + 1];
                }
            }

            if (first == 1) {
                if (Math.abs(BILL[BILL.length - 2] - BILL[BILL.length - 1]) > 50) {
                    if (num == 1) result.setText("wait...");
                    sendFile("wait", illumiAndTimeData);
                    illumiAndTimeData = "";
                    first = 2;
                    start = time[8];
                    //startTimeStamp = (getNowTime());
                    for (int i = 0; i < 10; i++) {
                        if (i == 0)
                            str = String.valueOf(BILL[i]) + "," + String.valueOf(time[i] - start);
                        else
                            str = str + "\n" + String.valueOf(BILL[i]) + "," + String.valueOf(time[i] - start);
                    }
                }
            } else if (first == 2) {
                end = System.currentTimeMillis();
                str = str + "\n" + String.valueOf((int) event.values[0]) + "," + String.valueOf(end - start);
                for (int i = 0; i < BILL.length - 1; i++)
                    if (Math.abs(BILL[i] - BILL[i + 1]) < 10) check++;
                if (check >= BILL.length - 1) {
                    first = 1;
                    if (num == 1) {
                        String ans = judge(str);
                        result.setText(ans);
                        //timeStamp.setText("" + startTimeStamp);
                        timeStamp.setText("" + start);
                        sendFile(ans, illumiAndTimeData);
                        illumiAndTimeData = "";
                        String serveTime = getNowDate();
                        onServe(serveTime + "," + macAddress + "," + start + "," + ans +"," + imageID);
                    } else result.setText("OK");
                    num = 1;
                }
                check = 0;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendFile(String fileName, String message) {
//        try {
//            save2SD(fileName, message);
//        } catch (IOException e) {
//        }
    }

    private void save2SD(String fileName, String message) throws IOException {
        // ファイル保存先をSDカード内のパッケージ名フォルダ以下のsample.txtとします。
        //String filePath = "/sdcard/test/test"+getNowDate()+".txt";

        // yyyyMMddhhmmssファイル名.txtになるようにする

        String filePath = "/sdcard/test/" + fileName + "_" + getNowTime() + ".csv";
        File file = new File(filePath);

        // パッケージ名フォルダを作成します。
        //file.getParentFile().mkdir();

        // ファイル出力
        FileOutputStream fos = new FileOutputStream(file, true);

        // UTF-8でファイル出力
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);

        // テキストを書き込みます。
        bw.write(message);
        bw.flush();
        bw.close();
    }

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br>
     */
    public static String getNowDate() {
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
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

    public void someProcess(String strBuf){
        String[] strSplit = strBuf.split(",");
        if(strSplit[0].equals("ImageAllShare")){
            imageID=Integer.parseInt(strSplit[1]);
            testImage.setImageResource(imageList[imageID]);
        }
        else{
            receivedMessage.setText("error");
        }
    }

    public String judge(String sss) {

        String anser = "";
        String data[] = sss.split("\n");
        int length = data.length;
        int[] lx = new int[length];
        int[] time = new int[length];
        int start = 0;
        int end;
        int max = 0;
        int bottom = 0;
        int WAVE = 0;
        double A;
        double I;
        double Ts;
        double Te;
        double D;
        double S;
        double St;
        double Tt;
        int gesture;//0:hide 1:roll 2:up 3:down 4:slash

        for (int i = 0; i < length; i++) {
            String str[] = data[i].split(",");
            lx[i] = Integer.parseInt(str[0]);
            time[i] = Integer.parseInt(str[1]);
        }

        end = length - 10;
        for (int i = 0; i < length; i++) {
            if (time[i] == 0) start = i;
            if (i > 0 && lx[i] > lx[max]) max = i;
            if (i > 0 && lx[i] < lx[bottom]) bottom = i;
        }

        for (int i = start + 1; i < end; i++) {
            if (lx[i - 1] > lx[i] && lx[i] < lx[i + 1]) WAVE++;
            //if(lx[max]-lx[i]<15) WAVE++;
        }

        A = lx[max] - lx[bottom];
        I = lx[max];
        Ts = time[bottom] - time[start];
        Te = time[end] - time[bottom];

        D = A / I;
        S = Math.abs(A / Ts) - (A / Te);
        St = Ts - Te;
        Tt = Ts + Te;

        if (D >= 0.95) gesture = 0;
        else if (WAVE >= 3) gesture = 1;
        else if (Tt >= 425) {
            if (S >= -0.15) gesture = 2;
                //if(St<0) gesture = 2;
            else gesture = 3;
        } else {
            if (S >= 117.4) gesture = 2;
            else gesture = 4;
        }

        if (gesture == 0) {
            anser = "HIDE";
        } else if (gesture == 1) {
            anser = "ROLL";
        } else if (gesture == 2) {
            anser = "UP";
        } else if (gesture == 3) {
            anser = "DOWN";
        } else if (gesture == 4) {
            anser = "SLASH";
        } else {
            anser = "ERROR";
        }
        //result.setText(""+anser);

        for (int i = 0; i < BILL.length - 1; i++) {
            BILL[i] = BILL[BILL.length - 1];
        }
        return anser;
    }
    //--

    public void setCalibrationByMacAdress() {
        if (macAddress.equals("30:85:a9:2f:00:af")) {
            calibrationDevice = 1.0;
        } else if (macAddress.equals("ac:22:0b:5c:8c:0c")) {
            calibrationDevice = 3.20;
        } else if (macAddress.equals("02:00:00:00:00:00")) {
            calibrationDevice = 4.57;
        } else {
            calibrationDevice = 0.0;
        }
    }
}
