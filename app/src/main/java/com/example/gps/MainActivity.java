package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationupdate, sw_gps;
    boolean updateOn = false;

    LocationRequest locationRequest;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat= findViewById(R.id.tv_lat);
        tv_lon= findViewById(R.id.tv_lon);
        tv_altitude= findViewById(R.id.tv_altitude);
        tv_accuracy= findViewById(R.id.tv_accuracy);
        tv_speed= findViewById(R.id.tv_speed);
        tv_sensor= findViewById(R.id.tv_sensor);
        tv_updates= findViewById(R.id.tv_updates);
        tv_address= findViewById(R.id.tv_address);

        sw_locationupdate= findViewById(R.id.sw_locationsupdates);
        sw_gps= findViewById(R.id.sw_gps);
/*
        Fragment selectedFragment = null;
        selectedFragment = new reporteFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
*/
        locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);


        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sw_gps.isChecked()){
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensor");
                }else{
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + Wifi");

                    openLogin();
                }/*
                Fragment selectedFragment = null;
                selectedFragment = new loginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();*/

            }
        });

        updateGPS();
    }

    public void openLogin(){
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }else{
                    Toast.makeText(this, "esta app requiere permiso para trabajar correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIvalues(location);
                }
            });
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIvalues(Location location){


        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }else{
            tv_altitude.setText("no hay");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }else{
            tv_speed.setText("no hay");
        }
    }
}