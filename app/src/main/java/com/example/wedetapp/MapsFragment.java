package com.example.wedetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapsFragment extends Fragment implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Location lastLocation;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private GoogleMap gMap;
    EditText editText;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap=googleMap;
if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
    if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
        buildGoogleApi();
        gMap.setMyLocationEnabled(true);
    }
}else{
    gMap.setMyLocationEnabled(true);
    buildGoogleApi();
}
        }
    };
     private synchronized void buildGoogleApi(){
         googleApiClient=new GoogleApiClient.Builder(getActivity())
                 .addOnConnectionFailedListener(this)
                 .addConnectionCallbacks(this)
                 .addApi(LocationServices.API).build();
                  googleApiClient.connect();

     }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
      locationRequest =new LocationRequest();
      locationRequest.setInterval(100);
      locationRequest.setFastestInterval(100);
      locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
      if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
          LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
      }
    }
    @Override
    public void onLocationChanged(Location location) {
         lastLocation=location;
         LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
         gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
         if (googleApiClient!=null){
             LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
         }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    public void SearchLocation(View view) {

    }
}