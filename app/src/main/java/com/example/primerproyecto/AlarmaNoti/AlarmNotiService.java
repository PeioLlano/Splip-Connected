package com.example.primerproyecto.AlarmaNoti;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.primerproyecto.Actividades.Login;
import com.example.primerproyecto.R;

public class AlarmNotiService extends Service {
    private static final String CHANNEL_ID = "AlarmNotification"; // ID del canal de notificación

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification(); // Llama al método para enviar la notificación
        return START_NOT_STICKY; // El servicio no se reiniciará automáticamente si se detiene
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Crea el canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Alarma", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Crea un intent para lanzar la actividad Login cuando se toque la notificación
        Intent intent = new Intent(this, Login.class);
        PendingIntent intentEnNot = PendingIntent.getActivity(getApplicationContext(), PendingIntent.FLAG_UPDATE_CURRENT, intent, PendingIntent.FLAG_IMMUTABLE);

        // Crea la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(getString(R.string.NotiFirebase))
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setContentIntent(intentEnNot)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Crea el canal de notificación y llama al método startForeground para dispositivos con API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Alarma", "Alarma", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            startForeground(1, builder.build()); // Llama a startForeground con el ID de la notificación y la notificación creada
        } else { // Si la API es menor que 26 se llama al método notify de la NotificationManager
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}