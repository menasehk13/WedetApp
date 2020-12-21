package com.example.wedetapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.getbase.floatingactionbutton.FloatingActionButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment {
    private GoogleMap gmap;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLang = 0;
    SupportMapFragment mapFragment;
    FloatingActionButton search;
    MapRipple ripple;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        search=view.findViewById(R.id.searchHotel);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapRipple ripple=new MapRipple(gmap,new LatLng(currentLat,currentLang),getContext());
                ripple.withNumberOfRipples(3);
                ripple.withRippleDuration(20000);
                ripple.withTransparency(0.5f);
                ripple.withStrokewidth(500);
                ripple.withStrokeColor(Color.BLUE);
                ripple.withFillColor(Color.BLUE);
                ripple.startRippleMapAnimation();
                String nearbyplace="restaurant";
                      String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                              "?location=" +currentLat + "," + currentLang + "&radius=500000"+
                              "&types="+ nearbyplace +"&sensor=true"
                              + "&key="+ getResources().getString(R.string.google_api_key);
                      new PlaceTask().execute(url);
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            gmap.setMyLocationEnabled(true);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLang = location.getLongitude();
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            googleMap.setMyLocationEnabled(true);
                         gmap=googleMap;
                         gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLang),15));
                     }
                 });
             }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode==44){
          if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
              getCurrentLocation();
          }
      }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data=null;
            try {
                data= downloadUrl(strings[0]);
            } catch (IOException e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
         new ParserTask().execute(s);

        }
    }

    private String downloadUrl(String string) throws IOException{

        URL url=new URL(string);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream inputStream=connection.getInputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder=new StringBuilder();
        String line="";
        while ((line=reader.readLine())!=null){
            builder.append(line);
        }
        String data=builder.toString();
        reader.close();
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer,List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser=new JsonParser();
            List<HashMap<String,String>> maplist=null;
            JSONObject jsonObject=null;
            try {
               jsonObject =new JSONObject(strings[0]);
                maplist=jsonParser.parseResult(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return maplist;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            gmap.clear();
            for (int i=0;i<hashMaps.size();i++){
                HashMap<String,String> hashMap=hashMaps.get(i);
                double lat= Double.parseDouble(hashMap.get("lat"));
                double lng= Double.parseDouble(hashMap.get("lng"));
                String name=hashMap.get("name");
                LatLng latLng=new LatLng(lat,lng);
                MarkerOptions options=new MarkerOptions();
                options.position(latLng);
                options.title(name);
                gmap.addMarker(options);

            }
        }
    }

}