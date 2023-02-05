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
import android.util.Log;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;


public class MapsActivity extends AppCompatActivity
        implements  OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener{


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

    Polygon polygon = null;
    Double lati1 ;
    Double longi1 ;
    Double lati2 ;
    Double longi2 ;
    Double lati3 ;
    Double longi3;
    Double lati4 ;
    Double longi4;
    List<LatLng> latLngs = null;

    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();


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
        
        

        
        check_user();


    }

    private void check_user() {

        String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
       // Toast.makeText(MapsActivity.this, m_androidId, Toast.LENGTH_LONG).show();


        Db = FirebaseDatabase.getInstance().getReference("track").child("user").child(m_androidId);
        Db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Alert_up").child("user_tracking").child("user_location");

                String id = dataSnapshot.child("id").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();

                // check if user is sxisted, if user is existed start tracking
                        if(m_androidId.equals(id)){
                            requestAppPermissions ();
                            myRef.child(id).child("name").setValue(name);
                        }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
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


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casisang, 16));
        check_user();



        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        // [START maps_poly_activity_add_polyline_set_tag]
        // [START maps_poly_activity_add_polyline]
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        // [END maps_poly_activity_add_polyline]
        // [START_EXCLUDE silent]
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // [END maps_poly_activity_add_polyline_set_tag]
        // Style the polyline.
        stylePolyline(polyline1);

        Polyline polyline2 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-29.501, 119.700),
                        new LatLng(-27.456, 119.672),
                        new LatLng(-25.971, 124.187),
                        new LatLng(-28.081, 126.555),
                        new LatLng(-28.848, 124.229),
                        new LatLng(-28.215, 123.938)));
        polyline2.setTag("B");
        stylePolyline(polyline2);

        // [START maps_poly_activity_add_polygon]
        // Add polygons to indicate areas on the map.
        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(-27.457, 153.040),
                        new LatLng(-33.852, 151.211),
                        new LatLng(-37.813, 144.962),
                        new LatLng(-34.928, 138.599)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.setTag("alpha");
        // [END maps_poly_activity_add_polygon]
        // Style the polygon.
        stylePolygon(polygon1);

        Polygon polygon2 = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(-31.673, 128.892),
                        new LatLng(-31.952, 115.857),
                        new LatLng(-17.785, 122.258),
                        new LatLng(-12.4258, 130.7932)));
        polygon2.setTag("beta");
        stylePolygon(polygon2);
        // [END_EXCLUDE]

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        LatLng casisangs= new LatLng(8.133303764999694, 125.13788323894);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casisangs, 16));


        hotspot();
        enableUserLocation();
        googleMap.setOnPolylineClickListener( this);
        googleMap.setOnPolygonClickListener( this);

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

        Db = FirebaseDatabase.getInstance().getReference("poly").child("tline");
        Db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // user.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        /*
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


                         */
                        // marker.showInfoWindow();
                        //showInfoWindow();

                        String name =  dataSnapshot1.child("name").getValue().toString();
                        String safe =  dataSnapshot1.child("alert_message").getValue().toString();
                        String radius =dataSnapshot1.child("radius").getValue().toString();
                        String lat1 =dataSnapshot1.child("plat1").getValue().toString();
                        String lon1 = dataSnapshot1.child("plon1").getValue().toString();
                        String lat2 = dataSnapshot1.child("plat2").getValue().toString();
                        String lon2 = dataSnapshot1.child("plon2").getValue().toString();
                        String lat3 = dataSnapshot1.child("plat3").getValue().toString();
                        String lon3 = dataSnapshot1.child("plon3").getValue().toString();
                        String lat4 = dataSnapshot1.child("plat4").getValue().toString();
                        String lon4 =dataSnapshot1.child("plon4").getValue().toString();


                        double calrad = Double.parseDouble(radius);
                        double calrads = calrad /3;
                        // float rad = Float.valueOf(radius);
                        lati1 = Double.valueOf(lat1);
                        longi1 = Double.valueOf(lon1);
                        lati2 = Double.valueOf(lat2);
                        longi2 = Double.valueOf(lon2);
                        lati3 = Double.valueOf(lat3);
                        longi3 = Double.valueOf(lon3);
                        lati4 = Double.valueOf(lat4);
                        longi4 = Double.valueOf(lon4);

                        LatLng poly1 = new LatLng(lati1, longi1);
                        LatLng poly2 = new LatLng(lati2, longi2);
                        LatLng poly3 = new LatLng(lati3, longi3);
                        LatLng poly4 = new LatLng(lati4, longi4);
                        //Gson gson = new Gson();

                        //String jsonString = gson.toJson(childSnapshot.child("plat1").getValue().toString());
                        // Toast.makeText(ViewActivity.this, ""+jsonString, Toast.LENGTH_LONG).show();

                        double CenterLat = (lati1 + lati2 + lati3 + lati4 ) / 4;
                        double CenterLon = (longi1 + longi2 + longi3 + longi4 ) / 4;
                        LatLng Center = new LatLng(CenterLat, CenterLon);
                        mMap.addMarker(new MarkerOptions().position(Center).title(name));
                        List<LatLng> result = new ArrayList<>();

                        List<LatLng> latLngs = new ArrayList<>();
                        latLngs.add(new LatLng(lati1, longi1));
                        latLngs.add(new LatLng(lati2, longi2));
                        latLngs.add(new LatLng(lati3, longi3));
                        latLngs.add(new LatLng(lati4, longi4));
                        //Log.i("TAG", "computeArea " + SphericalUtil.computeArea(latLngs));
                        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                                .clickable(true)
                                .add(poly1, poly2, poly3, poly4, poly1));
                        // Store a data object with the polygon, used here to indicate an arbitrary type.
                        polygon1.setTag("beta");
                        // [END maps_poly_activity_add_polygon]
                        // Style the polygon.
                        stylePolygon(polygon1);


                        // Toast.makeText(ViewActivity.this, ""+name+ ": "+ SphericalUtil.computeArea(latLngs), Toast.LENGTH_LONG).show();

