package fr.learning_adventure.android.itac.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by learninglab on 03/03/16.
 */
public class Artifact implements Serializable{
    private final static String JSON_ID = "id";
    private final static String JSON_AUTHOR = "author";
    private final static String JSON_MODIFIERS = "modifiers";
    private final static String JSON_TITLE = "title";
    private final static String JSON_MESSAGE = "message";


    /** Id of the {@link Artifact}. */
    private Integer id;

    /** {@link Learner} who creates the {@link Artifact}. */
    private Learner author;

    /** {@link Learner} set who modify the {@link Artifact}. */
    private Set<Learner> modifiers;

    /** Title of the {@link Artifact}. */
    private String title;

    /** Message of the {@link Artifact}. */
    private String message;



    /**
     * Create new {@link Artifact} with its author.
     * @param author {@link Learner} who creates the {@link Artifact}.
     */
    public Artifact(Learner author) {
        this.id = null;
        this.author = author;
        this.title = null;
        this.message = null;

    }

    /**
     * Create new {@link Artifact} from server data.
     * @param object {@link Artifact} in JSON format.
     */
    public Artifact(JSONObject object) {
        try {
            this.id = object.getInt(Artifact.JSON_ID);
            this.author = new Learner(object.getJSONObject(Artifact.JSON_AUTHOR));
            this.modifiers = new HashSet<>();
            for (int i = 0; i < object.getJSONArray(Artifact.JSON_MODIFIERS).length(); i++) {
                Learner Learner = new Learner(object.getJSONArray(Artifact.JSON_MODIFIERS).getJSONObject(i));
                Log.i("MODEL MODIFIER", this.author.getPseudo() + " " + Learner.getPseudo());
                this.modifiers.add(Learner);
            }
            this.title = object.getString(Artifact.JSON_TITLE);
            this.message = object.getString(Artifact.JSON_MESSAGE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return this.id;
    }

    public Learner getAuthor() {
        return this.author;
    }

    public Set<Learner> getModifiers() {
        return this.modifiers;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt(Artifact.JSON_ID, this.id);
            object.putOpt(Artifact.JSON_AUTHOR, this.author.toJSON());
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
        if ((this.id != null) && (o != null) && (((Artifact) o).id != null) && (o instanceof Artifact)) {
            return this.id.equals(((Artifact) o).id);
        } else {
            return false;
        }
    }
}


