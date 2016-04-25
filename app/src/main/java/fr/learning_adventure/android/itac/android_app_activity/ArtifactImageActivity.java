package fr.learning_adventure.android.itac.android_app_activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import fr.learning_adventure.android.itac.R;

/**
 * Created by yassine on 12/04/2016.
 */
public class ArtifactImageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artifact_image);

        String date = getIntent().getStringExtra("date");
        String imagepath = getIntent().getStringExtra("image");
        String pseudo = getIntent().getStringExtra("pseudo");


        TextView pseudoView =(TextView) findViewById(R.id.pseudo);
        ImageView imageView = (ImageView) findViewById(R.id.image);

        pseudoView.setText("Crée par "+pseudo+" le "+date);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagepath));
    }
}
