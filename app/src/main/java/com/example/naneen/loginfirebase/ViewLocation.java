package com.example.naneen.loginfirebase;

import android.Manifest;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Permission;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ViewLocation extends AppCompatActivity {
    private Button getLocation, stopGPS;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private int counter = 1;
    GPSTracker gps;

    //thread
    Handler h = new Handler();
    Thread task;
    private long startTime;
    private String timeString;
    private TextView timerText;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("LocationService");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLocation = (Button) findViewById(R.id.getLocation);
        stopGPS = (Button) findViewById(R.id.stopGPS);
        timerText = (TextView) findViewById(R.id.timerText);

        stopGPS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopHandlerTask();
                timerText.setText("Location Service is stopped");
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startTimer();
                counter = 1;
            }
        });

    }

    private void updateGPS() {
        gps = new GPSTracker(ViewLocation.this);

        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Toast.makeText(getApplicationContext(), counter + "\nCurrent location is \n Lat:" + latitude + "\nLong:" + longitude,
                    Toast.LENGTH_SHORT).show();
            myRef.child("Latitude").setValue(latitude);
            myRef.child("longitude").setValue(longitude);
            myRef.child("counter").setValue(counter);
            counter++;
        }
        else {
            gps.showSettingsAlert();
        }
    }

    private void stopHandlerTask() {
        h.removeCallbacks(task);
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        task = new Thread() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                long secs = millis / 1000 % 60; //second, 0-59
                if(secs % 5 == 0) {
                    updateGPS();
                }

                timeString = String.format("%02d", secs);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerText.setText(timeString);
                    }
                });
                h.postDelayed(task, 1000);
            }
        };
        h.postDelayed(task, 1000);
    }
}