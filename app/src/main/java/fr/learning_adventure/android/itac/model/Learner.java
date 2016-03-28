package fr.learning_adventure.android.itac.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

// Created by learninglab on 02/03/16.


public class Learner implements Serializable{

    private final static String JSON_PSEUDO = "pseudo";

    private final static String JSON_MAC = "mac";
    private final static String JSON_ARTIFACTS = "artifacts";



//* pseudo of the {@link Learner}.

    private String pseudo;


//* Mac address of the {@link Learner}.

    private String mac;

//* {@link artifact} list of the {@link Learner}.

   // private List<Artifact> artifacts;

//*
    // * Create new {@link Learner} from his/her mac address.
     //* @param mac The mac address


    public Learner(String mac, String pseudo) {
        this.pseudo = pseudo;
        this.mac = mac;
        //this.artifacts = null;
    }


//*
     //* Create new {@link Learner} from server data.
     //* @param object {@link Learner} in JSON format.


    public Learner(JSONObject object) {
        try {
            this.pseudo = object.getString(Learner.JSON_PSEUDO);
            this.mac = null;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }


    //public List<Artifact> getartifacts() {
        //return this.artifacts;
    //}

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Learner.JSON_PSEUDO,this.pseudo);
            object.putOpt(Learner.JSON_MAC, this.mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public int hashCode() {
        return 0;
    }


}

