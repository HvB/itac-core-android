/**
 *     Copyright © 2016 Yassine Siela
 *     Copyright © 2016 AIP Primeca RAO
 *     Copyright © 2016-2018 Université Savoie Mont Blanc
 *
 *     ITAC-Core-Android is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    public final String EVT_RECEPTION_ARTEFACT_INTO_EP;
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
        this.EVT_RECEPTION_ARTEFACT_INTO_EP = ctx.getString(R.string.EVT_ReceptionArtefactIntoEP);
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
