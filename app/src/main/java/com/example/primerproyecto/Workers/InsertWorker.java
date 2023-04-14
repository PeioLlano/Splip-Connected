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

public class InsertWorker extends Worker {

    public InsertWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String tabla = getInputData().getString("tabla");
        String[] keys = getInputData().getStringArray("keys");
        String[] values = getInputData().getStringArray("values");


        try {
            Log.d("statusCode", String.valueOf("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/pllano002/WEB/InsertData.php"));

            HttpURLConnection urlConnection = null;
            URL url = new URL("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/pllano002/WEB/InsertData.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            Log.d("tabla", tabla);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("tabla", tabla);
            for(int i=0; keys.length>i; i++) {
                Log.d("key-value", keys[i] + " --> " + values[i]);
                builder.appendQueryParameter(keys[i], values[i]);
            }

            String parametros = builder.build().getEncodedQuery();

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            String line, result = "";

            Log.d("statusCode", String.valueOf(statusCode));

            Data resultadosData = null;

            resultadosData = new Data.Builder()
                    .putBoolean("resultado", false)
                    .build();

            if (statusCode == 200) {

                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String status = jsonObject.getString("status");

                    Log.d("resultados", result);

                    if (status.equals("success")) {
                        resultadosData = new Data.Builder()
                                .putBoolean("resultado", true)
                                .build();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return Result.success(resultadosData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
