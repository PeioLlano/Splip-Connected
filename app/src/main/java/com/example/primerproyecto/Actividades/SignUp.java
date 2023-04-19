package com.example.primerproyecto.Actividades;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.primerproyecto.R;
import com.example.primerproyecto.Workers.InsertWorker;
import com.example.primerproyecto.Workers.SelectWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity{

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Button bSignUp = findViewById(R.id.bSignUp);

        int tiempoToast= Toast.LENGTH_SHORT;

        EditText ePasswordRepeat = findViewById(R.id.ePasswordRepeat);
        EditText eMail = findViewById(R.id.eMail);
        EditText ePassword = findViewById(R.id.ePassword);
        EditText eUsername = findViewById(R.id.eUsername);

        //Boton para registrar las credenciales infromadas
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todo_relleno(eUsername.getText().toString(),ePassword.getText().toString(),ePasswordRepeat.getText().toString(),eMail.getText().toString())) {
                    if (isValidEmailAddress(eMail.getText().toString())){
                        if (mismaPass(ePassword.getText().toString(), ePasswordRepeat.getText().toString())){
                            try{
                                registrarUsuario(eUsername.getText().toString(), ePassword.getText().toString());
                            }
                            catch (SQLiteConstraintException e) {
                                e.printStackTrace();
                                Toast aviso = Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_exist), Toast.LENGTH_SHORT);
                                aviso.show();
                            }
                        }
                        else {
                            Toast avisoDistintaPass = Toast.makeText(view.getContext(), getString(R.string.pass_dont_match), tiempoToast);
                            avisoDistintaPass.show();
                        }
                    }
                    else {
                        Toast avisoMailEmail = Toast.makeText(view.getContext(), getString(R.string.invalid_mail), tiempoToast);
                        avisoMailEmail.show();
                    }
                }
                else {
                    Toast avisoCamposNoRellenos = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                    avisoCamposNoRellenos.show();
                }
            }
        });
    }

    private void registrarUsuario(String username, String pass) {
        //Inicializar toast de inicio incorreto
        int tiempoToast= Toast.LENGTH_SHORT;
        Toast avisoInicioIncorrecto = Toast.makeText(this, getString(R.string.user_exist), tiempoToast);

        String passHash = hashPassword(pass);

        Data data = new Data.Builder()
                .putString("tabla", "Usuarios")
                .putStringArray("keys", new String[]{"Usuario", "Contraseña"})
                .putStringArray("values", new String[]{username, passHash})
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(InsertWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(req.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        Boolean inserted = status.getOutputData().getBoolean("resultado", false);
                        if(inserted) {

                            guardarPreferenciaLogin(username);

                            subirTokenFirebase(username);

                            Intent intentRegistrado = new Intent(SignUp.this, ListGrupos.class);
                            intentRegistrado.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentRegistrado.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intentRegistrado.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intentRegistrado.putExtra("usuario", username);
                            startActivity(intentRegistrado);
                            SignUp.this.finish();
                        }
                        //En caso contrario el toast de inicio incorrecto
                        else {
                            avisoInicioIncorrecto.show();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(req);
    }

    public static String hashPassword(String password) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private void subirTokenFirebase(String username) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            task.getException();
                        }

                        String token = task.getResult().getToken();

                        try {
                            Data data = new Data.Builder()
                                    .putString("tabla", "Token")
                                    .putStringArray("keys", new String[]{"Usuario", "Token"})
                                    .putStringArray("values", new String[]{username, token})
                                    .build();

                            Constraints constr = new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build();

                            OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(InsertWorker.class)
                                    .setConstraints(constr)
                                    .setInputData(data)
                                    .build();

                            WorkManager workManager = WorkManager.getInstance(SignUp.this);
                            workManager.enqueue(req);

                            workManager.getWorkInfoByIdLiveData(req.getId())
                                    .observe(SignUp.this, status -> {
                                        if (status != null && status.getState().isFinished()) {
                                            Boolean resultados = status.getOutputData().getBoolean("resultado", false);
                                            if(resultados) {
                                                Log.d("Token", "Token añadido correctamente");
                                            }
                                        }});

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //Guardar las preferencias del usuario que ha iniciado sesion
    public void guardarPreferenciaLogin(String user){
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loged_user", user);
        editor.commit();
    }

    //Comprobar si el mail es valido
    public static boolean isValidEmailAddress(String email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }

    //Comprobar si las dos contraseñas son iguales
    public static boolean mismaPass(String pass1, String pass2) {
        return (pass1.equals(pass2));
    }

    //Comprobar si todos los campos estan rellenos
    private boolean todo_relleno(String username, String pass1, String pass2, String mail) {
        return ((!username.equals("")) && (!pass1.equals("")) && (!pass2.equals("")) && (!mail.equals("")));
    }
}