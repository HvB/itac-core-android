/**
 *     Copyright © 2016 Yassine Siela
 *     Copyright © 2016 AIP Primeca RAO
 *     Copyright © 2016-2018 Université Savoie Mont Blanc
 *
 *     ITAC-Core-Android is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        String serverLogin = sharedPreferences.getString(getString(R.string.pref_key_server_login),"anonymous");
        String serverPassword = sharedPreferences.getString(getString(R.string.pref_key_server_password),"");
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i("ConnexionActivity", "server address : "+serverAddr);
        Log.i("ConnexionActivity", "server port : "+serverPort);

        setContentView(R.layout.activity_connexionzc);
        String uri = getIntent().getStringExtra("uri");

        EditText ip = (EditText) ConnexionActivity.this.findViewById(R.id.ip);
        EditText port = (EditText) ConnexionActivity.this.findViewById(R.id.port);
        EditText login = (EditText) ConnexionActivity.this.findViewById(R.id.login);
        EditText password = (EditText) ConnexionActivity.this.findViewById(R.id.password);
        ip.setText(serverAddr);
        port.setText(serverPort);
        login.setText(serverLogin);
        password.setText(serverPassword);
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
                                                  editor.putString(getString(R.string.pref_key_server_login), login.getText().toString());
                                                  editor.putString(getString(R.string.pref_key_server_password), password.getText().toString());
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