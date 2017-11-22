/**
 *     Copyright © 2016 Yassine Siela
 *     Copyright © 2016 AIP Primeca RAO
 *     Copyright © 2016-2017 Université Savoie Mont Blanc
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.util.UUID;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.adapter.AvatarAdapter;
import fr.learning_adventure.android.itac.widget.Clink;

public class AuthentificationActivity extends ActionBarActivity {

    int zoneEchangePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_authentification);


        // demarrage vi une uri dugenre itac://host:port
        // recuperation des infos et enregistrement dans les preferences
        Intent intent = this.getIntent();
        if (intent != null && intent.getData() != null){
            Uri uri = intent.getData();
            String host = uri.getHost();
            int port = uri.getPort();
            if (port == -1) port=8080;
            if ((host != null) && (! "".equals(host)) && (port != -1)){
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(getString(R.string.pref_key_server_addr), host);
                editor.putString(getString(R.string.pref_key_server_port), Integer.toString(port));
                editor.commit();
            }
        }

        final AvatarAdapter avatarAdapter = new AvatarAdapter(this);

        final GridView gridview = (GridView) findViewById(R.id.avatarGridView);
        gridview.setAdapter(avatarAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                avatarAdapter.setSelectedPosition(position);
                avatarAdapter.notifyDataSetChanged();
//                v.setBackgroundResource(R.drawable.rounded_corner_green);
//                for (int i = 0; i < 8; i++) {
//                    if (avatarAdapter.getSelectedPosition() != i) {
//                        gridview.getChildAt(i).setBackgroundResource(0);
//                    }
//                }
            }
        });


        Button button = (Button) this.findViewById(R.id.loginbtn);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          EditText pseudoText = (EditText) AuthentificationActivity.this.findViewById(R.id.pseudo);


                                          //saisie des coordonnés personnels : à ajouter les controles de saisie
                                          if (pseudoText.getText().toString().equals("")) {
                                              Clink.show(AuthentificationActivity.this, "veuillez saisir un pseudo");
                                          } else if (avatarAdapter.getSelectedPosition() < 0) {
                                              Clink.show(AuthentificationActivity.this, "veuillez choisir un avatar");

                                          } else {
                                              //passer à l'activité connexion en envoyant le parametre pseudo
                                              Intent intent = new Intent(AuthentificationActivity.this, EspacePersonnelActivity.class);
                                              intent.putExtra("pseudoName", pseudoText.getText().toString());
                                              intent.putExtra("avatarPosition",avatarAdapter.getSelectedPosition());
                                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                              AuthentificationActivity.this.startActivity(intent);
                                          }

                                      }
                                  }

        );


    }



}

