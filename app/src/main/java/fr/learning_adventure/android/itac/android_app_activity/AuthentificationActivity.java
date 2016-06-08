package fr.learning_adventure.android.itac.android_app_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.adapter.AvatarAdapter;
import fr.learning_adventure.android.itac.widget.Clink;

public class AuthentificationActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        final AvatarAdapter avatarAdapter = new AvatarAdapter(this);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(avatarAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                v.setBackgroundColor(Color.parseColor("#11db14"));
                avatarAdapter.setSelectedPosition(position);
                for (int i = 0; i < 8; i++) {
                    if (avatarAdapter.getSelectedPosition() != i) {
                        gridview.getChildAt(i).setBackgroundColor(Color.parseColor("#e9e8dd"));
                    }
                }
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
                                              intent.putExtra("avatarPosition", avatarAdapter.getSelectedPosition());
                                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                              AuthentificationActivity.this.startActivity(intent);
                                          }

                                      }
                                  }

        );


    }



}

