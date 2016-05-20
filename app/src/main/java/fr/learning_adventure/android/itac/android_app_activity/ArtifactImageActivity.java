package fr.learning_adventure.android.itac.android_app_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
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
        String contenu = getIntent().getStringExtra("image");
        String pseudo = getIntent().getStringExtra("pseudo");
        String created = getIntent().getStringExtra("created");


        TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        ImageView imageView = (ImageView) findViewById(R.id.image);

        pseudoView.setText("Crée par " + pseudo + " le " + date);
        if(created.equals("true"))
        {
            imageView.setImageBitmap(BitmapFactory.decodeFile(contenu));
        }
        else

        {
            byte[] decodedString = Base64.decode(contenu, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }
    }
}
