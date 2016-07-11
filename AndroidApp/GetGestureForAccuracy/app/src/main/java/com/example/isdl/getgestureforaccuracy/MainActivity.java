package com.example.isdl.getgestureforaccuracy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private int onoff;
    private String macAddress;
    private String deviceName;
    private SensorManager manager;
    private String gesture;

    ArrayList<String> illumiLog = new ArrayList< >();
    ArrayList<String> timeDataLog  = new ArrayList< >();
    ArrayList<String> nanotimeDataLog  = new ArrayList< >();

    //IP�A�h���X�̎w��
    private final static String IP = "172.20.11.176";
    private final static int PORT = 8080;

    private Socket socket; //�\�P�b�g
    private InputStream in;     //���̓X�g���[��
    private OutputStream out;    //�o�̓X�g���[��
    private boolean error;  //�G���[

    private final Handler handler = new Handler();//�n���h��

    private String resieveMessage;
    private String sendMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        macAddress = wifiInfo.getMacAddress();
        deviceName = getDeviceName(macAddress);

        //�{�^���ƃe�L�X�g�̐ݒ�
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

    }

    @Override
    public void onStart() {
        super.onStart();

        //�X���b�h�̐���
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

    //�A�N�e�B�r�e�B�̒�~���ɌĂ΂��
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
                    sendMessage = "";
                    for(int i = 0;i<timeDataLog.size();i++){
                        sendMessage += timeDataLog.get(i)+","+nanotimeDataLog.get(i)+","+illumiLog.get(i)+";";
                    }
                    onServe(sendMessage);
                    //reset log
                    illumiLog = new ArrayList<String>();
                    timeDataLog  = new ArrayList<String>();
                    nanotimeDataLog  = new ArrayList<String>();

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

            if(view == delete){
                //reset log
                gesture = "";
                illumiLog = new ArrayList<String>();
                timeDataLog  = new ArrayList<String>();
                nanotimeDataLog  = new ArrayList<String>();
            }
            else{
                state.setText(gesture);
                illumiLog.add(gesture);
                timeDataLog.add(gesture);
                nanotimeDataLog.add(gesture);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // �Z���T�[�̐��x���ύX�����ƌĂ΂��
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

                illumiLog.add(String.valueOf(lx));
                timeDataLog.add(String.valueOf(millistime));
                nanotimeDataLog.add(String.valueOf(nanotime));
            }
        }
    }

    //�ڑ�
    private void connect(String ip, int port) {
        int size;
        String strBuf = "";
        byte[] w = new byte[1024];
        try {
            //�\�P�b�g�ڑ�
            //addText("�ڑ���");
            //connectState.setText("�ڑ���");
            //state.setText("connect now");
            socket = new Socket(ip, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            //addText("�ڑ�����");
            //connectState.setText("�ڑ�����");
            //state.setText("connect end");

            while (socket != null && socket.isConnected()) {
                //�f�[�^�̎�M
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
                            //�l�X�ȏ���
                            //someProcess(finalStrBuf);

                        } else {
                            //addText("�ʐM���s���܂���");
                            //connectState.setText("�ʐM���s���܂���2");
                        }
                    }
                });
                //���x���ւ̕�����ǉ�
                //connectState.setText("��M");
                //receive.setText(""+str);
            }
        } catch (Exception e) {
            //addText("�ʐM���s���܂���");
            //connectState.setText("�ʐM���s���܂���\n"+e);
            state.setText("connect fault");
            Log.e("error", String.valueOf(e));
        }
    }

    public void onServe(final String anser) {
        //�X���b�b�h�̐���
        Thread thread = new Thread(new Runnable() {
            public void run() {
                error = false;
                try {
                    //�f�[�^�̑��M
                    if (socket != null && socket.isConnected()) {
                        String serveTime = getNowTime();
                        String write = serveTime +","+ deviceName+";"+anser;
                        byte[] w = write.getBytes("UTF8");
                        out.write(w);
                        out.flush();
                    }
                } catch (Exception e) {
                    error = true;
                }
                //�n���h���̐���
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!error) {
                        } else {
                            //addText("�ʐM���s���܂���");
                            //connectState.setText("�ʐM���s���܂���2");
                        }
                    }
                });
            }
        });
        thread.start();
    }

    //�ؒf
    private void disconnect() {
        try {
            //onServe("see u");
            socket.close();
            socket = null;
        } catch (Exception e) {
        }
    }

    //--------------�ڑ��v���O�����I���

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

    public static String getNowTime() {
        // �����擾
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
