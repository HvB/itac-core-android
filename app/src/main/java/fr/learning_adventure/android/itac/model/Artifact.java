package fr.learning_adventure.android.itac.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by learninglab on 03/03/16.
 */
public class Artifact implements Serializable{

    private Integer idAr;
    private String title;
    private String creator;
    private String contenu;
    private String typeConteneur ;
    private String idConteneur ;
    private String proprietaire;
    private String dateCreation;
    private String dateDerniereModification;
    private List<Modificateurs> modificateurs;
    private String type;





    private final static String JSON_IDAR = "idAr";
    private final static String JSON_CREATOR = "creator";
    private final static String JSON_PROPRIETAIRE = "proprietaire";
    private final static String JSON_TYPEARTEFACT = "typeArtefact";
    private final static String JSON_IDCONTENEUR = "idConteneur";
    private final static String JSON_TYPECONTENEUR = "typeConteneur";
    private final static String JSON_DATECREATION = "dateCreation";
    private final static String JSON_DATEDERNIEREMODIFICATION= "dateDerniereModification ";
    private final static String JSON_MODIFICATEURS= "modificateurs";
    private final static String JSON_MODIFICATEUR= "modificateur";
    private final static String JSON_DATEMODIFICATION= "dateModification ";
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

    public Integer getIdAr() {
        return idAr;
    }

    public void setIdAr(Integer idAr) {
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



    public Artifact( String creator) {
        this.idAr = null;
        this.creator = creator;
        this.proprietaire = null;
        this.type = null;
        this.idConteneur = null;
        this.typeConteneur = null;
        this.dateCreation = null;
        this.dateDerniereModification = null;
        this.modificateurs=null;
        this.title = null;
        this.contenu = null;
    }

    public class Modificateurs
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
            object.putOpt(Artifact.JSON_DATEDERNIEREMODIFICATION, this.typeConteneur);
            JSONArray jsonArr = new JSONArray();

            for (Modificateurs mod : this.getModificateurs() ) {

                JSONObject pnObj = new JSONObject();

                pnObj.put(Artifact.JSON_MODIFICATEUR,mod.getModificateur());

                pnObj.put(Artifact.JSON_DATEMODIFICATION, mod.getDateModification());

                jsonArr.put(pnObj);

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
            object.putOpt(Artifact.JSON_CONTENU, this.contenu);


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


