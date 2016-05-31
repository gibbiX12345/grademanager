package ch.bbcag.blugij.grademanager.utils;

import android.content.Context;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;

/**
 * Created by blugij on 31.05.2016.
 */
public class UIHelper {

    public static void toastFunctionNotAvailable(Context context){
        Toast.makeText(context, context.getString(R.string.toast_text_function_na), Toast.LENGTH_SHORT).show();
    }
}
