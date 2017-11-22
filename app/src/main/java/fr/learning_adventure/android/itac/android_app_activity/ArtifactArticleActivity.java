/**
 *     Copyright © 2016 Yassine Siela
 *     Copyright © 2016 AIP Primeca RAO
 *     Copyright © 2016-2017 Université Savoie Mont Blanc
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
package fr.learning_adventure.android.itac.android_app_activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.model.Artifact;

/**
 * Created by yassine on 12/04/2016.
 */
public class ArtifactArticleActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artifact_article);

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat fmtDateCreation = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
        DateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String title = getIntent().getStringExtra("title");
        String pseudo = getIntent().getStringExtra("pseudo");
        String message = getIntent().getStringExtra("message");
        String date = getIntent().getStringExtra("date");
        String modificateursStr = getIntent().getStringExtra("modificateurs");

        GridLayout modificateursView = (GridLayout) findViewById(R.id.modificateurs);
        TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        TextView titleTextView = (TextView) findViewById(R.id.titre);
        TextView messageTextView = (TextView) findViewById(R.id.message_input);
        JSONArray modificateurs = new JSONArray();
        try {
            String dateCreation = fmtDateCreation.format(fmt.parse(date));
            pseudoView.setText("Crée par " + pseudo + " le " + dateCreation);
            if (modificateursStr != null) {
                modificateurs = new JSONArray(modificateursStr);
                if (modificateurs.length() > 0) modificateursView.setVisibility(View.VISIBLE);
                else modificateursView.setVisibility(View.GONE);
                for (int i = 0 ; i < modificateurs.length(); i++){
                    JSONObject modificateur = modificateurs.getJSONObject(i);
                    Log.e("modificateurs", "parcours de la liste des modificateurs, pos " + i + ": "+modificateur);
                    TextView pseudoModifView = new TextView(this);
                    Date dateModif = fmt.parse(modificateur.getString(Artifact.JSON_DATEMODIFICATION));
                    pseudoModifView.setText(modificateur.getString(Artifact.JSON_MODIFICATEUR));
                    TextView dateModifView = new TextView(this);
                    dateModifView.setText(fmtOut.format(dateModif));
                    pseudoModifView.setPadding(10, 0,0,0);
                    dateModifView.setPadding(10, 0,10,0);
                    modificateursView.addView(dateModifView);
                    modificateursView.addView(pseudoModifView);
                }
            } else {
                Log.d("ArtifactArticleActivity", "La liste des modificateurs est nulle");
            }
        } catch (JSONException e) {
            Log.e("ArtifactArticleActivity", "error decoding artifact moficateurs",e);
        } catch (ParseException e) {
            Log.e("ArtifactArticleActivity", "error decoding creation or modification date",e);
        }
        titleTextView.setText(title);
        messageTextView.setText(message);

    }
}
