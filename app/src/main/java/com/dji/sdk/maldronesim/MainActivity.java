package com.dji.sdk.maldronesim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {
    TSPI mTSPI;
    TextView logs;
    FlightController flightController;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTSPI = new TSPI();
        mContext = getApplicationContext();
        logs = (TextView) findViewById(R.id.view_log);
        initFlightControllerState();
    }
    public void writeLogfile(Context context, String filename, String content){
        String data = content;
        FileOutputStream outputStream;
        try{
            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.d("filewrite","success");
        }catch(IOException e){
            Log.d("filewrite","failed");
            e.printStackTrace();
        }
    }
    private void initFlightControllerState(){
        String fileName = ("FlightLog: " + Calendar.getInstance().getTime()+".csv");

        Log.d("FlightControllerState", "connecting FlightController");

        try {
            flightController = ((Aircraft) DJISDKManager.getInstance().getProduct()).getFlightController();
        } catch (Exception e) {
            Log.d("FlightControllerState","not Connected");
        }

        if(flightController == null){
            Log.d("FlightControllerState","not Connected");
        } else {
            flightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(@NonNull FlightControllerState djiFlightControllerCurrentState) {

                    LocationCoordinate3D locationCoordinate3D = djiFlightControllerCurrentState.getAircraftLocation();
                    Attitude attitude = djiFlightControllerCurrentState.getAttitude();

                    mTSPI.setTimestamp(Calendar.getInstance().getTime());
                    mTSPI.setGpsSignalStrength(String.valueOf(djiFlightControllerCurrentState.getGPSSignalLevel()));
                    mTSPI.setSatelliteCount(djiFlightControllerCurrentState.getSatelliteCount());
                    mTSPI.setCurrentLatitude(locationCoordinate3D.getLatitude());
                    mTSPI.setCurrentLongitude(locationCoordinate3D.getLongitude());

                    mTSPI.setCurrentAltitude(locationCoordinate3D.getAltitude());

                    mTSPI.setPitch(attitude.pitch);
                    mTSPI.setYaw(attitude.yaw);

                    //logs.setText(mTSPI.logResults());
                    writeLogfile(mContext,fileName,mTSPI.logResults());
                    // 파이어베이스 업로드
                    Log.d("(Thread)TSPILogger", "hello from logger");

                }
            });
        }
    }
}