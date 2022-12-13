package com.android.alertup_user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import android.os.Bundle;


import android.provider.Settings;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;


import com.firebase.geofire.GeoFire;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;


public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    public GeofenceHelper geofenceHelper;
    AudioManager am;

    /* access modifiers changed from: private */
    public GeofencingClient geofencingClient;

    // private GeofencingClient geofencingClient;
    // private GeofenceHelper geofenceHelper;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference database; // database
    GeoFire geoFire;            // database
    Marker locationMarker;

    private static final int PermissionCode = 1994;
    private static final int ServiceRequest = 1994;
    private MediaPlayer mp;
    private DatabaseReference Db;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private Location mCurrentLocation;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    public LatLng latlng1;
    private java.util.List List;
    public List<Geofence> geofencelist = new ArrayList();
    public String geo_ID;
    public Integer radius1;
    MediaPlayer enter, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
       am = (AudioManager)  getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
       enter= MediaPlayer.create(getApplicationContext(), R.raw.enter);
        exit= MediaPlayer.create(getApplicationContext(), R.raw.exit);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4b88a2")));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>AlertUp</font>"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        this.geofencingClient = LocationServices.getGeofencingClient(this);
        this.geofenceHelper = new GeofenceHelper(this);


        // Database reference
        database = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(database);
        enableLocationSettings();
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (Build.VERSION.SDK_INT < 29) {
            enableUserLocation();
            // hotspot();
        } else if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_BACKGROUND_LOCATION") == 0) {
            //hotspot();
            
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.ACCESS_BACKGROUND_LOCATION")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, this.BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, this.BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
        }

        mMap = googleMap;
        LatLng casisang= new LatLng(8.133303764999694, 125.13788323894);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casisang, 15));


        hotspot();
        enableUserLocation();

       // user_location();
    }
    private void  user_location(){
        Db = FirebaseDatabase.getInstance().getReference("covid_tool").child("geofencing").child("user_location");
        Db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        // String Name = dataSnapshot1.child("name").getValue().toString();
                        //   Double latitude = Double.valueOf(dataSnapshot1.child("last_latitude").getValue().toString());
                        //   Double longitude = Double.valueOf(dataSnapshot1.child("last_longitude").getValue().toString());
                        //   LatLng LatLng = new LatLng(latitude, longitude);

                        // addMarker(LatLng);
                        // geo_ID = Name;
                        // latlng1 = LatLng;
                        // String title = "Name:\t" +Name ;
                        // float[] colours = { BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_ORANGE,
                        //     /* etc */ };
                        // BitmapDescriptorFactory.defaultMarker(colours[new Random().nextInt(colours.length)]);
                        // Marker marker =  mMap.addMarker(new MarkerOptions().position(LatLng).title(title).icon( BitmapDescriptorFactory.defaultMarker(colours[new Random().nextInt(colours.length)])));
                        //marker.showInfoWindow();

                    }
                    catch (NumberFormatException e)
                    {
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void hotspot() {
        Db = FirebaseDatabase.getInstance().getReference("alerts_zone").child("classified_zone");
        Db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // user.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        String Name = dataSnapshot1.child("Geo_Name").getValue().toString();
                        String alert = dataSnapshot1.child("alert_message").getValue().toString();
                        Integer radius = Integer.valueOf(dataSnapshot1.child("Radius").getValue().toString());
                        Double latitude = Double.valueOf(dataSnapshot1.child("latitude").getValue().toString());
                        Double longitude = Double.valueOf(dataSnapshot1.child("longitude").getValue().toString());
                        LatLng LatLng = new LatLng(latitude, longitude);

                        String a1 = String.valueOf(latitude);

                     // Toast.makeText(MapsActivity.this, "name  " + Name+"   radius "+a1 + "latitude" +latitude+ "longitude"  +longitude, Toast.LENGTH_LONG).show();
                        addMarker(LatLng);
                        addCircle(LatLng, radius);
                        geo_ID = alert;
                        latlng1 = LatLng;
                        radius1 = radius;
                        String title = "Name:\t" +Name + "\t\tradius:\t" +radius;
                        Marker marker =  mMap.addMarker(new MarkerOptions().position(LatLng).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        marker.showInfoWindow();
                        //showInfoWindow();
                        // addgeofence(latitude, longitude , perimeter);

                        addGeofence(geo_ID, latlng1, radius1);
                        //displayName(geo_ID);
                    }
                    catch (NumberFormatException e)
                    {
// log e if you want...
                    }

                }



                GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofencelist);
                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d(TAG, "onSuccess: Geofence Added...");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String errorMessage = geofenceHelper.getErrorString(e);
                                //Log.d(TAG, "onFailure: " + errorMessage);
                            }
                        });




            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




    }

    private void displayName(String geo_id) {
      //  String ss = geo_id;
       // Toast.makeText(MapsActivity.this, ss, Toast.LENGTH_LONG).show();
    }

    private void addGeofence(String geo_ID2, LatLng latLng1, Integer perimeter) {
        //double longitude = LatLng.longitude;
        //double latitude = LatLng.latitude;
        // Float perimeters = Float.valueOf(perimeter);
        //LatLng = new LatLng(latitude, longitude);
        // Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, LatLng, perimeter, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        geofencelist.add(geofenceHelper.getGeofence(geo_ID2, latLng1, perimeter,  Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT));
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofencelist);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        //Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }





/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


*/
private void enableUserLocation() {
    if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        mMap.setMyLocationEnabled(true);
    } else {
        //Ask for permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }
    }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void playSound(int resId) {
        mp = MediaPlayer.create(MapsActivity.this, resId);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        });
        mp.start();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ringer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {


            case R.id.ring:
                showNotification();
                return true;

            case R.id.vibrate:
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */




    private void vibrateDevice() {

        Vibrator vibratorService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibratorService != null && vibratorService.hasVibrator()) {
            vibratorService.vibrate(500);
        }
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, Integer radius) {

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 52, 234,53));
        circleOptions.fillColor(Color.argb(64, 75, 136,162));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showNotification()
    {
        Uri sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.alert);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MapsActivity. this, "default_notification_channel_id" )
                .setSmallIcon(R.drawable. ic_launcher_foreground )
                .setContentTitle( "Test" )
                .setSound(sound)
                .setContentText( "Hello! This is my first pushnotification" ) ;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE );
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                    .setUsage(AudioAttributes. USAGE_ALARM )
                    .build() ;
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( "NOTIFICATION_CHANNEL_ID" , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
            notificationChannel.setSound(sound , audioAttributes) ;
            mBuilder.setChannelId( "NOTIFICATION_CHANNEL_ID" ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () ,
                mBuilder.build()) ;
    }

    protected void enableLocationSettings() {

        LocationRequest locationRequest = LocationRequest.create()

                .setInterval(1000)

                .setFastestInterval(3000)

                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()

                .addLocationRequest(locationRequest);

        LocationServices

                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(this, 123);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });

    }








}