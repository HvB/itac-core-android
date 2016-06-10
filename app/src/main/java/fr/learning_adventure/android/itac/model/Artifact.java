package fr.learning_adventure.android.itac.model;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by learninglab on 03/03/16.
 */
public class Artifact implements Serializable {

    private String idAr;
    private String title;
    private String creator;
    private String contenu;
    private String typeConteneur;
    private String idConteneur;
    private String proprietaire;
    private String dateCreation;
    private String dateDerniereModification;
    private List<Modificateurs> modificateurs;
    private String type;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private String created ;


    private final static String JSON_IDAR = "idAr";
    private final static String JSON_CREATOR = "createur";
    private final static String JSON_PROPRIETAIRE = "proprietaire";
    private final static String JSON_TYPEARTEFACT = "typeArtefact";
    private final static String JSON_IDCONTENEUR = "idConteneur";
    private final static String JSON_TYPECONTENEUR = "typeConteneur";
    private final static String JSON_DATECREATION = "dateCreation";
    private final static String JSON_DATEDERNIEREMODIFICATION = "derniereModification";
    private final static String JSON_MODIFICATEURS = "modificateurs";
    private final static String JSON_MODIFICATEUR = "modifier";
    private final static String JSON_DATEMODIFICATION = "modifiedDate";
    private final static String JSON_TITLE = "titre";
    private final static String JSON_CONTENU = "contenu";


    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDateDerniereModification() {
        return dateDerniereModification;
    }

    public void setDateDerniereModification(String dateDerniereModification) {
        this.dateDerniereModification = dateDerniereModification;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(String proprietaire) {
        this.proprietaire = proprietaire;
    }

    public List<Modificateurs> getModificateurs() {
        return modificateurs;
    }

    public void setModificateurs(List<Modificateurs> modificateurs) {
        this.modificateurs = modificateurs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdAr() {
        return idAr;
    }

    public void setIdAr(String idAr) {
        this.idAr = idAr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeConteneur() {
        return typeConteneur;
    }

    public void setTypeConteneur(String typeConteneur) {
        this.typeConteneur = typeConteneur;
    }

    public String getIdConteneur() {
        return idConteneur;
    }

    public void setIdConteneur(String idConteneur) {
        this.idConteneur = idConteneur;
    }


    public Artifact(String creator) {
        this.idAr = null;
        this.creator = creator;
        this.proprietaire = null;
        this.type = null;
        this.idConteneur = null;
        this.typeConteneur = null;
        this.dateCreation = null;
        this.dateDerniereModification = null;
        this.modificateurs = null;
        this.title = null;
        this.contenu = null;    }

    public Artifact(JSONObject object) {

        try {
            this.idAr = object.getString(Artifact.JSON_IDAR);
            this.creator = object.getString(Artifact.JSON_CREATOR);
            this.proprietaire = object.getString(Artifact.JSON_PROPRIETAIRE);
            this.type = object.getString(Artifact.JSON_TYPEARTEFACT);
            this.idConteneur = object.getString(Artifact.JSON_IDCONTENEUR);
            this.typeConteneur = object.getString(Artifact.JSON_TYPECONTENEUR);
            this.dateCreation = object.getString(Artifact.JSON_DATECREATION);
            this.contenu = object.getString(Artifact.JSON_CONTENU);
            if (this.type.equals("message")) {
                this.title = object.getString(Artifact.JSON_TITLE);
                this.dateDerniereModification = object.getString(Artifact.JSON_DATEDERNIEREMODIFICATION);
                JSONArray jsonArr = object.getJSONArray(Artifact.JSON_MODIFICATEURS);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject objet = jsonArr.getJSONObject(i);
                    String modificateur = objet.getString(Artifact.JSON_MODIFICATEUR);
                    String date = objet.getString(Artifact.JSON_DATEMODIFICATION);
                    Modificateurs mod = new Modificateurs(modificateur, date);
                    this.modificateurs.add(mod);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        }


    public JSONObject toJSONMessage() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_IDAR, this.idAr);
            object.putOpt(Artifact.JSON_CREATOR, this.creator);
            object.putOpt(Artifact.JSON_PROPRIETAIRE, this.proprietaire);
            object.putOpt(Artifact.JSON_TYPEARTEFACT, this.type);
            object.putOpt(Artifact.JSON_IDCONTENEUR, this.idConteneur);
            object.putOpt(Artifact.JSON_TYPECONTENEUR, this.typeConteneur);
            object.putOpt(Artifact.JSON_DATECREATION, this.dateCreation);
            object.putOpt(Artifact.JSON_DATEDERNIEREMODIFICATION, this.dateDerniereModification);
            JSONArray jsonArr = new JSONArray();
            if (this.getModificateurs() != null) {
                for (Modificateurs mod : this.getModificateurs()) {

                    JSONObject pnObj = new JSONObject();

                    pnObj.put(Artifact.JSON_MODIFICATEUR, mod.getModificateur());

                    pnObj.put(Artifact.JSON_DATEMODIFICATION, mod.getDateModification());

                    jsonArr.put(pnObj);

                }
            }

            object.putOpt(Artifact.JSON_MODIFICATEURS, jsonArr);
            object.putOpt(Artifact.JSON_TITLE, this.title);
            object.putOpt(Artifact.JSON_CONTENU, this.contenu);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public JSONObject toJSONImage() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_IDAR, this.idAr);
            object.putOpt(Artifact.JSON_CREATOR, this.creator);
            object.putOpt(Artifact.JSON_PROPRIETAIRE, this.proprietaire);
            object.putOpt(Artifact.JSON_TYPEARTEFACT, this.type);
            object.putOpt(Artifact.JSON_IDCONTENEUR, this.idConteneur);
            object.putOpt(Artifact.JSON_TYPECONTENEUR, this.typeConteneur);
            object.putOpt(Artifact.JSON_DATECREATION, this.dateCreation);
            if(this.getCreated().equals("true"))
            {object.putOpt(Artifact.JSON_CONTENU, encodeImage(this.contenu));}
            else {object.putOpt(Artifact.JSON_CONTENU, this.contenu);}


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

    //encoder image en base 64
    @TargetApi(Build.VERSION_CODES.FROYO)
    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

}


