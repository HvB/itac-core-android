package fr.learning_adventure.android.itac.android_app_activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.TextView;

import fr.learning_adventure.android.itac.R;

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

        TextView pseudoView =(TextView) findViewById(R.id.pseudo);
        EditText titleTextView = (EditText) findViewById(R.id.titre);
        EditText messageTextView = (EditText) findViewById(R.id.message_input);

        titleTextView.setText(title);
        messageTextView.setText(message);
        pseudoView.setText("Crée par "+pseudo+" le "+date);

    }
}
