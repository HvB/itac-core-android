package fr.learning_adventure.android.itac.model;

import android.view.View;

import java.util.List;

/**
 * Created by learninglab on 20/04/16.
 */
public class PassObject {
    public View view;
    public Artifact artifact;
    public List<Artifact> srcList;

    public PassObject(View v, Artifact i, List<Artifact> s){
        view = v;
        artifact = i;
        srcList = s;
    }
}