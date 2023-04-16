package com.example.primerproyecto.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

import com.example.primerproyecto.Actividades.Login;
import com.example.primerproyecto.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class TextWidget extends AppWidgetProvider {

    private static ArrayList<String> frases = new ArrayList<>();
    private static final int UPDATE_INTERVAL = 5000; // 5 segundos en milisegundos
    private Handler handler = new Handler();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.text_widget);

        int numeroAleatorio = 0 + new Random().nextInt(frases.size() - 1);

        views.setTextViewText(R.id.appwidget_text, frases.get(numeroAleatorio));

        Intent intent = new Intent(context, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (frases.size() == 0){
            InputStream fich = context.getResources().openRawResource(R.raw.fortune);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fich));
            try {
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    frases.add(line);
                }
                reader.close();
                fich.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // There may be multiple widgets active, so update all of them
        handler.postDelayed(updateWidgetRunnable(context, appWidgetManager, appWidgetIds), UPDATE_INTERVAL);
    }

    private Runnable updateWidgetRunnable(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        return new Runnable() {
            @Override
            public void run() {
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
                // Programar la próxima actualización
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        InputStream fich = context.getResources().openRawResource(R.raw.fortune);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fich));
        try {
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                frases.add(line);
            }
            reader.close();
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}