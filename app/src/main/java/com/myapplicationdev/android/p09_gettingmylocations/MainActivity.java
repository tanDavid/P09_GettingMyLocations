package com.myapplicationdev.android.p09_gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong;
    Button btnUpdate, btnRemove, btnCheck;
    private GoogleMap map;
    double startingLat, startingLong;
    String folderLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnRemove = findViewById(R.id.btnRemove);
        btnCheck = findViewById(R.id.btnCheck);

        checkPermission();
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){

                    startingLat = location.getLatitude();
                    startingLong = location.getLongitude();
                    Log.d("TAG", startingLat + "");
                    tvLat.setText("Latitude: " + startingLat);
                    tvLong.setText("Longitude: " + startingLong);

                }else{
                    String msg = "No Last Known Location Found";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    Log.d("TAG", startingLat + "");
                }
            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                LatLng poi_SG = new LatLng(startingLat, startingLong);
                Marker central = map.addMarker(new MarkerOptions().position(poi_SG).title("Central").snippet("Current").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_SG, 11));

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);
                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Location data = locationResult.getLastLocation();
                            double lat = data.getLatitude();
                            double lng = data.getLongitude();
                            startingLat = lat;
                            startingLong = lng;
                            tvLat.setText("Latitude: " + startingLat);
                            tvLong.setText("Longitude: " + startingLong);


                            folderLocation = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/Folder";
                            File folder_I = new File(folderLocation);
                            if (folder_I.exists() == false) {
                                boolean result = folder_I.mkdir();
                                if (result == true) {
                                    Log.d("File Read/Write", "Folder created");
                                }else{
                                    Log.d("File Read/Write", "Folder failed created");
                                }
                            }


                            try {
                                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
                                File targetFile = new File(folderLocation, "ProblemStatementData.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write("Latitude" + startingLat + "\n" + "Longitude" + startingLong);
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                        }
                    };
                };
                checkPermission();
                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            }
        });

    }
    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);



        if(permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkPermission2(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);



        if(permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }else{
            String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(MainActivity.this, permission, 0);
            return false;
        }
    }


}