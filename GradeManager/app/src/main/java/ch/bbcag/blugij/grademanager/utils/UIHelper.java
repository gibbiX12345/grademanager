package ch.bbcag.blugij.grademanager.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;

/**
 * Created by blugij on 31.05.2016.
 */

    /*
        Klasse für Meldungen / Informationen an den Benutzer.
        Alle AlertDialogs, Snackbars und Toasts können in diese Klasse übertragen werden
        um Code einzusparen bzw. Änderungen nur an einem Ort durchführen zu müssen.
    */

public class UIHelper {

    public static void toastFunctionNotAvailable(Context context){
        makeToast(context, context.getString(R.string.toast_text_function_na), Toast.LENGTH_SHORT);
    }

    //generell
    public static void makeToast(Context context, String message, int length){
        Toast.makeText(context, message, length).show();
    }

    public static void showInfoMessage(Context context, String title, String text, DialogInterface.OnClickListener onClickListener){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", onClickListener);
        alertDialog.show();
    }


}
