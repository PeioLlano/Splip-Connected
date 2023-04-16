package com.example.primerproyecto.Workers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageWorker extends Worker {

    public MessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            Log.d("statusCode", String.valueOf("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/pllano002/WEB/InsertData.php"));

            HttpURLConnection urlConnection = null;
            URL url = new URL("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/pllano002/WEB/SendMessage.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.close();

            int statusCode = urlConnection.getResponseCode();
            String line, result = "";


            if (statusCode == 200) {
                Log.d("Mandado mensaje", "Ok");
            }

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
