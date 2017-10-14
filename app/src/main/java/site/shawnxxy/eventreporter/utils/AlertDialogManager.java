package site.shawnxxy.eventreporter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import site.shawnxxy.eventreporter.R;

/**
 * Created by ShawnX on 10/4/17.
 */

public class AlertDialogManager {

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog title
        alertDialog.setTitle(title);
        // Setting Dialog message
        alertDialog.setMessage(message);

        if (status != null) {
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        }

        // Seeting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // showing alert message
        alertDialog.show();
    }
}
