package com.mostdanger.realtimetracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    private EditText editText_Latitude;
    private EditText editText_Longitude;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        editText_Latitude = findViewById(R.id.editText);
        editText_Longitude = findViewById(R.id.editText2);
        button = findViewById(R.id.button);
        databaseReference = FirebaseDatabase.getInstance().getReference("Location");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String dbLatitude=dataSnapshot.child("latitude").getValue().toString().substring(1,dataSnapshot.child("latitude").getValue().toString().length()-1);
                    String dbLongitude=dataSnapshot.child("longitude").getValue().toString().substring(1,dataSnapshot.child("longitude").getValue().toString().length()-1);
                    String[] stringLat=dbLatitude.split(", ");
                    Arrays.sort(stringLat);
                    String latitude = stringLat[stringLat.length-1].split("=")[1];

                    String[] stringLon=dbLongitude.split(", ");
                    Arrays.sort(stringLon);
                    String longitude = stringLon[stringLon.length-1].split("=")[1];

                    LatLng latLng= new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(latitude+" , "+longitude));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("latitude").push().setValue(Double.parseDouble(String.valueOf(editText_Latitude.getText())));
                //  databaseReference.child("longitude").push().setValue(editText_Longitude.getText());
                databaseReference.child("longitude").push().setValue(Double.parseDouble(String.valueOf(editText_Longitude.getText())));
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    editText_Latitude.setText(Double.toString(location.getLatitude()));
                    editText_Longitude.setText(Double.toString(location.getLongitude()));

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updateButtonOnclick(View view){
     //   databaseReference.child("latitude").push().setValue(editText_Latitude.getText());
        databaseReference.child("latitude").push().setValue(43.1548);
      //  databaseReference.child("longitude").push().setValue(editText_Longitude.getText());
        databaseReference.child("longitude").push().setValue(5.1648);

    }
}
