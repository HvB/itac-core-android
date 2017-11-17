package fr.learning_adventure.android.itac.android_app_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.model.Artifact;

/**
 * Created by yassine on 12/04/2016.
 */
public class ArtifactImageActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artifact_image);

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat fmtDateCreation = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
        DateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String date = getIntent().getStringExtra("date");
        String contenu = getIntent().getStringExtra("image");
        String pseudo = getIntent().getStringExtra("pseudo");
        String created = getIntent().getStringExtra("created");


        TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        try {
            String dateCreation = fmtDateCreation.format(fmt.parse(date));
            pseudoView.setText("Crée par " + pseudo + " le " + dateCreation);
        } catch (ParseException e) {
            Log.e("ArtifactImageActivity", "error decoding creation date",e);
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        if (created.equals("true")) {
            Bitmap bmOrigine = BitmapFactory.decodeFile(contenu);

            if (bmOrigine.getHeight() > 2048 && bmOrigine.getWidth() > 2048){
                Bitmap bm = BitmapFactory.decodeFile(contenu,options);
                imageView.setImageBitmap(bm);
            }else {
                imageView.setImageBitmap(bmOrigine);
            }
        } else {
            Bitmap decodedByte = Artifact.decodeImage(contenu);
            if (decodedByte.getHeight() > 2048 && decodedByte.getWidth() > 2048){
                decodedByte = Artifact.decodeImage(contenu, options);
                imageView.setImageBitmap(decodedByte);
            } else {
                imageView.setImageBitmap(decodedByte);
            }
        }
    }

}
