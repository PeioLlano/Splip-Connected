package com.example.primerproyecto.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.primerproyecto.R;

public class AddPersonDialog extends AppCompatDialogFragment {

    private AddPersonDialogListener miListener;

    public interface AddPersonDialogListener{
        void añadirPersona(String Username);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_person_dialog, null);

        EditText eName = view.findViewById(R.id.eName);
        miListener =(AddPersonDialogListener) getActivity();

        builder.setView(view)
                .setTitle(getResources().getString(R.string.enterName))
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!eName.getText().toString().equals("")) {
                            miListener.añadirPersona(eName.getText().toString());
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
