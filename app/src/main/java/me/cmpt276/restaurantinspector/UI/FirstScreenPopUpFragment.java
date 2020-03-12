package me.cmpt276.restaurantinspector.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

import me.cmpt276.restaurantinspector.R;

/**
 *  Displays alert dialog when restaurant didn't have inspections yet
 */

public class FirstScreenPopUpFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.first_screen_pop_up_view, null);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("No inspections yet")
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }
}
