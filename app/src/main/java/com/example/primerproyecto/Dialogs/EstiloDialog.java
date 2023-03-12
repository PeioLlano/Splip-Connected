package com.example.primerproyecto.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EstiloDialog extends DialogFragment {

    ListenerdelDialogoEstilo miListener;

    public interface ListenerdelDialogoEstilo{
        void alElegirEstilo(int i);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        miListener =(ListenerdelDialogoEstilo) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] opciones = {"Dark", "Normal"};
        builder.setTitle("Seleccione el estilo")
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miListener.alElegirEstilo(i);
                    }
                });
        return builder.create();
    }


}

