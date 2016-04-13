package fr.learning_adventure.android.itac.android_app_activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

        TextView titleTextView = (TextView) findViewById(R.id.titre);
        TextView pseudoTextView = (TextView) findViewById(R.id.pseudo);
        TextView messageTextView = (TextView) findViewById(R.id.message_input);

        titleTextView.setText(title);
        pseudoTextView.setText(pseudo);
        messageTextView.setText(message);
    }
}
