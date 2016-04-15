package fr.learning_adventure.android.itac.listener;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by yassine on 07/04/2016.
 */

public class MyDragListener implements View.OnDragListener {


    @Override
    public boolean onDrag(View v, DragEvent event) {

        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                //v.setBackgroundColor(Color.parseColor("#aaaaaa"));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                //v.setBackgroundColor(Color.parseColor("Color.GREEN"));
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                LinearLayout container = (LinearLayout) v;
                container.addView(view);
                view.setVisibility(View.VISIBLE);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                //v.setBackgroundColor(Color.GREEN);
            default:
                break;
        }
        return true;
    }
}