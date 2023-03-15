package com.example.primerproyecto.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.primerproyecto.R;

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
        CharSequence[] opciones = {getString(R.string.dark), getString(R.string.normal)};
        builder.setTitle(getString(R.string.select_style))
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miListener.alElegirEstilo(i);
                    }
                });
        return builder.create();
    }


}

