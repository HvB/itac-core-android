package fr.learning_adventure.android.itac.model;

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
    private String image;
    private String pseudo;
    private int type;

    private final static String JSON_PSEUDO = "creator";
    private final static String JSON_TITLE = "title";
    private final static String JSON_MESSAGE = "message";
    private final static String JSON_IMAGE = "image";


    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
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

    public String getImagePath() {
        return image;
    }

    public void setImagePath(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public Artifact( String pseudo,int type) {
        this.type = type;
        this.pseudo = pseudo;
        this.title = null;
        this.message = null;
        this.image = null;

    }

    public Artifact(JSONObject object) {
        try {
            this.title = object.getString(Artifact.JSON_TITLE);
            this.message = object.getString(Artifact.JSON_MESSAGE);
            this.pseudo = object.getString(Artifact.JSON_PSEUDO);
            this.image = object.getString(Artifact.JSON_IMAGE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_PSEUDO, this.pseudo);
            object.putOpt(Artifact.JSON_TITLE, this.title);
            object.putOpt(Artifact.JSON_MESSAGE, this.message);
            object.putOpt(Artifact.JSON_IMAGE, this.image);

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


