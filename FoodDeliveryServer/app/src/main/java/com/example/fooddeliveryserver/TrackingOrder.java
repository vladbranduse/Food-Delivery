package com.example.fooddeliveryserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fooddeliveryserver.Model.Common;
import com.example.fooddeliveryserver.Remote.IGeoCoordinates;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fooddeliveryserver.databinding.ActivityTrackingOrderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
        {

    private GoogleMap mMap;
    private ActivityTrackingOrderBinding binding;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST=1000;
    private final static int LOCATION_PERMISSION_REQUEST=1001;

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL=1000;
    private static int FATEST_INTERVAL=5000;
    private static int DISPLACEMENT=10;

    private IGeoCoordinates mServics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrackingOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mServics = Common.getGeoCodeServce();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestRuntimePermission();
        }
        else{
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
        
        displayLocation();
        
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

            private void displayLocation() {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    requestRuntimePermission();
                }
                else
                {
                   mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(mLastLocation != null)
                    {
                        double latitude = mLastLocation.getLatitude();
                        double longitude = mLastLocation.getLongitude();


                        LatLng yourLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(yourLocation).title("Locatia ta"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                       // mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                       drawRoute(yourLocation, Common.currentRequest.getAddress());
                    }
                    else
                    {
                       Toast.makeText(this, "Locatia nu a putut fi gasita!", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            private void drawRoute(LatLng yourLocation, String address) {
                mServics.getGeoCode(address).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try{
                            JSONObject jsonObject = new JSONObject (response.body ().toString());
                            String lat = ((JSONArray) jsonObject.get ("results"))
                                    .getJSONObject (0)
                                    .getJSONObject ("geometry")
                                    .getJSONObject ("location")
                                    .get ("lat").toString();
                            String lng = ((JSONArray) jsonObject.get ("results"))
                                    .getJSONObject (0)
                                    .getJSONObject ("geometry")
                                    .getJSONObject ("location")
                                    .get ("lng").toString();

                            LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));


                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cutie);
                            bitmap = Common.scaleBitmap(bitmap, 70, 70);

                            MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                    .title("Comanda pentru "+Common.currentRequest.getPhone())
                                    .position(orderLocation);
                            mMap.addMarker(marker);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }

            private void createLocationRequest() {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FATEST_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

            }

            protected synchronized void buildGoogleApiClient() {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API).build();
                mGoogleApiClient.connect();
            }

            private boolean checkPlayServices() {
                int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if(resultCode != ConnectionResult.SUCCESS)
                {
                    if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                    {
                        GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                    }
                    else
                    {
                        Toast.makeText(this, "Acest device nu este suportat", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return false;
                }
                return true;
            }

            private void requestRuntimePermission() {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },LOCATION_PERMISSION_REQUEST);
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                switch (requestCode)
                {
                    case LOCATION_PERMISSION_REQUEST:
                        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        {
                            if(checkPlayServices())
                            {
                                buildGoogleApiClient();
                                createLocationRequest();

                                displayLocation();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

            }

            @Override
            public void onLocationChanged(@NonNull Location location) {
                mLastLocation = location;
                displayLocation();
            }

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                displayLocation();
                startLocationUpdates();
            }

            private void startLocationUpdates() {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
            }

            @Override
            public void onConnectionSuspended(int i) {
                mGoogleApiClient.connect();
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            protected void onResume() {
                super.onResume();
                checkPlayServices();
            }

            @Override
            protected void onStart() {
                super.onStart();
                if(mGoogleApiClient != null)
                    mGoogleApiClient.connect();
            }
        }