/*
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(Center);
                        circleOptions.radius(calrads);
                        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
                        circleOptions.fillColor(Color.argb(64, 255, 0,0));
                        circleOptions.strokeWidth(4);
                        mMap.addCircle(circleOptions);

 */
                        geo_ID = safe;

                        //addgeofence(latitude, longitude , perimeter);

                        addGeofence(geo_ID, Center, (int) calrads);
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

    // [END maps_poly_activity_on_map_ready]

    // [START maps_poly_activity_style_polyline]
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                // polyline.setStartCap(
                //new CustomCap(
                // BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_arrow_upward_24), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    // [END maps_poly_activity_style_polyline]

    // [START maps_poly_activity_on_polyline_click]
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }
    // [END maps_poly_activity_on_polyline_click]

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x33FF0000;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0xffF9A825;
        polygon.setFillColor(color);


        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    // [START maps_poly_activity_style_polygon]
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_DARK_GREEN_ARGB = 0x33FF0000;
    private static final int COLOR_LIGHT_GREEN_ARGB = 0x33FF0000;
    private static final int COLOR_DARK_ORANGE_ARGB = 0x33FF0000;
    private static final int COLOR_LIGHT_ORANGE_ARGB = 0x33FF0000;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_DARK_GREEN_ARGB;
                fillColor = COLOR_LIGHT_GREEN_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_DARK_ORANGE_ARGB;
                fillColor = COLOR_LIGHT_ORANGE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);

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
                Intent amphibiansActivityIntent = new Intent(MapsActivity.this, user_id.class);
                startActivity(amphibiansActivityIntent);

                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }





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

    // enable location foreground tracking service
    private void requestAppPermissions () {

        Dexter.withActivity(MapsActivity.this)

                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            //interact.downloadImage(array);
                            startService(new Intent(MapsActivity.this, ForegroundService.class));
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                            //finish();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

        builder.setTitle("Need Permissions");

        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");

        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void openSettings() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts("package", getPackageName(), null);

        intent.setData(uri);

        startActivityForResult(intent, 101);

    }



}