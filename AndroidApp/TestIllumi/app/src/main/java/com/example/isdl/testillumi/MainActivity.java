package com.example.isdl.testillumi;

import android.content.Context;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {
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
    private String gestureAnser = "";
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
    int start = 0;
    int end = 0;
    String str;
    int num = 0;
    int step =1;

    double dps;
    double wav;
    int tse ;
    double slp;

    int logThreshold;
    double illumiThreshold;
    double waveThreshold;

    String testMessage="";

    double lxBuf=-1;

    //private final static String BR = System.getProperty("line.separator");
    //IPアドレスの指定k
    private final static String IP = "172.20.11.109";
    private final static int PORT = 8080;

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

    //二次関数の近似式
    double calibrationAperDevice = 0.0;
    double calibrationBperDevice = 1.0;
    double calibrationCperDevice = 0.0;

    ArrayList<Double> illumiLog = new ArrayList<Double>();
    ArrayList<Long> timeDataLog  = new ArrayList<Long>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        String write = serveTime +","+ macAddress +","+anser;
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
                    step=1;
                    lxBuf=-1;
                    illumiLog = new ArrayList<Double>();
                    timeDataLog = new ArrayList<Long>();
                    illumiAndTimeData="";
                } else {
                    onoff = 0;
                    button.setText("ON");
                    //sendFile(gestureAnser, illumiAndTimeData);
                    onServe(start+",log\n,"+illumiAndTimeData);
                }
            }
        }
    }

    class imageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            testImage.setImageResource(imageList[(++imageID)%4]);
            if(imageID==4)imageID=0;
        }
    }

    //--
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (onoff == 1) {

            double lx = 0;
            long timeMillis = 0;
            double calibrationlx = 0;

            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                return;
            }

            int type = event.sensor.getType();
            if (type == Sensor.TYPE_LIGHT) {
                double rowlx = (event.values[0]);
                //キャリブレーション
                calibrationlx = (calibrationAperDevice*rowlx*rowlx+calibrationBperDevice*rowlx+calibrationCperDevice);
                lx = calibrationlx;
                //前後で割ってスムージング
                if(lxBuf==-1){
                    lxBuf=lx;
                }
                else{
                    double flag = ((lx+lxBuf)/2.0);
                    lxBuf=lx;
                    lx = flag;
                }
                timeMillis = System.currentTimeMillis();
                illumiLog.add(lx);
                timeDataLog.add(timeMillis);

                illumi.setText("" + lx);
                //illumiAndTimeData += lx + "\n";
                //illumiAndTimeData += timeMillis + "\t" + lx + "\n";
            }

            //step0.照度が落ちつくまで待機
            //とりあえず、データ数が揃うまで待つ
            if(illumiLog.size()<logThreshold){
                if(!result.getText().equals("wait")) {
                    result.setText("wait");
                }
            }

            //step1. データが揃ったら、照度が安定するまでまつ
            else if(step==1){
//                if(!result.getText().equals("wait")) {
//                    result.setText("wait");
//                }
                double illumiSum = 0;
                for(int i=0;i<logThreshold;i++){
                    illumiSum+=illumiLog.get((illumiLog.size() - 1) - i);
                }
                double illumiAve = (illumiSum/logThreshold);
                if(Math.abs(illumiAve-illumiLog.get(illumiLog.size()-1))<(illumiAve*illumiThreshold)){
                    check++;
                }else{
                    check=0;
                }
                if(check>logThreshold){
                    step=2;
                    result.setText("Ready");
                }
            }

            //step2. ジェスチャの開始を検知する
            else if(step==2){
//                if(!result.getText().equals("Ready")) {
//                    result.setText("Ready");
//                }
                double illumiSum = 0;
                for(int i=0;i<logThreshold;i++){
                    illumiSum+=illumiLog.get((illumiLog.size() - 1) - i);
                }
                double illumiAve = (illumiSum/logThreshold);
                if(Math.abs(illumiAve-illumiLog.get(illumiLog.size()-1))>illumiAve*illumiThreshold*2.5){
                    step=3;
                    check=0;
                    start=timeDataLog.size()-2;
                    result.setText("Analyzing...");
                }
            }
            //step3. ジェスチャの終了を検知する
            else if(step==3){
//                if(!result.getText().equals("Analyzing...")) {
//                    result.setText("Analyzing...");
//                }
                double illumiSum = 0;
                for(int i=0;i<logThreshold;i++){
                    illumiSum+=illumiLog.get((illumiLog.size() - 1) - i);
                }
                double illumiAve = (illumiSum/logThreshold);
                if(Math.abs(illumiAve-illumiLog.get(start))<illumiAve*illumiThreshold*4){
                    check++;
                }else{
                    check=0;
                }
                if(check>logThreshold*1.2){
                    step=4;
                    end=timeDataLog.size()-logThreshold;
                    end = judgeEnd();
                    //ジェスチャの判定
                    gestureAnser = judgeGesture(illumiLog, timeDataLog, start, end);
                    result.setText(gestureAnser);
                    //timeStamp.setText("" + timeDataLog.get(start));
                    timeStamp.setText(testMessage);
                    illumiAndTimeData += gestureAnser;
                    for(int i=start;i<=end;i++){
                        illumiAndTimeData+=","+illumiLog.get(i);
                    }
                    illumiAndTimeData += "\n";
                    //sendFile(gestureAnser, illumiAndTimeData);
                    //String serveTime = getNowTime();
                    onServe(start + "," + gestureAnser +"," + imageID+","+testMessage);
                }
            }
            //step4. step2に戻るために諸々頑張る
            else if(step==4){
                //illumiAndTimeData = "";
                double[] illumiLogBuf = new double[logThreshold];
                long[] timeDataLogBuf = new long[logThreshold];
                for(int i=0;i<logThreshold;i++){
                    illumiLogBuf[i]=illumiLog.get(illumiLog.size()-logThreshold+i);
                    timeDataLogBuf[i]=timeDataLog.get(timeDataLog.size()-logThreshold+i);
                }
                illumiLog = new ArrayList<Double>();
                timeDataLog = new ArrayList<Long>();
                for(int i=0;i<logThreshold;i++){
                    illumiLog.add(illumiLogBuf[i]);
                    timeDataLog.add(timeDataLogBuf[i]);
                }
                double illumiSum = 0;
                for(int i=0;i<logThreshold;i++){
                    illumiSum+=illumiLog.get((illumiLog.size() - 1) - i);
                }
                double illumiAve = (illumiSum/logThreshold);
                if(Math.abs(illumiAve-illumiLog.get(illumiLog.size()-1))<(illumiAve*illumiThreshold)){
                    check++;
                }else{
                    check=0;
                }
                if(check>logThreshold){
                    step=2;
                    check=0;
                    lxBuf=-1;
                    //result.setText("Ready");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void sendFile(String fileName, String message) {
        try {
            save2SD(fileName, message);
        } catch (IOException e) {
        }
    }

    private void save2SD(String fileName, String message) throws IOException {
        // ファイル保存先をSDカード内のパッケージ名フォルダ以下のsample.txtとします。
        //String filePath = "/sdcard/test/test"+getNowDate()+".txt";

        // yyyyMMddhhmmssファイル名.txtになるようにする

        String filePath = Environment.getExternalStorageDirectory() + "/sample.txt";
        //String filePath = "/sdcard/" + fileName + "_" + getNowTime() + ".csv";
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
        String mission = strSplit[0];
        if(mission.equals("ImageAllShare")){
            imageID=Integer.parseInt(strSplit[1]);
            testImage.setImageResource(imageList[imageID]);
        }
        else if(mission.equals("ImageSomeShare")){
            if(macAddress.equals(strSplit[2])){
                imageID=Integer.parseInt(strSplit[1]);
                testImage.setImageResource(imageList[imageID]);
            }
        }
        else if(mission.equals("ImageSomeShareFlagON")||mission.equals("")){
            System.out.println("no problem");
        }
        else{
            receivedMessage.setText("error");
        }
    }
    public String judgeGesture(ArrayList<Double>illumiLog, ArrayList<Long>timeDataLog, int start, int end){
//        int startTime = 0;
//        int endTime   = (int)(end-start);

        //end特化型認識
        double max = ((illumiLog.get(start)+illumiLog.get(end))/2);
        int bottom = 0;

        for (int i = start; i <= end; i++) {;
            if (i > 0 && illumiLog.get(i) < illumiLog.get(bottom)) bottom = i;
        }

        double Ts = (timeDataLog.get(bottom)-timeDataLog.get(start));
        double Te = (timeDataLog.get(end)-timeDataLog.get(bottom));

        double A= max - illumiLog.get(bottom);
        double deepness = (double)A/(double)max;
        double slope = (double)A/(double)Ts-(double)A/(double)Te;
//        Log.d("slope",String.valueOf(slope));
//        Log.d("A",String.valueOf(A));
//        Log.d("Ts",String.valueOf(Ts));
//        Log.d("Te",String.valueOf(Te));a
        double time  = (double)(Ts+Te);
        //wave特化型認識
        double wave = judgeWaveNum(illumiLog,start,end,max);

        //なめらかにして波の検出効率を上げた→失敗
//        for (int i = start+(int)(logThreshold/2); i <= end-(int)(logThreshold/2); i++) {
//            //if (illumiLog.get(i-(int)(logThreshold/2)) > illumiLog.get(i) && illumiLog.get(i) < illumiLog.get(i+(int)(logThreshold/2))){
//            if (illumiLog.get(i-1) > illumiLog.get(i) && illumiLog.get(i) < illumiLog.get(i+1)){
//                wave++;
//            }
//            //if(lx[max]-lx[i]<15) WAVE++;
//        }

//        Log.d("deepness", String.valueOf(deepness));
//        Log.d("wave", String.valueOf(wave));
//        Log.d("time", String.valueOf(time));
//        Log.d("slope", String.valueOf(slope));
        testMessage =String.valueOf(deepness)+","+String.valueOf(wave)+","+String.valueOf(time)+","+String.valueOf(slope);


        //全ジェスチャ
        if (deepness >= dps) return "HIDE";
        else if (wave >= wav) return "ROLL";
        else if (time >= tse) {
            if (slope >= slp) return "UP";
                //if(St<0) gesture = 2;
            else return "DOWN";
        } else {
            if (slope >= 130) return "UP";
            else return "SLASH";
        }

        //startからendまでをarraylistから抽出してillumiandtimeData


        //HIDEとSLASHのジェスチャ
//        if (deepness >= dps) return "HIDE";
//        else return "SLASH";

    }

    //波特化型認識
    public double judgeWaveNum(ArrayList<Double> illumiLog, int start, int end, double max){
        int waveFlag=0;
//        for(int i = start;i<end;i++){
//            int diff = illumiLog.get(i+1)-illumiLog.get(i);
//            if(Math.abs(diff) > A*waveThreshold){
//                waveFlag++;
//            }
//        }

        double lastDiff=0.0;
        ArrayList<Double> illumiMountainLog =  new ArrayList<Double>();
        for (int i=start;i<=end;i++){
            //illumiAndTimeData += i+"\t"+illumiLog.get(i)+"\n";
            double diff = illumiLog.get(i+1)-illumiLog.get(i);
            if(Math.abs(diff)==0 || diff*lastDiff<0){
                illumiMountainLog.add(illumiLog.get(i));
//                Log.d("illumi", String.valueOf(illumiLog.get(i)));
//                Log.d("time",String.valueOf(i));
            }
            lastDiff=diff;
        }

        if(illumiMountainLog.size()==0){
            return 0;
        }

        double lastIllumiMountain = illumiMountainLog.get(0);
        for(int i=1; i<illumiMountainLog.size();i++){
            double illumiDiff = illumiMountainLog.get(i)-lastIllumiMountain;
//            Log.d("mountain",String.valueOf(illumiMountainLog.get(i)));
            if(Math.abs(illumiDiff)>(double)max*0.05){
                waveFlag++;
//                Log.d("add",String.valueOf(illumiDiff) );
            }
            lastIllumiMountain=illumiMountainLog.get(i);
        }
        return ((waveFlag+1)/2);
    }

    //end特化型認識
    public int judgeEnd(){
        int endPoint = end;
        double startIllumi = illumiLog.get(start);
        for(int i=endPoint;i>0;i--){
            double diff = Math.abs(startIllumi-illumiLog.get(i));
            if(diff>(double)startIllumi*0.05){
                endPoint = i+1;
                return endPoint;
            }
        }
        return endPoint;
    }

    //山下のジェスチャ認識
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
//            calibrationAperDevice = 0.0001008;
//            calibrationBperDevice = 0.356056925;
//            calibrationCperDevice = 61.36738592;

            logThreshold = 10;
            illumiThreshold = 0.025;

            dps = 0.85;
            wav = 2.0;
            tse = 700;
            slp = 0.45;

        } else if (macAddress.equals("ac:22:0b:5c:8c:0c")) {
//            calibrationAperDevice = 0.003207998;
//            calibrationBperDevice = 0.239641099;
//            calibrationCperDevice = 160.320846;

            calibrationBperDevice=1.0;

            logThreshold = 10;
            illumiThreshold = 0.025;

            dps = 0.85;
            wav = 2.0;
            tse = 700;
            slp = 0.45;

        } else if (macAddress.equals("02:00:00:00:00:00")) {
//            calibrationAperDevice = 0.005032123;
//            calibrationBperDevice = 0.454065939;
//            calibrationCperDevice = 160.43202;

            calibrationBperDevice = 1.0;

            logThreshold = 10;
            illumiThreshold = 0.025;

            dps = 0.85;
            wav = 2.0;
            tse = 700;
            slp = 0.45;

        } else {
            calibrationAperDevice = 0.0;
            calibrationBperDevice = 1.0;
            calibrationCperDevice = 0.0;
        }
    }
}
