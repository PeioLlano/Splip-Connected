package com.example.primerproyecto.AlarmaNoti;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.primerproyecto.R;

public class AlarmNotiService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();
        return Service.START_NOT_STICKY;
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Alarma", "Alarma", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Alarma")
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(getString(R.string.NotiFirebase))
                //.setContentText("My Notification Message")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
