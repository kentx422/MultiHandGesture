package com.example.isdl.multigesture4devicesuntildecember;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener, Runnable {

    private TextView state;
    private TextView nowlx;
    private Button onoffButton;
    private TextView version;
    private TextView testText;
    private TextView deviceTime;
    private Button setTimeButton;
    private EditText ipAddress;
    private Button connectButton;
    private TextView requestOrder;
    private Button orderButton;
    private TextView deviceChar;

    private int onoffButtonFlag;
    private int connectButtonFlag;
    private int orderButtonFlag;

    private int step = 0;
    private int start = 0;
    private int end = 0;

    private long timeDeviceStart;
    private long timeDeviceNow;
    private long timeServerWhenSynchronizing;
    private long timeServerNow;

    //    private String deviceName;
//    private String subjectName;
    private SensorManager manager;
    private String gesture;
    private String udid;

    private String serverStart;

    int logThreshold;
    double illumiThreshold;
    int check;
    ArrayList<Double> illumiLog = new ArrayList<>();
    ArrayList<Long> timeDataLog = new ArrayList<>();
    ArrayList<Long> nanotimeDataLog = new ArrayList<>();

    //version: 0.0 (Copy from MultiGestureUntilDecember) > 1.0 (Connect mobile devices)
    private String versionNow = " 1.3 (Connect mobile devices)";

    //IPアドレスの指定
    private static String IP = "172.20.11.175";
    private final static int PORT = 8080;

    private Socket socket; //ソケット
    private InputStream in;      //入力ストリーム
    private OutputStream out;    //出力ストリーム
    private boolean error;  //エラー

    private final Handler handler = new Handler();//ハンドラ

    private String resieveMessage;
    private String sendMessage;

    private Thread threadForTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //端末の開始時間をセット
        timeDeviceStart = System.currentTimeMillis();
        timeDeviceNow = 0;
        timeServerWhenSynchronizing = 0;

        // Android IDの取得
        udid = Settings.Secure.getString(this.getContentResolver(), Settings.System.ANDROID_ID);

        //ボタンとテキストの設定
        state = (TextView) findViewById(R.id.state);
        nowlx = (TextView) findViewById(R.id.lx);
        onoffButton = (Button) findViewById(R.id.onoff);
        testText = (TextView) findViewById(R.id.testtext);
        deviceTime = (TextView) findViewById(R.id.deviceTime);
        setTimeButton = (Button) findViewById(R.id.setTime);
        version = (TextView) findViewById(R.id.version);
        version.setText("version: " + versionNow);
        ipAddress = (EditText)  findViewById(R.id.IPAddress);
        ipAddress.setText(IP);
        connectButton = (Button) findViewById(R.id.connectButton);
        requestOrder = (TextView) findViewById(R.id.requestOrder);
        orderButton = (Button) findViewById(R.id.orderButton);
        deviceChar = (TextView) findViewById(R.id.deviceChar);

        deviceChar.setText(getDeviceCharByUDID(udid));

        onoffButtonFlag=0;
        connectButtonFlag=0;
        orderButtonFlag=0;

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        onoffButton.setOnClickListener(new clickListener());
        setTimeButton.setOnClickListener(new clickListener());
        connectButton.setOnClickListener(new clickListener());
        orderButton.setOnClickListener(new clickListener());

        //閾値
        logThreshold = 2;
        illumiThreshold = 0.05;

        //時間を表示するためのスレッド
        threadForTime = new Thread(this);
        threadForTime.start();
    }

    @Override
    public void onStart() {
        super.onStart();

//        //スレッドの生成
//        Thread thread = new Thread() {
//            public void run() {
//                try {
//                    connect(IP, PORT);
//                } catch (Exception e) {
//                }
//            }
//        };
//        thread.start();
    }

    //アクティビティの停止時に呼ばれる
    @Override
    public void onStop() {
        super.onStop();
        disconnect();

    }

    //--------------接続
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
                            testText.setText(finalStrBuf);
                            someProcessForDecember(finalStrBuf);
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
        final String temp = serveTime +","+ udid +","+anser+";";
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
            socket.close();
            socket = null;
        } catch (Exception e) {
        }
    }


    //--------------接続プログラム終わり

    @Override
    public void run() {
        while (true) {
            // sleep: period msec
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    timeDeviceNow = System.currentTimeMillis();
                    // カウント時間 = 経過時間 - 開始時間
                    //double diffTime = (timeServerWhenSynchronizing + timeDeviceNow - timeDeviceStart)/1000.0;
                    //deviceTime.setText(String.valueOf(diffTime));
                    timeServerNow = (timeServerWhenSynchronizing + timeDeviceNow - timeDeviceStart);
                    deviceTime.setText(String.valueOf(timeServerNow/1000.0));
                }
            });
        }
    }


    class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view == onoffButton) {
                if (onoffButtonFlag == 0) {
                    onoffButtonFlag = 1;
                    onoffButton.setText("OFF");
                    state.setText(gesture);
                } else if(onoffButtonFlag == 1) {
                    onoffButtonFlag = 0;
                    onoffButton.setText("ON");
                    illumiLog = new ArrayList<Double>();
                    timeDataLog = new ArrayList<Long>();
                    step=0;
                    state.setText("Not in service");
                }
                else{
                    System.out.println("ERROR: onoffButtonFlag");
                }
            }
            else if(view == setTimeButton){
                onServe("setTime");
            }
            else if(view == connectButton){
                if(connectButtonFlag==0) {
                    connectButtonFlag=1;
                    connectButton.setText("DISCONNECT");
                    IP = String.valueOf(ipAddress.getText());
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
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onServe("connect");
                } else if(connectButtonFlag == 1) {
                    connectButtonFlag = 0;
                    connectButton.setText("CONNECT");

                    onoffButtonFlag = 0;
                    onoffButton.setText("ON");
                    illumiLog = new ArrayList<Double>();
                    timeDataLog = new ArrayList<Long>();
                    step=0;
                    state.setText("Not in service");

                    orderButtonFlag = 0;
                    orderButton.setText("Start Order");

                    onServe("disconnect");
                } else{
                    System.out.println("ERROR: connectButtonFlag");
                }

            }
            else if(view == orderButton){
                if(orderButtonFlag==0) {
                    onServe("startOrder");
                    orderButton.setText("next Order: "+orderButtonFlag+1);
                } else if(orderButtonFlag > 0) {
                    onServe("nextOrder," +(orderButtonFlag+1));
                    orderButton.setText("next Order: "+orderButtonFlag+1);
                }else{
                    System.out.println("ERROR: orderButtonFlag");
                }
                orderButtonFlag++;
            }
        }
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
        if (onoffButtonFlag == 1) {

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

                nowlx.setText("" + lx);

                illumiLog.add(lx);
                timeDataLog.add(millistime);
                nanotimeDataLog.add(nanotime);

                onServe("illuminanceAndTimeData,"+lx+","+timeServerNow);

                //recognizeGesture(lx);
            }
        }
    }

    //受信したメッセージに対する処理
    void someProcessForDecember(String message){
        String[] sentence = message.split(";");

        for(int i=0; i<sentence.length;i++){
            String[] word = sentence[i].split(",");
            String udidFromServer = word[1];
            String operator = word[2];

            state.setText("operator :"+operator);

            if(operator.equals("timeFromServer")||operator.equals("replySetTime")) {
                timeServerWhenSynchronizing = Long.parseLong(word[3]);
                timeDeviceStart = System.currentTimeMillis();
            }
            else if(operator.equals("replyDisconnect")) {
                if (!udid.equals(udidFromServer)) {
                    connectButtonFlag = 0;
                    connectButton.setText("CONNECT");
                    onServe("replyReplyDisconnect");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    onoffButtonFlag = 0;
                    onoffButton.setText("ON");
                    illumiLog = new ArrayList<Double>();
                    timeDataLog = new ArrayList<Long>();
                    step=0;
                    state.setText("Not in service");

                    disconnect();
                }else{
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //onServe("makeCSV");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    onoffButtonFlag = 0;
                    onoffButton.setText("ON");
                    illumiLog = new ArrayList<Double>();
                    timeDataLog = new ArrayList<Long>();
                    step=0;
                    state.setText("Not in service");

                    disconnect();
                }
            }

            else if(operator.equals("replyStartOrder")){
                String tempOrder = word[3];
                if(!tempOrder.equals("endOrder")){
                    requestOrder.setText(tempOrder);
                    orderButtonFlag = 1;
                    orderButton.setText("Next Order: "+orderButtonFlag);
                }
                else{
                    if(word[1].equals(udid)){
                        orderButtonFlag = 0;
                        onServe("endOrder");
                        orderButton.setText("Start Order");
                    }
                }
            }
            else if(operator.equals("replyNextOrder")){
                String tempOrder = word[3];
                if(!tempOrder.equals("endOrder")){
                    int tempFlag = Integer.parseInt(word[4]);
                    requestOrder.setText(tempOrder);
                    orderButtonFlag = tempFlag;
                    orderButton.setText("Next Order: "+orderButtonFlag);
                }
                else{
                    if(word[1].equals(udid)){
                        orderButtonFlag = 0;
                        onServe("endOrder");
                        orderButton.setText("Start Order");
                    }
                }
            }
            else if(operator.equals("replyEndOrder")){
                requestOrder.setText("REQUEST");
                orderButtonFlag = 0;
                orderButton.setText("Start Order");
            }



//            else if(operator.equals("replyChooseGesture")) {
//                if (!udid.equals(udidFromServer)) {
//                    String timeGesture = word[3];
//                    String gesture = word[4];
//                    testText.setText(gesture);
//                    if (gesture.contains("toRight")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        toRightButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,toRight" + toRightButtonFlag + "," + timeGesture);
//                        toRightButton.setText("to R: " + toRightButtonFlag);
//                    } else if (gesture.contains("upRight")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        upRightButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,upRight" + upRightButtonFlag + "," + timeGesture);
//                        upRightButton.setText("up R: " + upRightButtonFlag);
//                    } else if (gesture.contains("downRight")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        downRightButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,downRight" + downRightButtonFlag + "," + timeGesture);
//                        downRightButton.setText("down R: " + downRightButtonFlag);
//                    } else if (gesture.contains("toLeft")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        toLeftButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,toLeft" + toLeftButtonFlag + "," + timeGesture);
//                        toLeftButton.setText("to L: " + toLeftButtonFlag);
//                    } else if (gesture.contains("upLeft")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        upLeftButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,upLeft" + upLeftButtonFlag + "," + timeGesture);
//                        upLeftButton.setText("up L: " + upLeftButtonFlag);
//                    } else if (gesture.contains("downLeft")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        downLeftButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,downLeft" + downLeftButtonFlag + "," + timeGesture);
//                        downLeftButton.setText("down L: " + downLeftButtonFlag);
//                    } else if (gesture.contains("toTop")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        toTopButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,toTop" + toTopButtonFlag + "," + timeGesture);
//                        toTopButton.setText("to T: " + toTopButtonFlag);
//                    } else if (gesture.contains("upTop")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        upTopButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,upTop" + upTopButtonFlag + "," + timeGesture);
//                        upTopButton.setText("up T: " + upTopButtonFlag);
//                    } else if (gesture.contains("downTop")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        downTopButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,downTop" + downTopButtonFlag + "," + timeGesture);
//                        downTopButton.setText("down T: " + downTopButtonFlag);
//                    } else if (gesture.contains("toBottom")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        toBottomButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,toBottom" + toBottomButtonFlag + "," + timeGesture);
//                        toBottomButton.setText("to B: " + toBottomButtonFlag);
//                    } else if (gesture.contains("upBottom")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        upBottomButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,upBottom" + upBottomButtonFlag + "," + timeGesture);
//                        upBottomButton.setText("up B: " + upBottomButtonFlag);
//                    } else if (gesture.contains("downBottom")) {
//                        int gestureTempFlag = Integer.parseInt(gesture.replaceAll("[^0-9]", ""));
//                        downBottomButtonFlag = gestureTempFlag;
//                        onServe("replyReplyChooseGesture,downBottom" + downBottomButtonFlag + "," + timeGesture);
//                        downBottomButton.setText("down B: " + downBottomButtonFlag);
//                    }
//                }
//            }
        }
    }

    void recognizeGesture(double lx) {
        double illumiSum = 0;
        double illumiAve = 0;
        double illumiDiff = 0;
        switch (step) {
            //step0.照度が落ちつくまで待機
            //とりあえず、データ数が揃うまで待つ
            case 0:
                state.setText("step 0");
                if (illumiLog.size() > logThreshold) {
                    step = 1;
                }
                break;
            //step1. データが揃ったら、照度が安定するまでまつ
            case 1:
                state.setText("step 1");
                for (int i = 0; i < logThreshold; i++) {
                    illumiSum += illumiLog.get((illumiLog.size() - 1) - i);
                }
                illumiAve = (illumiSum / logThreshold);
                illumiDiff =  Math.abs(illumiLog.get(illumiLog.size() - 1) - illumiLog.get(illumiLog.size() - 2));

                if ( illumiDiff < (illumiAve * illumiThreshold)) {
                    check++;
                } else {
                    check = 0;
                }
                if (check > logThreshold) {
                    //step = 2;
                }

                break;
            //step2. ジェスチャの開始を検知する
            case 2:
                state.setText("step 2");
                illumiSum = 0;
                for (int i = 0; i < logThreshold; i++) {
                    illumiSum += illumiLog.get((illumiLog.size() - 1) - i);
                }
                illumiAve = (illumiSum / logThreshold);
                if (Math.abs(illumiAve - illumiLog.get(illumiLog.size() - 1)) > illumiAve * illumiThreshold) {
                    step = 3;
                    check = 0;
                    start = timeDataLog.size() - logThreshold;
                }
                break;
            //step3. ジェスチャの終了を検知する
            case 3:
                state.setText("step 3");
                illumiSum = 0;
                for (int i = 0; i < logThreshold; i++) {
                    illumiSum += illumiLog.get((illumiLog.size() - 1) - i);
                }
                illumiAve = (illumiSum / logThreshold);
                if (Math.abs(illumiLog.get(illumiLog.size() - 1) - illumiLog.get(illumiLog.size() - 2)) < illumiAve * illumiThreshold) {
                    check++;
                } else {
                    check = 0;
                }
                if (check > logThreshold) {
                    step = 4;
                    end = timeDataLog.size() - logThreshold * 3;
                    end = judgeEnd();
                    //ジェスチャの判定
                    //gestureAnser = judgeGesture(illumiLog, timeDataLog, start, end);
                    Double aveLux = (illumiLog.get(start) + illumiLog.get(end)) / 2;
                    gesture = extractFeature(aveLux, timeDataLog, illumiLog);
                    testText.setText(gesture);
                    //result.setText(gestureAnser);
                    //timeStamp.setText("" + timeDataLog.get(start));
                    //timeStamp.setText(testMessage);
//                    illumiAndTimeData += gestureAnser;
//                    for(int i=start;i<=end;i++){
//                        illumiAndTimeData+=","+illumiLog.get(i);
//                    }
//                    illumiAndTimeData += "\n";
                    //sendFile(gestureAnser, illumiAndTimeData);
                    //String serveTime = getNowTime();
                    //onServe(currentTimeDataLog.get(start) + "," + gestureAnser +"," + imageID+","+testMessage);
                    //onServe(currentTimeDataLog.get(start) + "," + gestureAnser);
                }
                break;
            //step4. step2に戻るために諸々頑張る
            case 4:
                state.setText("step 4");
                double[] illumiLogBuf = new double[logThreshold];
                long[] timeDataLogBuf = new long[logThreshold];
                for (int i = 0; i < logThreshold; i++) {
                    illumiLogBuf[i] = illumiLog.get(illumiLog.size() - logThreshold + i);
                    timeDataLogBuf[i] = timeDataLog.get(timeDataLog.size() - logThreshold + i);
                }
                illumiLog = new ArrayList<Double>();
                timeDataLog = new ArrayList<Long>();

                for (int i = 0; i < logThreshold; i++) {
                    illumiLog.add(illumiLogBuf[i]);
                    timeDataLog.add(timeDataLogBuf[i]);
                }
                illumiSum = 0;
                for (int i = 0; i < logThreshold; i++) {
                    illumiSum += illumiLog.get((illumiLog.size() - 1) - i);
                }
                illumiAve = (illumiSum / logThreshold);
                if (Math.abs(illumiAve - illumiLog.get(illumiLog.size() - 1)) < (illumiAve * illumiThreshold)) {
                    check++;
                } else {
                    check = 0;
                }
                if (check > logThreshold) {
                    step = 2;
                    check = 0;
                    //result.setText("Ready");
                }
                break;

        }


    }

    //end特化型認識
    public int judgeEnd() {
        int endPoint = end;
        double startIllumi = illumiLog.get(start);
        for (int i = endPoint; i > 0; i--) {
            double diff = Math.abs(startIllumi - illumiLog.get(i));
            if (diff > (double) startIllumi * 0.05) {
                endPoint = i + 1;
                return endPoint;
            }
        }
        return endPoint;
    }

    //波特化型認識
    public static double judgeWaveNum(ArrayList<Double> illumiLog, int start, int end, double max) {
        int waveFlag = 0;
        double lastDiff = 0.0;
        ArrayList<Double> illumiMountainLog = new ArrayList<Double>();
        illumiMountainLog.add(max);
        for (int i = start; i <= end - 1; i++) {
            //System.out.println(illumiLog.get(i+1)+"-"+illumiLog.get(i));
            double diff = illumiLog.get(i + 1) - illumiLog.get(i);
            if (Math.abs(diff) == 0 || diff * lastDiff < 0) {
                illumiMountainLog.add(illumiLog.get(i));
                //System.out.println(illumiLog.get(i));
            }
            lastDiff = diff;
        }
        illumiMountainLog.add(max);

        if (illumiMountainLog.size() == 2) {
            return 0;
        }

        ArrayList<Double> illumiMountainClusterLog = new ArrayList<Double>();
        illumiMountainClusterLog.add(illumiMountainLog.get(0));
        for (int i = 1; i < illumiMountainLog.size(); i++) {
            double diff = illumiMountainLog.get(i - 1) - illumiMountainLog.get(i);
            if (Math.abs(diff) > max * 0.2) {
                illumiMountainClusterLog.add(illumiMountainLog.get(i));
            }
        }

//      System.out.println(illumiMountainLog);
//      System.out.println(illumiMountainClusterLog);

        double lastIllumiMountainCluster = illumiMountainClusterLog.get(0);
        double lastIllumiDiff = 0.0;

        for (int i = 1; i < illumiMountainClusterLog.size(); i++) {
            double illumiDiff = illumiMountainClusterLog.get(i) - lastIllumiMountainCluster;
            //System.out.println(illumiDiff+","+lastIllumiDiff);
            if (illumiDiff * lastIllumiDiff < 0) {
                waveFlag++;
                //System.out.println("wave");
            }

//          if(Math.abs(illumiDiff)>(double)max*0.2){
//              waveFlag++;
//              System.out.println("diff:"+illumiDiff);
//          }
            lastIllumiMountainCluster = illumiMountainLog.get(i);
            lastIllumiDiff = illumiDiff;
        }
        return (waveFlag + 1.0) / 2.0;
    }

    //特徴点抽出
    public String extractFeature(Double aveLux, ArrayList<Long> nanoTime, ArrayList<Double> illumiLog) {
        String result = "";
        String gesture = "";
        int startPoint = 0;
        int endPoint = illumiLog.size() - 1;
        int maxPoint = 0;
        int minPoint = 0;

        double waveCount = 0.0;
        double totalWidth = 0.0;
        double tiltAve = 0.0;
        double deepness = 0.0;

        //どれだけ変化したらstartあるいはendとみなすかの閾値
        double threshold = aveLux * 0.05;


        //lux(ArrayList<String>) >> illumiLog(ArrayList(Double))
//        ArrayList<Double> illumiLog = new ArrayList<Double>();
//        for(int i = 0;i<lux.size();i++){
//            //System.out.println(lux.get(i));
//            illumiLog.add(Double.parseDouble(lux.get(i)));
//        }

        //start探し
        for (int i = 1; i < illumiLog.size(); i++) {
            if (Math.abs(illumiLog.get(i - 1) - illumiLog.get(i)) > threshold) {
                startPoint = i - 1;
                break;
            }
        }
        //end探し
        for (int i = illumiLog.size() - 2; i >= 0; i--) {
            if (Math.abs(illumiLog.get(i) - illumiLog.get(i + 1)) > threshold) {
                endPoint = i + 1;
                break;
            }
        }

        //max探し
        double max = 0.0;
        for (int i = 0; i < illumiLog.size(); i++) {
            if (illumiLog.get(i) > max) {
                maxPoint = i;
                max = illumiLog.get(i);
            }
        }
        //max探し
        double min = aveLux;
        for (int i = 0; i < illumiLog.size(); i++) {
            if (illumiLog.get(i) < min) {
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
        totalWidth = (nanoTime.get(endPoint).doubleValue() - nanoTime.get(startPoint).doubleValue()) / 1000000.0;

        //tiltAve
        //double ts = (Double.parseDouble(nanoTime.get(minPoint)) - Double.parseDouble(nanoTime.get(startPoint)))/1000000.0;
        //double te = (Double.parseDouble(nanoTime.get(endPoint)) - Double.parseDouble(nanoTime.get(minPoint)))/1000000.0;
        //double A  = illumiLog.get(maxPoint) - illumiLog.get(minPoint);
        double ts = (nanoTime.get(minPoint).doubleValue() - nanoTime.get(startPoint).doubleValue()) / 1000000.0;
        double te = (nanoTime.get(endPoint).doubleValue() - nanoTime.get(minPoint).doubleValue()) / 1000000.0;
        double A = illumiLog.get(maxPoint) - illumiLog.get(minPoint);
        tiltAve = A / ts - A / te;

        //deepness
        deepness = A / illumiLog.get(maxPoint);


        //System.out.println(illumiLog.get(startPoint)+","+illumiLog.get(endPoint));
        result = waveCount + "," + totalWidth + "," + tiltAve + "," + deepness;
        //System.out.println(result);
        //receivedMessage.setText(result);

        if (waveCount >= 2.5) {
            gesture = "roll";
        } else if (deepness > 0.92) {
            gesture = "hide";
        } else if (totalWidth < 425) {
            gesture = "slash";
        } else if (tiltAve > 0.13) {
            gesture = "up";
        } else {
            gesture = "down";
        }

        start = startPoint;
        end = endPoint;

        return gesture;
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

    public String getDeviceCharByUDID(String udid) {
        if (udid.equals("a4c7b9190b6bd931")) {
            //return "nexus7-2012-hmurakami";
            return "A";
        } else if (udid.equals("c58ce7becdb6013")) {
            //return "nexus7-2012-tshimakawa";
            return "B";
        } else if (udid.equals("8e9e784548c0cb6a")) {
            //return "nexus7-2013-haida";
            return "C";
        } else if (udid.equals("f7196b5116fe5f4d")) {
            //return "nexus7-2013-amiyoshi";
            return "D";
        }
        else{
            return "X";
        }
    }
}
