package fr.learning_adventure.android.itac.android_app_activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.widget.Clink;


public class ConnexionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddr = sharedPreferences.getString(getString(R.string.pref_key_server_addr),"127.0.0.1");
        String serverPort = sharedPreferences.getString(getString(R.string.pref_key_server_port),"8080");
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i("ConnexionActivity", "server address : "+serverAddr);
        Log.i("ConnexionActivity", "server port : "+serverPort);

        setContentView(R.layout.activity_connexionzc);
        String uri = getIntent().getStringExtra("uri");

        EditText ip = (EditText) ConnexionActivity.this.findViewById(R.id.ip);
        EditText port = (EditText) ConnexionActivity.this.findViewById(R.id.port);
        ip.setText(serverAddr);
        port.setText(serverPort);
        Button button = (Button) this.findViewById(R.id.loginbtn);
        TextView uriTextView = (TextView) this.findViewById(R.id.uri);
        uriTextView.setText("l'adresse actuelle : "+uri);

        //saisie de l'adresse IP et port, modification des preferences, retour à EspacePersonnelActivity
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
                                              try {
                                                  short val = Short.parseShort(port.getText().toString());
                                                  editor.putString(getString(R.string.pref_key_server_addr), ip.getText().toString());
                                                  editor.putString(getString(R.string.pref_key_server_port), port.getText().toString());
                                                  boolean ok = editor.commit();
                                                  if (ok) {
                                                      ConnexionActivity.this.finish();
                                                  } else {
                                                      Log.i("ConnectionActivity","probleme lors de la validation des donnees");
                                                  }
                                              } catch (NumberFormatException e){
                                                  Log.i("ConnectionActivity","port number is not valid");
                                                  Clink.show(ConnexionActivity.this, "veuillez saisir un numero de port valide");
                                              }
                                          }
                                      }
                                  }
        );
    }
}