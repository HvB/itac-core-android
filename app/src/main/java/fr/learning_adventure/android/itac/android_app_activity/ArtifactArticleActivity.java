package fr.learning_adventure.android.itac.android_app_activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.adapter.AvatarAdapter;

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
        final int selectedPosition = getIntent().getExtras().getInt("avatarPosition");
        AvatarAdapter imageAdapter = new AvatarAdapter(this);

        ImageView imageView = (ImageView) findViewById(R.id.imageAvatar);
        imageView.setImageResource(imageAdapter.mThumbIds[selectedPosition]);
        TextView pseudoView =(TextView) findViewById(R.id.pseudo);
        pseudoView.setText(pseudo);

        EditText titleTextView = (EditText) findViewById(R.id.titre);
        EditText messageTextView = (EditText) findViewById(R.id.message_input);

        titleTextView.setText(title);
        messageTextView.setText(message);
    }
}
