package fr.learning_adventure.android.itac.android_app_activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.model.Modificateurs;

/**
 * Created by yassine on 12/04/2016.
 */
public class ArtifactArticleActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artifact_article);

        String title = getIntent().getStringExtra("title");
        String pseudo = getIntent().getStringExtra("pseudo");
        String message = getIntent().getStringExtra("message");
        String date = getIntent().getStringExtra("date");
        List<Modificateurs> listModificateurs = (List<Modificateurs>) getIntent().getSerializableExtra("modificateurs");

        LinearLayout modificateursLayout = (LinearLayout)findViewById(R.id.modificateurs);
        TextView pseudoView =(TextView) findViewById(R.id.pseudo);
        TextView titleTextView = (TextView) findViewById(R.id.titre);
        TextView messageTextView = (TextView) findViewById(R.id.message_input);

        for(int i=0;i<listModificateurs.size();i++)
        {   TextView modificateurTextView = new TextView(ArtifactArticleActivity.this);
            modificateurTextView.setText("modifié par " + listModificateurs.get(i).getModificateur() + "  le " + listModificateurs.get(i).getDateModification());
            modificateursLayout.addView(modificateurTextView);
        }


        titleTextView.setText(title);
        messageTextView.setText(message);
        pseudoView.setText("Crée par "+pseudo+" le "+date);

    }
}
