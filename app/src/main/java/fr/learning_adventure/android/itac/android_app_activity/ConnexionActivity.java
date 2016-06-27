package fr.learning_adventure.android.itac.android_app_activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.widget.Clink;


public class ConnexionActivity extends Activity {
    private final static String FILE_URI_SOCKET = "uri_socket.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexionzc);
        String uri = getIntent().getStringExtra("uri");

        Button button = (Button) this.findViewById(R.id.loginbtn);
        TextView uriTextView = (TextView) this.findViewById(R.id.uri);
        uriTextView.setText("l'adresse actuelle : "+uri);

        //saisie de l'adresse IP et port, ajout dans le fichier, retour à EspacePersonnelActivity
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          EditText ip = (EditText) ConnexionActivity.this.findViewById(R.id.ip);
                                          EditText port = (EditText) ConnexionActivity.this.findViewById(R.id.port);

                                          if (ip.getText().toString().equals("")) {
                                              Clink.show(ConnexionActivity.this, "veuillez saisir l'adresse IP");
                                          } else if (port.getText().toString().equals("")) {
                                              Clink.show(ConnexionActivity.this, "veuillez saisir le numero de port");
                                          } else {
                                              ecrireFicher("http://" + ip.getText().toString() + ":" + port.getText().toString());
                                              ConnexionActivity.this.finish();
                                          }

                                      }
                                  }

        );

    }

    private void ecrireFicher(String monText) {

        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                FILE_URI_SOCKET);
        BufferedWriter writer = null;
        try {
            FileWriter out = new FileWriter(file);
            writer = new BufferedWriter(out);
            writer.write(monText);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}