package ch.bbcag.blugij.grademanager.utils;

import android.content.Context;
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
        Toast.makeText(context, context.getString(R.string.toast_text_function_na), Toast.LENGTH_SHORT).show();
    }
}
