package fr.learning_adventure.android.itac.model;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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

    private JSONObject jsonSrc;
    private String created ;

    private Bitmap thumbnail = null;

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
    public final static String JSON_LOGIN = "login";
    public final static String JSON_DEVICE_UUID = "deviceUid";
    public final static String ARTIFACT_TYPE_MESSAGE = "message";
    public final static String ARTIFACT_TYPE_IMAGE = "image";


    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public JSONObject getJsonSrc() {
        return jsonSrc;
    }

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

    public Bitmap getThumbnail() {
        if (thumbnail == null){
            String base64Img;
            if(this.getCreated().equals("true")){
                base64Img = encodeImage(this.contenu);
            } else {
                base64Img = this.contenu;
            }
            byte[] decodedImg = Base64.decode(base64Img, Base64.DEFAULT);
            Bitmap img = BitmapFactory.decodeByteArray(decodedImg, 0, decodedImg.length);
            thumbnail = Bitmap.createScaledBitmap(img, 200,160, false);
        }
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }



    public Artifact(String creator) {
        Log.d("artifact_constructor ", "new artifact creation ");
        this.idAr = UUID.randomUUID().toString();
        jsonSrc = new JSONObject();
        this.creator = creator;
        this.proprietaire = creator;
        this.type = null;
        this.idConteneur = null;
        this.typeConteneur = null;
        this.dateCreation = null;
        this.modificateurs = new JSONArray();
        this.title = null;
        this.contenu = null;
        this.created="true";
    }

    public Artifact(@NonNull JSONObject object) {
        Log.d("artifact_constructor ", "JSON artifact creation: " + object);
        try {
            this.jsonSrc = object;
            this.idAr = object.getString(Artifact.JSON_IDAR);
            this.creator = object.getString(Artifact.JSON_CREATOR);
            this.proprietaire = object.optString(Artifact.JSON_PROPRIETAIRE);
            this.type = object.getString(Artifact.JSON_TYPEARTEFACT);
            this.dateCreation = object.getString(Artifact.JSON_DATECREATION);
            this.contenu = object.getString(Artifact.JSON_CONTENU);
            this.idConteneur = object.optString(Artifact.JSON_IDCONTENEUR);
            this.typeConteneur = object.optString(Artifact.JSON_TYPECONTENEUR);
            this.title = object.optString(Artifact.JSON_TITLE);
            this.modificateurs = object.optJSONArray(Artifact.JSON_MODIFICATEURS);
            if (this.modificateurs == null) {
                this.modificateurs = new JSONArray();
            }
            this.created="false";
        } catch (JSONException e) {
            Log.d("fromJSOM", "error while parsing JSON artifact", e);
        }

        }


    public JSONObject toJSONMessage() {
        JSONObject object = getJsonSrc();
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
        JSONObject object = getJsonSrc();
        try {
            object.putOpt(Artifact.JSON_IDAR, this.idAr);
            object.putOpt(Artifact.JSON_CREATOR, this.creator);
            object.putOpt(Artifact.JSON_PROPRIETAIRE, this.proprietaire);
            object.putOpt(Artifact.JSON_DATECREATION, this.dateCreation);
            object.putOpt(Artifact.JSON_TYPEARTEFACT, this.type);
            object.putOpt(Artifact.JSON_IDCONTENEUR, this.idConteneur);
            object.putOpt(Artifact.JSON_TYPECONTENEUR, this.typeConteneur);
            object.putOpt(Artifact.JSON_TITLE, this.title);
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
        JSONObject res = getJsonSrc();
        if (ARTIFACT_TYPE_MESSAGE.equals(this.getType())){
            res = this.toJSONMessage();
        } else if (ARTIFACT_TYPE_IMAGE.equals(this.getType())){
            res = this.toJSONImage();
        } else {
            try {
                res.putOpt(Artifact.JSON_IDAR, this.idAr);
                res.putOpt(Artifact.JSON_CREATOR, this.creator);
                res.putOpt(Artifact.JSON_PROPRIETAIRE, this.proprietaire);
                res.putOpt(Artifact.JSON_DATECREATION, this.dateCreation);
                res.putOpt(Artifact.JSON_TYPEARTEFACT, this.type);
                res.putOpt(Artifact.JSON_IDCONTENEUR, this.idConteneur);
                res.putOpt(Artifact.JSON_TYPECONTENEUR, this.typeConteneur);
                res.putOpt(Artifact.JSON_TITLE, this.title);
                res.putOpt(Artifact.JSON_CONTENU, this.contenu);
                res.putOpt(Artifact.JSON_MODIFICATEURS, this.getModificateurs());
        } catch (JSONException e) {
                Log.e("toJSONMessage", "error during generation JSON artifact",e);
            }
            Log.d("toJSONMessage", "JSON artifact" + res);
        }
        return res;
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
//        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        String encImage = "data:image/jpeg;base64,"+Base64.encodeToString(b, Base64.NO_WRAP);
        //Base64.de
        return encImage;
    }

    private Bitmap decodeImage(String base64img) {
        String base64Data = base64img.replaceFirst("^data:image\\/[-\\w]*;base64,","");
        byte[] decodedImg = Base64.decode(base64Data, Base64.DEFAULT);
        Bitmap img = BitmapFactory.decodeByteArray(decodedImg, 0, decodedImg.length);
        return img;
    }

    public boolean copyImage(File path) {
        boolean res = false;
        if (ARTIFACT_TYPE_IMAGE.equals(this.getType())) {
            try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(path))) {
                Bitmap img;
                if ("true".equals(this.getCreated())) {
                    img = BitmapFactory.decodeFile(this.getContenu());
                } else {
                    img = decodeImage(this.getContenu());
                }
                if ((img != null) &&(img.compress(Bitmap.CompressFormat.JPEG, 100, fos))) {
                    this.setContenu(path.getAbsolutePath());
                    this.setCreated("true");
                    res = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return res;
    }

    public File saveImage() {
        File res = null;
        if (ARTIFACT_TYPE_IMAGE.equals(this.getType())) {
            if ("true".equals(this.getCreated())) {
                res = new File(this.getContenu());
            } else {
                File path = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(path))) {
                    Bitmap img;
                    img = decodeImage(this.getContenu());
                    img.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    this.setContenu(path.getAbsolutePath());
                    this.setCreated("true");
                    res = path;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return res;
    }
}


