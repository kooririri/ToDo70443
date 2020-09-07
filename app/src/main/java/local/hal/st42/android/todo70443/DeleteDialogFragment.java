package local.hal.st42.android.todo70443;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {
    private DatabaseHelper _helper;
    private long _id;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.message_confirm);
        builder.setMessage(R.string.message_alert);
        builder.setPositiveButton(R.string.message_delete,new DialogButtonClickListener());
        builder.setNegativeButton(R.string.message_cancel,new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Activity parent = getActivity();
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    DataAccess.delete(db,_id);
                    parent.finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }

        }
    }

    public void set_helper(DatabaseHelper _helper) {
        this._helper = _helper;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
