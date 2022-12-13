package com.android.alertup_user;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());

        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();
        MediaPlayer enter= MediaPlayer.create(context.getApplicationContext(), R.raw.enter);
        MediaPlayer exit= MediaPlayer.create(context.getApplicationContext(), R.raw.exit);


        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:

                geofenceList = geofencingEvent.getTriggeringGeofences();
                for (Geofence geofence: geofenceList) {
                    Log.d(TAG, "onReceive: " + geofence.getRequestId());


                        notificationHelper.sendHighPriorityNotification("ENTER IN HIGHLY CLASSIFIED AREA", geofence.getRequestId(), MapsActivity.class);

                }
                Toast.makeText(context, "ENTER IN HIGHLY CLASSIFIED AREA", Toast.LENGTH_SHORT).show();

                   // notificationHelper.sendHighPriorityNotification("ENTER IN HIGHLY CLASSIFIED AREA", "", MapsActivity.class);



                @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_APPEND);
                String name = sharedPreferences.getString("name", "");
                String lat = sharedPreferences.getString("last_latitude", "");
                String lon = sharedPreferences.getString("last_longitude", "");
                //// Toast.makeText(context, ""+lat+ ""+lon, Toast.LENGTH_SHORT).show();
              //  enter.start();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("covid_tool").child("trigger").child("user_enter");


                DatabaseReference newChildRef = myRef.push();
                String key = newChildRef.getKey();

                if (key!= null) {
                    myRef.child(key).child("name").setValue(name);
                    // myRef.child(name).child("Radius").setValue(contact);
                    myRef.child(key).child("last_latitude").setValue(lat);
                    myRef.child(key).child("last_longitude").setValue(lon);


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

                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "DWELL IN HIGHLY CLASSIFIED AREA", Toast.LENGTH_SHORT).show();

                geofenceList = geofencingEvent.getTriggeringGeofences();
                for (Geofence geofence: geofenceList) {
                    Log.d(TAG, "onReceive: " + geofence.getRequestId());


                    notificationHelper.sendHighPriorityNotification("DWELL IN HIGHLY CLASSIFIED AREA", geofence.getRequestId(), MapsActivity.class);

                }


                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "EXIT IN HIGHLY CLASSIFIED AREA", Toast.LENGTH_SHORT).show();
               // enter.stop();
                //exit.start();

                    notificationHelper.sendHighPriorityNotification("EXIT IN HIGHLY CLASSIFIED AREA", "", MapsActivity.class);

                break;
        }

    }
}
