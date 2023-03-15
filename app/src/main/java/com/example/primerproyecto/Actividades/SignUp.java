package com.example.primerproyecto.Actividades;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.BBDD.BBDD;
import com.example.primerproyecto.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity{

    SQLiteDatabase bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        BBDD gestorBBDD = new BBDD(this, "SpliP", null, 1);
        bbdd = gestorBBDD.getWritableDatabase();

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
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("Usuario", eUsername.getText().toString());
                                contentValues.put("Contraseña", ePassword.getText().toString());

                                bbdd.insertOrThrow("Usuarios", null, contentValues);

                                guardarPreferenciaLogin(eUsername.getText().toString());

                                Intent intentRegistrado = new Intent(SignUp.this, ListGrupos.class);
                                intentRegistrado.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intentRegistrado.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intentRegistrado.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intentRegistrado.putExtra("usuario", eUsername.getText().toString());
                                startActivity(intentRegistrado);
                                SignUp.this.finish();
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