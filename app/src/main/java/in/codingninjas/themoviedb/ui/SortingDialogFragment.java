package in.codingninjas.themoviedb.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import in.codingninjas.themoviedb.R;
import in.codingninjas.themoviedb.data.Sort;
import in.codingninjas.themoviedb.data.SortHelper;

/**
 * Created by nsbhasin on 21/07/17.
 */

public class SortingDialogFragment extends DialogFragment {
    public static final String BROADCAST_SORT_PREFERENCE_CHANGED = "SortPreferenceChanged";

    public static final String TAG = "SortingDialogFragment";
    private SortHelper sortHelper;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sortHelper = SortHelper.getInstance(getContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle(getString(R.string.sort_dialog_title));
        builder.setNegativeButton(getString(R.string.action_cancel), null);
        builder.setSingleChoiceItems(R.array.pref_sort_by_labels, sortHelper.getSortByPreference().ordinal(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                sortHelper.saveSortByPreference(Sort.values()[which]);
                sendSortPreferenceChangedBroadcast();
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }

    private void sendSortPreferenceChangedBroadcast() {
        Intent intent = new Intent(BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext()).sendBroadcast(intent);
        Log.i("LocalBroadcastManager",BROADCAST_SORT_PREFERENCE_CHANGED + "sent");
    }
}
