package fr.learning_adventure.android.itac.listener;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.widget.AbsListView;

import java.util.List;

import fr.learning_adventure.android.itac.adapter.ArtifactAdapter;
import fr.learning_adventure.android.itac.model.Artifact;
import fr.learning_adventure.android.itac.model.PassObject;

/**
 * Created by yassine on 07/04/2016.
 */

public class MyArtifactDragListener implements View.OnDragListener {




    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(Color.RED);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.parseColor("#e9e8dd"));
                break;
            case DragEvent.ACTION_DROP:

                PassObject passObj = (PassObject)event.getLocalState();
                int position = passObj.position;
                View view = passObj.view;
                Artifact passedItem = passObj.artifact;
                List<Artifact> srcList = passObj.srcList;

                AbsListView oldParent = (AbsListView)view.getParent();
                ArtifactAdapter srcAdapter = (ArtifactAdapter) oldParent.getAdapter();
               srcList.remove(position);
                srcAdapter.notifyDataSetChanged();


                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundColor(Color.parseColor("#e9e8dd"));

            default:
                break;
        }

        return true;
    }


}