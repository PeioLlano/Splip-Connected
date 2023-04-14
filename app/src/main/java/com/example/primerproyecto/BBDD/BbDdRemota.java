package com.example.primerproyecto.BBDD;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import androidx.lifecycle.LifecycleOwner;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.primerproyecto.Workers.SelectWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

public class BbDdRemota {

    public Cursor selectTabla(Context contexto, String tabla, Boolean condicion, String[] keyCod, String[] valCod) {
        final Cursor[] cursorResult = {null};

        Data.Builder data = new Data.Builder()
                .putString("tabla", tabla);

        if (condicion) {
            String condicionStr = "";
            for (int i=0; i<keyCod.length ;i++) {
                condicionStr += keyCod[i] + "='" + valCod[i] + "'";
                if ((i+1)<keyCod.length){
                    condicionStr += " AND ";
                }
            }
            data.putString("condicion", condicionStr);
        }

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(SelectWorker.class)
                .setConstraints(constr)
                .setInputData(data.build())
                .build();

        WorkManager.getInstance(contexto).getWorkInfoByIdLiveData(req.getId())
                .observe((LifecycleOwner) this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        String resultados = status.getOutputData().getString("resultados");
                        if (resultados == "null" || resultados == "") resultados = null;
                        if(resultados != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(resultados);
                                cursorResult[0] = getCursorFromJsonArray(jsonArray);

                            } catch (JSONException e) {
                                // manejar la excepción...
                            }
                        }
                        //En caso contrario el toast de inicio incorrecto
                        else {
                            cursorResult[0] = null;
                        }
                    }
                });
        WorkManager.getInstance(contexto).enqueue(req);
        return cursorResult[0];
    }

    public static Cursor getCursorFromJsonArray(JSONArray jsonArray) throws JSONException {
        // Creamos un nuevo MatrixCursor
        MatrixCursor cursor = new MatrixCursor(new String[]{});

        // Iteramos sobre los elementos del JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Si es la primera iteración, definimos las columnas
            if (i == 0) {
                JSONArray keys = jsonObject.names();
                String[] columns = new String[keys.length()];

                for (int j = 0; j < keys.length(); j++) {
                    columns[j] = keys.getString(j);
                }

                cursor = new MatrixCursor(columns);
            }

            // Creamos un array de objetos con los valores de la fila
            Object[] values = new Object[jsonObject.length()];

            for (int j = 0; j < jsonObject.length(); j++) {
                values[j] = jsonObject.get(cursor.getColumnName(j));
            }

            // Añadimos la fila al cursor
            cursor.addRow(values);
        }

        return cursor;
    }

}
