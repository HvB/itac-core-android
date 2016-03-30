package fr.learning_adventure.android.itac.model;

import android.graphics.Bitmap;

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



}


