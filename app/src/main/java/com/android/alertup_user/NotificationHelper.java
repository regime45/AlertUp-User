package com.android.alertup_user;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import static com.android.alertup_user.R.drawable.alert_icon;
import static com.android.alertup_user.R.drawable.ic_launcher_background;


public class NotificationHelper extends ContextWrapper {

    private static final String TAG = "NotificationHelper";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    @RequiresApi(api = Build.VERSION_CODES.O)


    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;
        Uri sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.alert);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setSound(sound , audioAttributes) ;

        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.BLUE);

        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    public void sendHighPriorityNotification(String title, String body, Class activityName) {
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            Intent intent = new Intent(this, activityName);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            MediaPlayer enter = MediaPlayer.create(getApplicationContext(), R.raw.alert);
            // enter.start();
            Bitmap licon = BitmapFactory.decodeResource(getResources(), R.drawable.alert_icon);
            Uri sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.alert);

            String replyLabel = "Aknowledge Alert";
            RemoteInput remoteInputs = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel(replyLabel)
                    .build();

            NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(alert_icon,
                            getString(R.string.label), pendingIntent)
                            .build();



            PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSound(sound)
                    .setSmallIcon(R.drawable.alert_icon)
                    .setLargeIcon(licon)
                    .setColor(Color.BLUE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setSummaryText("summary").setBigContentTitle(title).bigText(body))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .addAction(action)
                    .build();
            NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);





        }

    }

}
