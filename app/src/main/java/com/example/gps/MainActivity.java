package com.example.gps;
import android.Manifest;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gps.base.Base;
import com.example.gps.MyLocationProvider;

public class MainActivity extends Base
        implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;
    MapView mapView ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView=findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if(isLocationPermissionGranted()){//is permission granted
            //call your function
            getUserLocation();
        }else {//request permission
            requestLocationPermission();
        }
    }

    GoogleMap googleMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        drawUserLocation();
    }
    Marker marker;
    public void drawUserLocation(){
        if(myLoaction==null||googleMap==null)return;
        if(marker==null)
            marker = googleMap.addMarker(new MarkerOptions()
                    .title("i'm here")
                    .position(new LatLng(myLoaction.getLatitude(),myLoaction.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background)));

        marker.setPosition(new LatLng(myLoaction.getLatitude(),myLoaction.getLongitude()));
        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(myLoaction.getLatitude(), myLoaction.getLongitude()),15));
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    Location myLoaction =null;
    MyLocationProvider locationProvider;
    public void getUserLocation(){
        if(locationProvider==null)
            locationProvider=new MyLocationProvider(this);
        myLoaction = locationProvider.getCurrentLocaion(
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        myLoaction =location;
                        drawUserLocation();
                        Toast.makeText(MainActivity.this,
                                ""+
                                        location.getLatitude() + " "+
                                        location.getLongitude(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }


                }
        );
    }
    public boolean isLocationPermissionGranted(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    public void requestLocationPermission(){
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showMessage("app wants to access location to find nearby cafes",
                    "ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    },true);
        }
        else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getUserLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "cannot access user Location", Toast.LENGTH_SHORT).show();

                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
