package com.example.primerproyecto.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.primerproyecto.R;

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
        CharSequence[] opciones = {getString(R.string.English), getString(R.string.Spanish), getString(R.string.Euskera)};
        builder.setTitle(getString(R.string.select_lenguage))
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miListener.alElegirIdioma(i);
                    }
                });
        return builder.create();
    }


}

