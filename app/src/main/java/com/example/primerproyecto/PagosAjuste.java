package com.example.primerproyecto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Pago;
import com.example.primerproyecto.Dialogs.IdiomaDialog;
import com.example.primerproyecto.ListAdapters.PagoAdapter;
import com.example.primerproyecto.ListAdapters.PagosAjusteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringTokenizer;

public class PagosAjuste extends AppCompatActivity implements PagosAjusteAdapter.CheckBoxListener {

    ArrayList<Pago> pagosAjuste = new ArrayList<>();
    ArrayList<Pago> pagosSeleccionados = new ArrayList<>();
    private PagosAjusteAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagos_ajuste);

        String divisa = "";

        Bundle extras = getIntent().getExtras();
        Grupo grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        divisa = grupo.getDivisa();
        pagosAjuste =  grupo.ajustarCuentas();

        ListView lPagos = (ListView) findViewById(R.id.lPagos);
        pAdapter = new PagosAjusteAdapter(getApplicationContext(), pagosAjuste, divisa);
        lPagos.setAdapter(pAdapter);

        FloatingActionButton bMail = (FloatingActionButton) findViewById(R.id.bMail);
        bMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelo_linea_cuerpo = "";
                String entrada_cuerpo = "";
                String final_cuerpo = "";
                String asunto = "";

                InputStream fich = getResources().openRawResource(R.raw.mail_body);
                BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
                try {
                    asunto = buff.readLine();
                    entrada_cuerpo = buff.readLine();
                    modelo_linea_cuerpo = buff.readLine();
                    final_cuerpo = buff.readLine();
                    fich.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(String.format(asunto, grupo.getTitulo()));
                String body = entrada_cuerpo + "\n \n";
                for (Pago p: pagosAjuste) {
                    body +=  "\t" + String.format(modelo_linea_cuerpo, p.getAutor().getNombre(), p.getDestinatario().getNombre(), p.getCantidad()) + "\n";
                }
                body += "\n" + final_cuerpo;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                //emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(asunto, grupo.getTitulo()));
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(emailIntent, "Enviar e-mail"));

            }
        });

        FloatingActionButton bSave = (FloatingActionButton) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*try {
                    OutputStreamWriter fichero = new OutputStreamWriter(openFileOutput("formato_mail_aviso.txt",
                            Context.MODE_PRIVATE));
                    fichero.write("Ajuste de cuentas para %s \n" +
                                    "Los pagos que le quedan por hacer para ajustar las cuentas, son los siguientes: \n" +
                                    "\t - Pago de %s a %s con un importe de %f. \n" +
                                    "~ Gracias por confiar en Splip ~");
                    fichero.close();
                } catch (IOException e){}*/

                Intent intent = new Intent();
                intent.putExtra("pagosSeleccionados", pagosSeleccionados);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        LinearLayout lVacia = findViewById(R.id.lVacia);

        if (pagosAjuste.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lPagos.setVisibility(View.GONE);
        }
    }

    @Override
    public void checkPago(Pago p) {
        if (pagosSeleccionados.contains(p)) {
            pagosSeleccionados.remove(p);
        }
        else{
            pagosSeleccionados.add(p);
        }
    }
}
