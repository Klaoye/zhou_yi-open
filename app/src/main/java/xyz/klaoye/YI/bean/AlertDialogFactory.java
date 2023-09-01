package xyz.klaoye.YI.bean;

import android.content.Context;
import android.content.DialogInterface;

import android.app.AlertDialog;

import xyz.klaoye.YI.R;

public class AlertDialogFactory {
    private Context context;

    public AlertDialogFactory(Context context) {
        this.context = context;
    }

    public AlertDialog getNoticeDialog(String tittle, int iconID, String message, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(this.context)
                .setTitle(tittle)
                .setIcon(iconID)
                .setMessage(message)
                .setPositiveButton(R.string.ok, listener)
                .create();
        return dialog;
    }

    public AlertDialog getAgreeDialog(String tittle, int iconID, String message,
                                      DialogInterface.OnClickListener okListener,
                                      DialogInterface.OnClickListener cancelListener) {
        AlertDialog dialog = new AlertDialog.Builder(this.context)
                .setTitle(tittle)
                .setIcon(iconID)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, cancelListener)
                .create();
        return dialog;
    }

}
