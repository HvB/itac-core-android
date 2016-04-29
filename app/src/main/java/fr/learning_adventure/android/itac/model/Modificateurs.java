package fr.learning_adventure.android.itac.model;

import java.io.Serializable;

/**
 * Created by learninglab on 26/04/16.
 */
public class Modificateurs implements Serializable
{
    private String modificateur;
    private String dateModification;


    public Modificateurs(String modificateur, String dateModification) {
        this.modificateur = modificateur;
        this.dateModification = dateModification;
    }

    public String getModificateur() {
        return modificateur;
    }

    public void setModificateur(String modificateur) {
        this.modificateur = modificateur;
    }

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }
}
