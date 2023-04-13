package com.example.primerproyecto.Workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SelectWorker extends Worker {
    public SelectWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String tabla = getInputData().getString("tabla");
        String condicion = getInputData().getString("condicion");


        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/pllano002/WEB/ReadData.php?tabla=" + tabla;
        if (condicion != null){
            url += "&condicion=" + condicion;
        }

        try {
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String resultados = "";

            while ((inputLine = in.readLine()) != null) {
                resultados += inputLine;
            }
            in.close();

            Data resultadosData = new Data.Builder()
                    .putString("resultados", resultados)
                    .build();

            return Result.success(resultadosData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
