package fr.learning_adventure.android.itac.model;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

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
    private JSONArray modificateurs;
    private String type;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    private String created ;


    private final static String JSON_IDAR = "id";
    private final static String JSON_CREATOR = "creator";
    private final static String JSON_PROPRIETAIRE = "owner";
    private final static String JSON_TYPEARTEFACT = "type";
    private final static String JSON_IDCONTENEUR = "idContainer";
    private final static String JSON_TYPECONTENEUR = "typeContainer";
    private final static String JSON_DATECREATION = "dateCreation";
    private final static String JSON_MODIFICATEURS = "history";
    public final static String JSON_MODIFICATEUR = "user";
    public final static String JSON_DATEMODIFICATION = "dateModification";
    private final static String JSON_TITLE = "title";
    private final static String JSON_CONTENU = "content";
    public final static String ARTIFACT_TYPE_MESSAGE = "message";
    public final static String ARTIFACT_TYPE_IMAGE = "image";


    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
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

    public JSONArray getModificateurs() {
        return modificateurs;
    }

    public void setModificateurs(JSONArray modificateurs) {
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
        this.modificateurs = null;
        this.title = null;
        this.contenu = null;    }

    public Artifact(JSONObject object) {
        Log.d("fromJSOM", "JSON artifact" + object);
        try {
            this.idAr = object.getString(Artifact.JSON_IDAR);
            this.creator = object.getString(Artifact.JSON_CREATOR);
            this.proprietaire = object.getString(Artifact.JSON_PROPRIETAIRE);
            this.type = object.getString(Artifact.JSON_TYPEARTEFACT);
            this.dateCreation = object.getString(Artifact.JSON_DATECREATION);
            this.contenu = object.getString(Artifact.JSON_CONTENU);
            this.idConteneur = object.optString(Artifact.JSON_IDCONTENEUR);
            this.typeConteneur = object.optString(Artifact.JSON_TYPECONTENEUR);
            this.title = object.optString(Artifact.JSON_TITLE);
            this.modificateurs = object.optJSONArray(Artifact.JSON_MODIFICATEURS);
        } catch (JSONException e) {
            Log.d("fromJSOM", "error while parsing JSON artifact", e);
        }

        }


    public JSONObject toJSONMessage() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_IDAR, this.idAr);
            object.putOpt(Artifact.JSON_CREATOR, this.creator);
            object.putOpt(Artifact.JSON_PROPRIETAIRE, this.proprietaire);
            object.putOpt(Artifact.JSON_DATECREATION, this.dateCreation);
            object.putOpt(Artifact.JSON_TYPEARTEFACT, this.type);
            object.putOpt(Artifact.JSON_IDCONTENEUR, this.idConteneur);
            object.putOpt(Artifact.JSON_TYPECONTENEUR, this.typeConteneur);
            object.putOpt(Artifact.JSON_TITLE, this.title);
            object.putOpt(Artifact.JSON_CONTENU, this.contenu);
            object.putOpt(Artifact.JSON_MODIFICATEURS, this.getModificateurs());
        } catch (JSONException e) {
            Log.e("toJSONMessage", "error during generation JSON artifact",e);
        }
        Log.d("toJSONMessage", "JSON artifact" + object);
        return object;
    }

    public JSONObject toJSONImage() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_IDAR, this.idAr);
            object.putOpt(Artifact.JSON_CREATOR, this.creator);
            object.putOpt(Artifact.JSON_PROPRIETAIRE, this.proprietaire);
            object.putOpt(Artifact.JSON_DATECREATION, this.dateCreation);
            object.putOpt(Artifact.JSON_TYPEARTEFACT, this.type);
            object.putOpt(Artifact.JSON_IDCONTENEUR, this.idConteneur);
            object.putOpt(Artifact.JSON_TYPECONTENEUR, this.typeConteneur);
            if(this.getCreated().equals("true"))
            {object.putOpt(Artifact.JSON_CONTENU, encodeImage(this.contenu));}
            else {object.putOpt(Artifact.JSON_CONTENU, this.contenu);}
            object.putOpt(Artifact.JSON_MODIFICATEURS, this.getModificateurs());
        } catch (JSONException e) {
            Log.e("toJSONImage", "error during generation of JSON artifact",e);
        }
        Log.d("toJSONImage", "JSON artifact" + object);
        return object;
    }

    public JSONObject toJSON(){
        JSONObject res = {};
        if (this.getType().equals()){

        }
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


