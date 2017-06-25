package fr.learning_adventure.android.itac.model;

import android.content.Context;

import fr.learning_adventure.android.itac.R;

/**
 * Created by Stephane on 09/11/2016.
 */

public class ItacConstant {
    public final String EVT_ENVOIE_ARTEFACT_DE_ZP_VERS_ZE;
    public final String EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_ZP;
    public final String EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP;
    public final String EVT_RECEPTION_ARTEFACT_INTO_ZE;
    public final String EVT_RECEPTION_ARTEFACT_INTO_ZP;
    public final String EVT_REPONSE_OK_CONNEXION_ZEP;
    public final String EVT_REPONSE_NOK_CONNEXION_ZEP;
    public final String EVT_NEW_ARTEFACT_IN_ZE;
    public final String EVT_NEW_ARTEFACT_IN_ZP;
    public final String EVT_DECONNEXION;

    private static ItacConstant instance;

    private ItacConstant(Context ctx){
        this.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_ZP = ctx.getString(R.string.EVT_EnvoieArtefactdeZEversZP);
        this.EVT_ENVOIE_ARTEFACT_DE_ZP_VERS_ZE = ctx.getString(R.string.EVT_EnvoieArtefactdeZPversZE);
        this.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP = ctx.getString(R.string.EVT_EnvoieArtefactdeZEversEP);
        this.EVT_RECEPTION_ARTEFACT_INTO_ZE = ctx.getString(R.string.EVT_ReceptionArtefactIntoZE);
        this.EVT_RECEPTION_ARTEFACT_INTO_ZP = ctx.getString(R.string.EVT_ReceptionArtefactIntoZP);
        this.EVT_REPONSE_OK_CONNEXION_ZEP = ctx.getString(R.string.EVT_ReponseOKConnexionZEP);
        this.EVT_REPONSE_NOK_CONNEXION_ZEP = ctx.getString(R.string.EVT_ReponseNOKConnexionZEP);
        this.EVT_NEW_ARTEFACT_IN_ZE = ctx.getString(R.string.EVT_NewArtefactInZE);
        this.EVT_NEW_ARTEFACT_IN_ZP = ctx.getString(R.string.EVT_NewArtefactInZP);
        this.EVT_DECONNEXION = ctx.getString(R.string.EVT_Deconnexion);
    }

    public static ItacConstant getInstance(Context ctx){
        if (instance == null) {
            instance = new ItacConstant(ctx);
        }
        return instance;
    }
}
