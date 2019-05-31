package com.example.modelctrackpad;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Sensor mySensor;
    private SensorManager SM;
    public static String wifiModuleIp = "192.168.0.100";
    public static int MotorPort = 21567;
    public static float fx = 0;
    public static float fy = 0;
    public static String CMD = "0,0,0";
    public static float a = 0;
    public static float b = 0;
    public static float c = 0;
    public static float fz = 0;
    public static float L;
    public static float R;
    public static float V;
    public static float ax;
    public static float az;
    public static float ay;
    public static float atot;
    public static double tilt=125;

    public static String Result;
    public static Switch start;
    public static Switch CamStart;
    public static String Thrust;
    WebView webView;
    ImageView image;

    SeekBar seekbar;
    TextView textView;
    public static int Vert = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //i think tells the phone youre gonna do stuff without the title
        getSupportActionBar().hide(); //hides the title bar
        setContentView(com.example.modelctrackpad.R.layout.activity_main);


        //setContentView(com.example.modelctrackpad.R.layout.activity_main);
        webView = findViewById(com.example.modelctrackpad.R.id.WebView);
        webView.loadUrl("http://192.168.0.100:8000/stream.mjpg");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(false);
        seekbar = findViewById(com.example.modelctrackpad.R.id.seekBar);
        textView = findViewById(com.example.modelctrackpad.R.id.textView);
        start=findViewById(com.example.modelctrackpad.R.id.switch1);
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        CamStart =findViewById(com.example.modelctrackpad.R.id.CamSwitch);

        //accelerometer sensor:
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensor listener:
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        seekbar.setMax(200);
        seekbar.setProgress(Vert);

        start = findViewById(com.example.modelctrackpad.R.id.switch1);
        addTouchListener();


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Vert = i;
                Socket_AsyncTask cmd_Change_Servo = new Socket_AsyncTask();
                cmd_Change_Servo.execute();
                textView.setText(makeCommands());


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*Vert = 100;
                textView.setText(makeCommands());
                seekbar.setProgress(Vert);
                textView.setText(makeCommands()); */

            }

        });
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use

    }
    public static String makeCommands() {
        a = ((fx-250)/250)*100;
        b = ((250-fy)/250)*100;
        c = Vert-100;
        if (a>50){
            a=50;
        }
        if (a<-50){
            a=-50;
        }
        if (b>60){
            a=a/2;
        }


        L = a+b;
        R = b-a;
        V = c;


        if (L>100f){
            L = 100f;
        }
        if(L<-100f){
            L = -100f;
        }
        if(R>100f){
            R = 100f;
        }
        if(R<-100f){
            R = -100f;
        }

        if(fz<0){
            L = -100f;
            R = -100f;

        }
        atot = (az*az)+(ax*ax)+(ay*ay);




        if (CamStart.isChecked()){
            tilt =  Math.round(Math.acos((az/Math.sqrt(atot)))*180/3.14)+35;

        }else{
            tilt=tilt;
        }


        L = Math.round(L/10)*10;
        R = Math.round(R/10)*10;
        V = Math.round(V/10)*10;

        if(start.isChecked()){
            Thrust = String.valueOf(L) + "," + String.valueOf(R) + "," + String.valueOf(V);
        }else{
            Thrust = "0,0,0";
        }
        Result = Thrust+","+String.valueOf(tilt);
        return Result;
    }
    @Override
    public void onSensorChanged(SensorEvent event){

        ax = event.values[1];
        ay = event.values[0];
        az = event.values[2];
        textView.setText(makeCommands());
        Socket_AsyncTask cmd_Change_Servo = new Socket_AsyncTask();
        cmd_Change_Servo.execute();

    }

    public void addTouchListener() {
        image = findViewById(com.example.modelctrackpad.R.id.imageView);

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                fx = 0;
                fx = event.getX();
                fy = 0;
                fy = event.getY();
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_UP:
                        fx=250;
                        fy=250;

                }

                Socket_AsyncTask cmd_Change_Servo = new Socket_AsyncTask();
                cmd_Change_Servo.execute();
                textView.setText(makeCommands());
                return true;
            }
        });
    }



    public static class Socket_AsyncTask extends AsyncTask<Void,Void,Void>
    {
        Socket socket;
        @Override
        protected Void doInBackground(Void... params){
            try{
                InetAddress inetAddress = InetAddress.getByName(MainActivity.wifiModuleIp);
                socket = new java.net.Socket(inetAddress,MainActivity.MotorPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeBytes(makeCommands());
                //dataOutputStream.write(TestInt); //sends a number pls
                dataOutputStream.close();
                socket.close();
            }catch (UnknownHostException e){e.printStackTrace();}catch (IOException e){e.printStackTrace();}
            return null;
        }
    }


}
