package fr.learning_adventure.android.itac.widget;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;



/**
 * Show a new {@link Toast} with specific properties.
 * Created by itac69 on 02/06/2015.
 */
public class Clink {

    /**
     * Show a new {@link Toast}.
     * @param context Application context.
     * @param message The {@link Toast} message.
     */
    public static void show(Context context, String message) {
        Toast toast = Toast.makeText(
                context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
