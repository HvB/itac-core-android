package fr.learning_adventure.android.itac.android_app_activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.widget.Clink;

import fr.learning_adventure.android.itac.model.Learner;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

public class AuthentificationActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);




                Button button = (Button) this.findViewById(R.id.loginbtn);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText pseudoText = (EditText) AuthentificationActivity.this.findViewById(R.id.pseudo);


                        //saisie des coordonnés personnels : à ajouter les controles de saisie
                        if (pseudoText.getText().toString().equals("")) {
                            Clink.show(AuthentificationActivity.this, "veuillez saisir un pseudo");
                        }
                        else {
                            //passer à l'activité connexion en envoyant le parametre pseudo
                            Intent intent = new Intent(AuthentificationActivity.this,EspacePersonnelActivity.class);
                            intent.putExtra("pseudoName",pseudoText.getText().toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            AuthentificationActivity.this.startActivity(intent);
                        }

                    }
                }

                );


        }
    }

