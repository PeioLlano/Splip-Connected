package com.example.primerproyecto.AlarmaNoti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.primerproyecto.Actividades.Login;

public class AlarmaNotiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmNotiService.class);
        context.startForegroundService(serviceIntent);
    }


}
