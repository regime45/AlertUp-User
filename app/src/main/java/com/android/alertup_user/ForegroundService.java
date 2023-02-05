package com.android.alertup_user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class ForegroundService extends Service {



    private final IBinder mBinder = new MyBinder();

    private static final String CHANNEL_ID = "2";



    @Override

    public IBinder onBind(Intent intent) {

        return mBinder;

    }



    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }



    @Override

    public void onCreate() {

        super.onCreate();

        buildNotification();

        requestLocationUpdates();

    }



    private void buildNotification() {

        String stop = "stop";

        PendingIntent broadcastIntent = PendingIntent.getBroadcast(

                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle(getString(R.string.app_name))

                .setContentText("Location tracking is working")

                .setOngoing(true)

                .setContentIntent(broadcastIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),

                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setShowBadge(false);

            channel.setDescription("Location tracking is working");

            channel.setSound(null, null);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);

        }



        startForeground(1, builder.build());

    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();

        request.setInterval(1000);

        request.setFastestInterval(3000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            // Request location updates and when an update is

            // received, store the location in Firebase

            client.requestLocationUpdates(request, new LocationCallback() {

                @Override

                public void onLocationResult(LocationResult locationResult) {



                    String location = "Latitude : " + locationResult.getLastLocation().getLatitude() +

                            "\nLongitude : " + locationResult.getLastLocation().getLongitude();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Alert_up").child("user_tracking").child("user_location");

                    DatabaseReference newChildRef = myRef.push();
                    String key = newChildRef.getKey();

                    @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_APPEND);
                    String name = sharedPreferences.getString("name", "");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                    editor.putString("last_latitude", String.valueOf(locationResult.getLastLocation().getLatitude()));
                    editor.putString("last_longitude", String.valueOf(locationResult.getLastLocation().getLongitude()));
                    editor.putString("id", String.valueOf(m_androidId));
                    editor.commit();



                    if (m_androidId != null) {
                       // myRef.child(m_androidId).child("name").setValue(name);
                        // myRef.child(name).child("Radius").setValue(contact);
                        myRef.child(m_androidId).child("last_latitude").setValue(locationResult.getLastLocation().getLatitude());
                        myRef.child(m_androidId).child("last_longitude").setValue(locationResult.getLastLocation().getLongitude());
                    }
                    //     Toast.makeText(context, " save successfully...", Toast.LENGTH_SHORT).show();


                    myRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent (new ValueEventListener()  {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                //   Toast.makeText(Fire_Activity.this, "naa na  "  , Toast.LENGTH_LONG).show();
                            }



                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                  // Toast.makeText(ForegroundService.this, location, Toast.LENGTH_SHORT).show();

                }

            }, null);

        } else {

            stopSelf();

        }

    }

    public class MyBinder extends Binder {

        public ForegroundService getService() {

            return ForegroundService.this;

        }

    }

}