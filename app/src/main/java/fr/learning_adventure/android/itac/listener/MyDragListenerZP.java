package fr.learning_adventure.android.itac.listener;

import android.view.DragEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import java.util.List;

import fr.learning_adventure.android.itac.adapter.ArtifactAdapter;
import fr.learning_adventure.android.itac.model.Artifact;
import fr.learning_adventure.android.itac.model.PassObject;

/**
 * Created by learninglab on 22/04/16.
 */
public class MyDragListenerZP implements View.OnDragListener {


    LinearLayout trashEditLayout;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:

                PassObject passObj = (PassObject) event.getLocalState();
                int position = passObj.position;
                View view = passObj.view;
                Artifact passedItem = passObj.artifact;
                List<Artifact> srcList = passObj.srcList;
                AbsListView oldParent = (AbsListView) view.getParent();
                ArtifactAdapter srcAdapter = (ArtifactAdapter) oldParent.getAdapter();
                srcList.remove(position);
                srcAdapter.notifyDataSetChanged();


                break;
            case DragEvent.ACTION_DRAG_ENDED:

            default:
                break;
        }

        return true;
    }


}

