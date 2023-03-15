package com.example.primerproyecto.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.primerproyecto.R;

import java.util.ArrayList;

public class AddGroupDialog extends AppCompatDialogFragment {

    private AddGroupDialogListener miListener;

    public interface AddGroupDialogListener{
        void añadirGrupo(String Name, String currency);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_group_dialog, null);

        EditText eName = view.findViewById(R.id.eName);
        miListener =(AddGroupDialogListener) getActivity();

        Spinner sDivisa = (Spinner) view.findViewById(R.id.sDivisa);
        ArrayList<String> divisas = new ArrayList<>();

        divisas.add("EUR (€)");
        divisas.add("USD ($)");
        divisas.add("GBP (£)");

        ArrayList<String> divisasRes = new ArrayList<>();

        divisasRes.add("€");
        divisasRes.add("$");
        divisasRes.add("£");

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_texto, divisas);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        sDivisa.setAdapter(adapter);


        builder.setView(view)
                .setTitle(getResources().getString(R.string.EnterNameCurrency))
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!eName.getText().toString().equals("")) {
                            miListener.añadirGrupo(eName.getText().toString(), divisasRes.get(divisas.indexOf(sDivisa.getSelectedItem().toString())));
                        }
                        else{
                            int tiempoToast= Toast.LENGTH_SHORT;
                            Toast avisoGasto = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                            avisoGasto.show();
                        }
                    }
                });

        return builder.create();
    }
}
