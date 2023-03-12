package com.example.primerproyecto.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class IdiomaDialog extends DialogFragment {

    ListenerdelDialogoIdioma miListener;

    public interface ListenerdelDialogoIdioma{
        void alElegirIdioma(int i);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        miListener =(ListenerdelDialogoIdioma) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] opciones = {"Inglés", "Español", "Euskera"};
        builder.setTitle("Seleccione el idioma")
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miListener.alElegirIdioma(i);
                    }
                });
        return builder.create();
    }


}

