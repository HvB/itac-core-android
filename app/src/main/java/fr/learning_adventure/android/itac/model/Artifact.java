package fr.learning_adventure.android.itac.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by learninglab on 03/03/16.
 */
public class Artifact implements Serializable{

    private Integer idAr;
    private String title;
    private String message;
    private Bitmap image;
    private String pseudo;

    private final static String JSON_ID = "id";
    private final static String JSON_PSEUDO = "author";
    private final static String JSON_TITLE = "title";
    private final static String JSON_MESSAGE = "message";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIdAr() {
        return idAr;
    }

    public void setIdAr(Integer idAr) {
        this.idAr = idAr;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Artifact(Integer idAr, String pseudo) {
        this.idAr = idAr;
        this.pseudo = pseudo;
        this.title = null;
        this.message = null;
        this.image = null;

    }

    public Artifact(JSONObject object) {
        try {
            this.idAr = object.getInt(Artifact.JSON_ID);
            this.title = object.getString(Artifact.JSON_TITLE);
            this.message = object.getString(Artifact.JSON_MESSAGE);
            this.pseudo = object.getString(Artifact.JSON_PSEUDO);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_ID, this.idAr);
            object.putOpt(Artifact.JSON_TITLE, this.title);
            object.putOpt(Artifact.JSON_MESSAGE, this.message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if ((this.idAr != null) && (o != null) && (((Artifact) o).idAr != null) && (o instanceof Artifact)) {
            return this.idAr.equals(((Artifact) o).idAr);
        } else {
            return false;
        }
    }



}


