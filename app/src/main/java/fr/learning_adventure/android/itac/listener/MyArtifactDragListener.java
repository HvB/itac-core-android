package fr.learning_adventure.android.itac.listener;

import android.view.DragEvent;
import android.view.View;
import android.widget.AbsListView;

import java.util.List;

import fr.learning_adventure.android.itac.adapter.ArtifactAdapter;
import fr.learning_adventure.android.itac.model.Artifact;

/**
 * Created by yassine on 07/04/2016.
 */

public class MyArtifactDragListener implements View.OnDragListener {

    Artifact me;

    public MyArtifactDragListener(Artifact i){
        me = i;
    }

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

                PassObject passObj = (PassObject)event.getLocalState();
                View view = passObj.view;
                Artifact passedItem = passObj.artifact;
                List<Artifact> srcList = passObj.srcList;

                AbsListView oldParent = (AbsListView)view.getParent();
                ArtifactAdapter srcAdapter = (ArtifactAdapter) oldParent.getAdapter();

                AbsListView newParent = (AbsListView)v.getParent();
                ArtifactAdapter destAdapter = (ArtifactAdapter)(newParent.getAdapter());
                List<Artifact> destList = destAdapter.getList();

                int removeLocation = srcList.indexOf(passedItem);
                int insertLocation = destList.indexOf(me);
    /*
     * If drag and drop on the same list, same position,
     * ignore
     */
                if(srcList != destList || removeLocation != insertLocation){
                    if(removeItemToList(srcList, passedItem)){
                        destList.add(insertLocation, passedItem);
                    }

                    srcAdapter.notifyDataSetChanged();
                    destAdapter.notifyDataSetChanged();
                }


                break;
            case DragEvent.ACTION_DRAG_ENDED:
            default:
                break;
        }

        return true;
    }
    class PassObject{
        View view;
        Artifact artifact;
        List<Artifact> srcList;

        PassObject(View v, Artifact i, List<Artifact> s){
            view = v;
            artifact = i;
            srcList = s;
        }
    }
    private boolean removeItemToList(List<Artifact> l, Artifact it){
        boolean result = l.remove(it);
        return result;
    }

    private boolean addItemToList(List<Artifact> l, Artifact it){
        boolean result = l.add(it);
        return result;
    }
}