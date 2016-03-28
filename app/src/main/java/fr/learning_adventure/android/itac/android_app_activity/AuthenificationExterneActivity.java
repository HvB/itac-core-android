package fr.learning_adventure.android.itac.android_app_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.widget.Clink;

/**
 * Created by learninglab on 18/03/16.
 */
public class AuthenificationExterneActivity extends ActionBarActivity{

    private final static String FILE_Annuaire = "annuaire.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentiicationexterne);




        Button button = (Button) this.findViewById(R.id.loginbtn);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          EditText pseudoText = (EditText) AuthenificationExterneActivity.this.findViewById(R.id.pseudo);
                                          EditText loginText = (EditText) AuthenificationExterneActivity.this.findViewById(R.id.login);
                                          EditText passwordText = (EditText) AuthenificationExterneActivity.this.findViewById(R.id.password);


                                          //saisie des coordonnés personnels : à ajouter les controles de saisie
                                          if (pseudoText.getText().toString().equals("")) {
                                              Clink.show(AuthenificationExterneActivity.this, "veuillez saisir un pseudo");
                                          }
                                          if (loginText.getText().toString().equals("")) {
                                              Clink.show(AuthenificationExterneActivity.this, "veuillez saisir un login");
                                          }
                                          if (passwordText.getText().toString().equals("")) {
                                              Clink.show(AuthenificationExterneActivity.this, "veuillez saisir un mot de passe");
                                          }
                                          else {
                                              //passer à l'activité connexion en envoyant le parametre pseudo
                                              Intent intent = new Intent(AuthenificationExterneActivity.this,EspacePersonnelActivity.class);
                                              intent.putExtra("pseudoName",pseudoText.getText().toString());
                                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                              AuthenificationExterneActivity.this.startActivity(intent);
                                          }

                                      }
                                  }

        );


    }


    private String getUriSocket() {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                FILE_Annuaire);
        StringBuilder uriSocket = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            uriSocket.append(br.readLine());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uriSocket.toString();
    }
}

