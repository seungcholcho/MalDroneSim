package com.dji.sdk.maldronesim;

import static java.lang.Double.isNaN;
import static dji.log.GlobalConfig.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {
    TSPI mTSPI;
    FlightController flightController;
    String textview;

    private TextView currentTime;
    private TextView longitude;
    private TextView latitude;
    private TextView altitude_seaTodrone;
    private TextView altitude;
    private TextView databaseState;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        mTSPI = new TSPI();
        getFlightControllerState();

        //To write Log file
        mContext = getApplicationContext();
    }

    private void initUI() {

        databaseState = findViewById(R.id.text_DB_connect);
        currentTime = findViewById(R.id.text_time);
        longitude = findViewById(R.id.text_longditude);
        latitude = findViewById(R.id.text_latitude);
        altitude_seaTodrone = findViewById(R.id.text_altitude);
        altitude = findViewById(R.id.text_altitude_over_sea);

    }

    //Write log file
    public void writeLogfile(Context context, String filename, String content){
        String data = content;
        FileOutputStream outputStream;
//        try{
//            outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
//            outputStream.write(data.getBytes());
//            outputStream.close();
//            Log.d("filewrite","success" + filename);
//        }catch(IOException e){
//            Log.d("filewrite","failed");
//            e.printStackTrace();
//        }
    }


    //In order to use the API provided by DJI, the request is sent once every 0.1 seconds using the Callback function.
    //Return values include the connection status of the controller and TSPI data of the drone.
    private void getFlightControllerState(){
        //Get current time
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String strDate = dateFormat.format(date);
        String fileName = (strDate + ".csv");

        writeLogfile(mContext,fileName,mTSPI.logResults());

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

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    LocationCoordinate3D locationCoordinate3D = djiFlightControllerCurrentState.getAircraftLocation();
                    Attitude attitude = djiFlightControllerCurrentState.getAttitude();

                    Date currentDate = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");

                    mTSPI.setTimestamp(dateFormat.format(currentDate));
                    mTSPI.setGpsSignalStrength(String.valueOf(djiFlightControllerCurrentState.getGPSSignalLevel()));
                    mTSPI.setSatelliteCount(djiFlightControllerCurrentState.getSatelliteCount());
                    mTSPI.setCurrentLatitude(locationCoordinate3D.getLatitude());
                    mTSPI.setCurrentLongitude(locationCoordinate3D.getLongitude());

                    mTSPI.setCurrentAltitude(locationCoordinate3D.getAltitude());
                    mTSPI.setCurrentAltitude_seaTohome(djiFlightControllerCurrentState.getTakeoffLocationAltitude());

                    textview = "Time : " + String.valueOf(mTSPI.getTimestamp())
                                + "\nLatitude : " + String.valueOf(mTSPI.getCurrentLatitude())
                                + "\nLongitude : " + String.valueOf(mTSPI.getCurrentLongitude());

                    writeLogfile(mContext, fileName, mTSPI.logResults());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            databaseState.setText("DB conneced : Ture");
                            currentTime.setText("Current time : " + mTSPI.getTimestamp());
                            longitude.setText("Longittude : " + mTSPI.getCurrentLongitude());
                            latitude.setText("Latitude : " + mTSPI.getCurrentLatitude());
                            altitude_seaTodrone.setText("Altitude(over the sea) : " + mTSPI.getCurrentAltitude_seaTohome());
                            altitude.setText("Altitude : " + mTSPI.getCurrentAltitude());

                        }
                    });


                    //Save data as hash values
                    HashMap result = new HashMap<>();
                    result.put("Time",  mTSPI.getTimestamp());
                    result.put("GpsSignal", String.valueOf(djiFlightControllerCurrentState.getGPSSignalLevel()));
                    result.put("Altitude_seaTohome",djiFlightControllerCurrentState.getTakeoffLocationAltitude());
                    result.put("Altitude", locationCoordinate3D.getAltitude());
                    result.put("Latitude", locationCoordinate3D.getLatitude());
                    result.put("Longitude", locationCoordinate3D.getLongitude());
                    result.put("Pitch", attitude.pitch);
                    result.put("Yaw", attitude.yaw);


                    //When the latitude and longitude are not null, TSPI data is sent to the database.
                    if (!(isNaN((double)result.get("Latitude"))) && !(isNaN((double)result.get("Longitude")))){
                        db.collection("0614_test_2000_1").add(result).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                    }
                    else{
                        System.out.println("Latitude and Logitude are NaN");
                    }
                }
            });
        }
    }
}