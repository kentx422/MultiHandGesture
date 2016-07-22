package com.example.isdl.demoappforjulymonthlylecture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    //private TextView result;
    //private TextView receivedMessage;
    private TextView backNumber;
    //private TextView illumi;
    private TextView printTime;

    private RelativeLayout relativeLayout;

    private boolean flag = true;
    private String illumiAndTimeData = "";
    private String gestureAnser = "";
    private String macAddress;
    private String deviceName;
    private String udid;
    private String backNumberStr;
    private int fontSize;

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
    int tme;
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
    ArrayList<Long> currentTimeDataLog = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();



        // ボタンを設定
        //result = (TextView) findViewById(R.id.textViewB);
        //receivedMessage = (TextView) findViewById(R.id.textViewD);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayoutBackGround);
        backNumber = (TextView) findViewById(R.id.textViewE);
        //illumi = (TextView) findViewById(R.id.textViewA);
        //printTime = (TextView) findViewById(R.id.textViewC);

        relativeLayout.setBackgroundColor(Color.BLACK);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Android IDの取得
        udid = Settings.Secure.getString(this.getContentResolver(), Settings.System.ANDROID_ID);
        deviceName = getDeviceNameByUDID(udid);

        //閾値
        logThreshold = 2;
        illumiThreshold = 0.05;

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
                            //receivedMessage.setText("Received Message: "+finalStrBuf);
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
        String serveTime = getNowTime();
        final String temp = serveTime +","+ udid +","+anser;
        //receivedMessage.setText("Send Message: "+temp);
        //スレッッドの生成
        Thread thread = new Thread(new Runnable() {
            public void run() {
                error = false;
                try {
                    //データの送信
                    if (socket != null && socket.isConnected()) {
                        String write = temp;
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

    //--
    @Override
    public void onSensorChanged(SensorEvent event) {
        //if (onoff == 1) {
        if (onoff == 1) {

        double lx = 0;
            long timeMillis = 0;
            long nanotTime = 0;
            double calibrationlx = 0;

            if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
                return;
            }

            int type = event.sensor.getType();
            if (type == Sensor.TYPE_LIGHT) {
                double rowlx = (event.values[0]);
                //キャリブレーション
                lx = rowlx;
                timeMillis = System.currentTimeMillis();
                nanotTime = System.nanoTime();
                illumiLog.add(lx);
                //timeDataLog.add(timeMillis);
                timeDataLog.add(nanotTime);
                currentTimeDataLog.add(timeMillis);

                //illumi.setText("" + lx);
                //illumiAndTimeData += lx + "\n";
                //illumiAndTimeData += timeMillis + "\t" + lx + "\n";
            }



            //step0.照度が落ちつくまで待機
            //とりあえず、データ数が揃うまで待つ
            if(illumiLog.size()<logThreshold){
//                if(!result.getText().equals("wait")) {
//                    result.setText("wait");
//                }
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
                if(Math.abs(illumiLog.get(illumiLog.size() - 1) - illumiLog.get(illumiLog.size() - 2))<(illumiAve*illumiThreshold)){
                    check++;
                }else{
                    check=0;
                }
                if(check>logThreshold){
                    step=2;
                    //result.setText("Ready");
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
                if(Math.abs(illumiAve-illumiLog.get(illumiLog.size()-1))>illumiAve*illumiThreshold){
                    //step=3;
                    step=4;
                    check=0;
                    start=timeDataLog.size()-logThreshold;
                    onServe("pairing");
                    //result.setText("Analyzing...");
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
                if(Math.abs(illumiLog.get(illumiLog.size() - 1)-illumiLog.get(illumiLog.size() - 2))<illumiAve*illumiThreshold){
                    check++;
                    //receivedMessage.setText(""+check);
                }
                else{
                    check=0;
                }
//
//                if(Math.abs(illumiAve-illumiLog.get(start))<illumiAve*illumiThreshold*10){
//                    check++;
//                    receivedMessage.setText(""+check);
//                }else{
//                    check=0;
//                }
                if(check>logThreshold*2){
                    step=4;
                    end=timeDataLog.size()-logThreshold*3;
                    //end = judgeEnd();
                    //ジェスチャの判定
                    //gestureAnser = judgeGesture(illumiLog, timeDataLog, start, end);
                    Double aveLux = (illumiLog.get(start) + illumiLog.get(end))/2;
                    gestureAnser = extractFeature(aveLux, timeDataLog, illumiLog);
                    //result.setText(gestureAnser);
                    //timeStamp.setText("" + timeDataLog.get(start));
                    //timeStamp.setText(testMessage);
                    illumiAndTimeData += gestureAnser;
                    for(int i=start;i<=end;i++){
                        illumiAndTimeData+=","+illumiLog.get(i);
                    }
                    illumiAndTimeData += "\n";
                    //sendFile(gestureAnser, illumiAndTimeData);
                    //String serveTime = getNowTime();
                    //onServe(currentTimeDataLog.get(start) + "," + gestureAnser +"," + imageID+","+testMessage);
                    //onServe(currentTimeDataLog.get(start) + "," + gestureAnser);
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
                currentTimeDataLog = new ArrayList<Long>();

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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("", "ACTION_DOWN");
                Log.d("", "EventLocation X:" + motionEvent.getX() + ",Y:" + motionEvent.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (onoff == 0) {
                    onoff = 1;
                    backNumber.setText(backNumberStr);
                    step=1;
                    lxBuf=-1;
                    illumiLog = new ArrayList<Double>();
                    timeDataLog = new ArrayList<Long>();
                    illumiAndTimeData="";
                    relativeLayout.setBackgroundColor(Color.BLACK);
                    backNumber.setTextSize(fontSize);
                } else {
                    onoff = 0;
                    backNumber.setText("START");
                    backNumber.setTextSize(80);
                    relativeLayout.setBackgroundColor(Color.BLACK);
                    //sendFile(gestureAnser, illumiAndTimeData);
                    //onServe(start + ",log\n," + illumiAndTimeData);
                }
//                printTime.setText("current: "+System.currentTimeMillis()+"  nowTime: "+getNowTime());
//                onServe(System.currentTimeMillis()+","+" ");


                Log.d("", "ACTION_UP");
                long eventDuration2 = motionEvent.getEventTime() - motionEvent.getDownTime();
                Log.d("", "eventDuration2: " +eventDuration2+" msec");
                Log.d("", "Pressure: " + motionEvent.getPressure());

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("", "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("", "ACTION_CANCEL");
                break;
        }

        return false;
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

        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            nowTime += "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            nowTime += "" + calendar.get(Calendar.HOUR_OF_DAY);
        }
        if (calendar.get(Calendar.MINUTE)  < 10) {
            nowTime += "0" + calendar.get(Calendar.MINUTE);
        } else {
            nowTime += "" + calendar.get(Calendar.MINUTE);
        }
        if (calendar.get(Calendar.SECOND)  < 10) {
            nowTime += "0" + calendar.get(Calendar.SECOND);
        } else {
            nowTime += "" + calendar.get(Calendar.SECOND);
        }
        return nowTime;
    }

    public void someProcess(String strBuf){
        String[] splitSemicolon = strBuf.split(";");
        int existFlag = 0;
        for(int i=0;i<splitSemicolon.length;i++){
            String[] splitConma = splitSemicolon[i].split(",");
            if(splitConma[0].equals(deviceName)){

                backNumber.setText(""+(Integer.parseInt(splitConma[1])+1));
                relativeLayout.setBackgroundColor(Color.rgb(9,83,133));
                existFlag = 1;
            }
        }
        if(existFlag==0){
            relativeLayout.setBackgroundColor(Color.rgb(174,16,42));
            backNumber.setText("");
        }

//        if(mission.equals("ImageAllShare")){
//            imageID=Integer.parseInt(strSplit[1]);
//            testImage.setImageResource(imageList[imageID]);
//        }
//        else if(mission.equals("ImageSomeShare")){
//            if(macAddress.equals(strSplit[2])){
//                imageID=Integer.parseInt(strSplit[1]);
//                testImage.setImageResource(imageList[imageID]);
//            }
//        }
//        else if(mission.equals("ImageSomeShareFlagON")||mission.equals("")){
//            System.out.println("no problem");
//        }
//        else{
//            receivedMessage.setText("error");
//        }
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
        else if (time >= tme) {
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
    public static double judgeWaveNum(ArrayList<Double> illumiLog, int start, int end, double max){
        int waveFlag=0;
        double lastDiff=0.0;
        ArrayList<Double> illumiMountainLog =  new ArrayList<Double>();
        illumiMountainLog.add(max);
        for (int i=start;i<=end-1;i++){
            //System.out.println(illumiLog.get(i+1)+"-"+illumiLog.get(i));
            double diff = illumiLog.get(i+1)-illumiLog.get(i);
            if(Math.abs(diff)==0 || diff*lastDiff<0){
                illumiMountainLog.add(illumiLog.get(i));
                //System.out.println(illumiLog.get(i));
            }
            lastDiff=diff;
        }
        illumiMountainLog.add(max);

        if(illumiMountainLog.size()==2){
            return 0;
        }

        ArrayList<Double> illumiMountainClusterLog =  new ArrayList<Double>();
        illumiMountainClusterLog.add(illumiMountainLog.get(0));
        for(int i=1; i<illumiMountainLog.size();i++){
            double diff = illumiMountainLog.get(i-1)-illumiMountainLog.get(i);
            if(Math.abs(diff)>max*0.2){
                illumiMountainClusterLog.add(illumiMountainLog.get(i));
            }
        }

//      System.out.println(illumiMountainLog);
//      System.out.println(illumiMountainClusterLog);

        double lastIllumiMountainCluster = illumiMountainClusterLog.get(0);
        double lastIllumiDiff = 0.0;

        for(int i=1; i<illumiMountainClusterLog.size();i++){
            double illumiDiff = illumiMountainClusterLog.get(i)-lastIllumiMountainCluster;
            //System.out.println(illumiDiff+","+lastIllumiDiff);
            if(illumiDiff*lastIllumiDiff<0){
                waveFlag++;
                //System.out.println("wave");
            }

//          if(Math.abs(illumiDiff)>(double)max*0.2){
//              waveFlag++;
//              System.out.println("diff:"+illumiDiff);
//          }
            lastIllumiMountainCluster=illumiMountainLog.get(i);
            lastIllumiDiff = illumiDiff;
        }
        return (waveFlag+1.0)/2.0;
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
    //--------
    //特徴点抽出
    public String extractFeature(Double aveLux,ArrayList<Long> nanoTime, ArrayList<Double> illumiLog){
        String result = "";
        String gesture = "";
        int startPoint = 0;
        int endPoint = illumiLog.size()-1;
        int maxPoint = 0;
        int minPoint = 0;

        double waveCount = 0.0;
        double totalWidth = 0.0;
        double tiltAve = 0.0;
        double deepness = 0.0;

        //どれだけ変化したらstartあるいはendとみなすかの閾値
        double threshold = aveLux*0.05;


        //lux(ArrayList<String>) >> illumiLog(ArrayList(Double))
//        ArrayList<Double> illumiLog = new ArrayList<Double>();
//        for(int i = 0;i<lux.size();i++){
//            //System.out.println(lux.get(i));
//            illumiLog.add(Double.parseDouble(lux.get(i)));
//        }

        //start探し
        for(int i = 1;i<illumiLog.size();i++){
            if(Math.abs(illumiLog.get(i-1) - illumiLog.get(i)) > threshold){
                startPoint = i-1;
                break;
            }
        }
        //end探し
        for(int i = illumiLog.size()-2;i>=0;i--){
            if(Math.abs(illumiLog.get(i) - illumiLog.get(i+1)) > threshold){
                endPoint = i+1;
                break;
            }
        }

        //max探し
        double max = 0.0;
        for(int i = 0;i<illumiLog.size();i++){
            if(illumiLog.get(i) > max){
                maxPoint = i;
                max = illumiLog.get(i);
            }
        }
        //max探し
        double min = aveLux;
        for(int i = 0;i<illumiLog.size();i++){
            if(illumiLog.get(i) < min){
                minPoint = i;
                min = illumiLog.get(i);
            }
        }


//		System.out.println(startPoint+","+endPoint);
//		System.out.println(illumiLog.get(startPoint)+","+illumiLog.get(endPoint));

        //weveCount
        waveCount = judgeWaveNum(illumiLog, startPoint, endPoint, aveLux);

        //totalWidth msで表現
        //totalWidth = (Double.parseDouble(nanoTime.get(endPoint)) - Double.parseDouble(nanoTime.get(startPoint)))/1000000.0;
        totalWidth = (nanoTime.get(endPoint).doubleValue() - nanoTime.get(startPoint).doubleValue())/1000000.0;

        //tiltAve
        //double ts = (Double.parseDouble(nanoTime.get(minPoint)) - Double.parseDouble(nanoTime.get(startPoint)))/1000000.0;
        //double te = (Double.parseDouble(nanoTime.get(endPoint)) - Double.parseDouble(nanoTime.get(minPoint)))/1000000.0;
        //double A  = illumiLog.get(maxPoint) - illumiLog.get(minPoint);
        double ts   = (nanoTime.get(minPoint).doubleValue() - nanoTime.get(startPoint).doubleValue())/1000000.0;
        double te   = (nanoTime.get(endPoint).doubleValue() - nanoTime.get(minPoint).doubleValue())/1000000.0;
        double A    = illumiLog.get(maxPoint) - illumiLog.get(minPoint);
        tiltAve = A/ts - A/te;

        //deepness
        deepness = A / illumiLog.get(maxPoint);


        //System.out.println(illumiLog.get(startPoint)+","+illumiLog.get(endPoint));
        result = waveCount+","+totalWidth+","+tiltAve+","+deepness;
        //System.out.println(result);
        //receivedMessage.setText(result);

        if(waveCount >= 2.5){
            gesture = "roll";
        }else if(deepness > 0.92){
            gesture = "hide";
        }else if(totalWidth < 425){
            gesture = "slash";
        }else if(tiltAve > 0.13){
            gesture = "up";
        }else{
            gesture = "down";
        }

        start = startPoint;
        end = endPoint;

        return gesture;
    }



    public void setCalibrationByMacAdress() {
        if (macAddress.equals("30:85:a9:2f:00:af")) {
//            calibrationAperDevice = 0.0001008;
//            calibrationBperDevice = 0.356056925;
//            calibrationCperDevice = 61.36738592;

            logThreshold = 10;
            illumiThreshold = 0.025;

            dps = 0.85;
            wav = 2.0;
            tme = 700;
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
            tme = 700;
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
            tme = 700;
            slp = 0.45;

        } else {
            calibrationAperDevice = 0.0;
            calibrationBperDevice = 1.0;
            calibrationCperDevice = 0.0;
        }
    }

    public String getDeviceNameByUDID(String udid) {
        if (udid.equals("a4c7b9190b6bd931")) {
            backNumberStr = "A";
            fontSize = 300;
            return "nexus7-2012-hmurakami";
        }
        else if (udid.equals("8e9e784548c0cb6a")) {
            backNumberStr = "D";
            fontSize = 300;
            return "nexus7-2013-haida";
        }
        else if (udid.equals("f7196b5116fe5f4d")) {
            return "nexus7-2013-amiyoshi";
        }
        else if (udid.equals("a63f8c393f29b971")) {
            return "Galaxy-S5-atonomura";
        }
        else if (udid.equals("7b2f5bfd497b875f")) {
            backNumberStr = "C";
            fontSize = 200;
            return "Xperia-Z5-tyamamoto";
        }
        else if (udid.equals("6834af3a92999f3b")) {
            backNumberStr = "B";
            fontSize = 200;
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
