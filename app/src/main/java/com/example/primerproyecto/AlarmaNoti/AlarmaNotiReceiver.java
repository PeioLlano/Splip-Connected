package com.example.primerproyecto.AlarmaNoti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.primerproyecto.Actividades.Login;

public class AlarmaNotiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Se crea un intent para iniciar el servicio que enviará la notificación
        Intent serviceIntent = new Intent(context, AlarmNotiService.class);
        // Se inicia el servicio como foreground, lo que significa que se muestra una notificación de que el servicio está en ejecución
        context.startForegroundService(serviceIntent);
    }


}
