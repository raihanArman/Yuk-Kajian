package id.co.myproject.yukkajian.view.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import id.co.myproject.yukkajian.R;
import id.co.myproject.yukkajian.model.Kajian;
import id.co.myproject.yukkajian.request.ApiRequest;
import id.co.myproject.yukkajian.request.RetrofitRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LokasiActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private FloatingActionButton fbBack;
    ApiRequest apiRequest;
    Kajian kajian;
    int typeIntent;
    public static final int INTENT_LOKASI_KAJIAN = 987;
    public static final int INTENT_LOKASI_KAJIAN_HARI_INI = 687;
    public static final int MY_PERMISSION_REQUEST_CODE = 700;
    public static final int PLAY_SERVICE_REQUEST_CODE = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    Marker mCurrentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
        typeIntent = getIntent().getIntExtra("type_intent", 0);
        fbBack = findViewById(R.id.fb_back);

        fbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setUpLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        }else {
            if (checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null){
            if (typeIntent == INTENT_LOKASI_KAJIAN) {
                kajian = getIntent().getParcelableExtra("kajian");
                String[] lokasi = kajian.getLatlng().split("@");
                String lat = lokasi[0];
                String lang = lokasi[1];

                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();

                if (mCurrentMarker != null) {
                    mCurrentMarker.remove();
                }
                mCurrentMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Your Location"));
                double latitudeKajian = Double.parseDouble(lat);
                double longtitudeKajian = Double.parseDouble(lang);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitudeKajian, longtitudeKajian))
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                        .title(kajian.getJudul_kajian()));
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Lokasi anda saat ini"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeKajian, longtitudeKajian), 15.0f));
            }else {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();
                if (mCurrentMarker != null){
                    mCurrentMarker.remove();
                }
                mCurrentMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Your Location"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String time1 = " 00:00:00";
                String time2 = " 23:59:00";
                String tanggal1 = date + time1;
                String tanggal2 = date + time2;
                Call<List<Kajian>> callKajianByDay = apiRequest.kajianByDayRequest(tanggal1, tanggal2);
                callKajianByDay.enqueue(new Callback<List<Kajian>>() {
                    @Override
                    public void onResponse(Call<List<Kajian>> call, Response<List<Kajian>> response) {
                        List<Kajian> kajianList = response.body();
                        for (Kajian kajian : kajianList){
                            String[] lokasi = kajian.getLatlng().split("@");
                            String lat = lokasi[0];
                            String lang = lokasi[1];
                            double latitudeKajian = Double.parseDouble(lat);
                            double longtitudeKajian = Double.parseDouble(lang);
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitudeKajian, longtitudeKajian))
                                    .flat(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                                    .title(kajian.getJudul_kajian()));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Kajian>> call, Throwable t) {

                    }
                });

            }
        }else {
            Log.d("ERROR", "displayLocation: Cannot get your location");
        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_REQUEST_CODE);
            }else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

}
