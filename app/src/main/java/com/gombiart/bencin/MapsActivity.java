package com.gombiart.bencin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



    private GoogleMap mMap;
    public ArrayList<GasStation> gasStations = new ArrayList<>();
    AsyncTask<Void, Void, Void> parser;
    private boolean doneLoading = false;

    private View locationButton;
    private LatLng myLocation;

    private SeekBar distanceBar;
    private double maxDistance = 2000;

    private BitmapDescriptor petrolIcon;

    private TextView distanceTv;

    private Marker currentMarker;

    UiSettings uiSettings;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        petrolIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_petrol);
        initDataParser();
        distanceBar = (SeekBar) findViewById(R.id.distanceBar);
        maxDistance = distanceBar.getProgress();
        distanceBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        distanceBar.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        distanceTv = (TextView) findViewById(R.id.distanceTv);
        distanceTv.setText(distanceBar.getProgress() + " km");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Member Zoom To My Location Button
        View mapView = mapFragment.getView();
        if(mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
            locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        }

        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxDistance = progress + 10;
                distanceTv.setText((progress+10) + " km");
                System.out.println(maxDistance);
                done();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("this's landscape");

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            System.out.println("this's portrait");
        }
    }


    private void initDataParser(){
        AsyncTask<Void, Void, Void> p =  new DataParserV2(this, "http://www.petrol.si").execute();
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
        uiSettings = mMap.getUiSettings();
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
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location != null){
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

        uiSettings.setZoomControlsEnabled(true);

    }


    public void addGasStation(GasStation gs){
        gasStations.add(gs);
    }


    public void done(){
        if(mMap != null)
            mMap.clear();
        doneLoading = true;
        //Zoom to my location
        locationButton.performClick();

        for(i = 0; i < gasStations.size()-1; i++){
            double appDistance = distance(myLocation.latitude, myLocation.longitude, gasStations.get(i).getCoordinates().latitude, gasStations.get(i).getCoordinates().longitude);
            if(appDistance <= maxDistance) {
                mMap.addMarker(new MarkerOptions().position(gasStations.get(i).getCoordinates()).title(gasStations.get(i).getAddress()).icon(petrolIcon));
            }

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(final Marker marker) {
                    currentMarker = marker;
                    final View v = getLayoutInflater().inflate(R.layout.gas_station_layout, null);
                    TextView ime = (TextView) v.findViewById(R.id.imeCrpalke);
                    TextView naslov = (TextView) v.findViewById(R.id.naslovCrpalke);
                    TextView tel = (TextView) v.findViewById(R.id.telefon);
                    final TextView tv = (TextView) v.findViewById(R.id.imgSrc);
                    final ImageView cenik = (ImageView) v.findViewById(R.id.imgCene);
                    TextView status = (TextView) v.findViewById(R.id.status);

                    for(GasStation gss : gasStations){
                        if(gss.getAddress().equals(marker.getTitle())){
                            final GasStation gs = gss;
                            if(gs.getPricesImg() == null && gs.getPrices() == null) {
                                gs.fetchPrices(tv, MapsActivity.this);
                            }
                            if(gs.getPricesImg() != null && gs.getPrices() != null) {
                                tv.setVisibility(View.GONE);
                                cenik.setImageBitmap(gs.getPrices());
                                v.invalidate();

                            }
                            else if(gs.getPricesImg() == null) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("");
                                        cenik.setImageBitmap(gs.getPrices());
                                        if (currentMarker == marker) {
                                            marker.showInfoWindow();

                                        }
                                    }
                                }, 5000);
                            }

                            ime.setText(gs.getName());
                            naslov.setText(gs.getAddress());
                            tel.setText(gs.getPhoneNum());
                            if(gs.getStatus() == null){
                                status.setText("Zaprto");
                            }
                            else
                                status.setText(gs.getStatus());



                            v.invalidate();
                            break;
                        }
                    }

                    ime.invalidate();
                    naslov.invalidate();
                    tel.invalidate();
                    cenik.invalidate();
                    status.invalidate();
                    v.invalidate();

                    return v;
                   }



                @Override
                public View getInfoContents(final Marker marker) {
                    currentMarker = marker;
                    final View v = getLayoutInflater().inflate(R.layout.gas_station_layout, null);
                    TextView ime = (TextView) v.findViewById(R.id.imeCrpalke);
                    TextView naslov = (TextView) v.findViewById(R.id.naslovCrpalke);
                    TextView tel = (TextView) v.findViewById(R.id.telefon);
                    final TextView tv = (TextView) v.findViewById(R.id.imgSrc);
                    final ImageView cenik = (ImageView) v.findViewById(R.id.imgCene);
                    TextView status = (TextView) v.findViewById(R.id.status);

                    for(GasStation gss : gasStations){
                        if(gss.getAddress().equals(marker.getTitle())){
                            final GasStation gs = gss;
                            if(gs.getPricesImg() == null && gs.getPrices() == null) {
                                gs.fetchPrices(tv, MapsActivity.this);
                            }
                            if(gs.getPricesImg() != null && gs.getPrices() != null) {
                                tv.setVisibility(View.GONE);
                                cenik.setImageBitmap(gs.getPrices());
                                v.invalidate();

                            }
                            else if(gs.getPricesImg() == null) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.setText("");
                                        cenik.setImageBitmap(gs.getPrices());
                                        if (currentMarker == marker) {
                                            marker.showInfoWindow();

                                        }
                                    }
                                }, 5000);
                            }

                            ime.setText(gs.getName());
                            naslov.setText(gs.getAddress());
                            tel.setText(gs.getPhoneNum());
                            status.setText(gs.getStatus());



                            v.invalidate();
                            break;
                        }
                    }

                    ime.invalidate();
                    naslov.invalidate();
                    tel.invalidate();
                    cenik.invalidate();
                    status.invalidate();
                    v.invalidate();

                    return v;
                }
            });
        }
    }




    public double distance(double lat1, double lon1, double lat2, double lon2){
        double R = 6371;
        double dLat =  Math.toRadians(lat1 - lat2);
        double dLon = Math.toRadians(lon1 - lon2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d;
    }



}
