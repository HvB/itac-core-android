package fr.learning_adventure.android.itac.android_app_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.model.Learner;
import fr.learning_adventure.android.itac.widget.Clink;


/**
 * Created by learninglab on 03/03/16.
 */
public class EspacePersonnelActivity extends ActionBarActivity {
    private static Socket socket;
    private final static String FILE_URI_SOCKET = "uri_socket.txt";
    Boolean connected = true;
    ListView mListView;
    List<String> messages = new ArrayList<String>();
    Learner learner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espacepersonnel);


        //récuperer le pseudo et l'afficher
        Intent intent = getIntent();
        final String pseudo = intent.getStringExtra("pseudoName");
        EspacePersonnelActivity.this.setPseudo(pseudo);
        TextView pseudoTextView = (TextView) findViewById(R.id.pseudo_textView);
        pseudoTextView.setText("bienvenue " + pseudo);

        //ajout de message
        final EditText message = (EditText) EspacePersonnelActivity.this.findViewById(R.id.message_input);
        ImageButton button = (ImageButton) this.findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages.add(message.getText().toString());
                socket.emit("message", message.getText().toString());
                message.setText("");

            }
        });


        //Ajouter une liste d'objets
        mListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EspacePersonnelActivity.this,
                android.R.layout.simple_list_item_1, (List<String>) messages);
        mListView.setAdapter(adapter);

        //apel aux méthodes initialize et setinterface: initialiser socket et gerer interface
        initialize();
        setInterface();

    }


    // Création du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Authentification externe
            case R.id.connect:
                Intent intent = new Intent(EspacePersonnelActivity.this, AuthenificationExterneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                EspacePersonnelActivity.this.startActivity(intent);

                return true;

            //accés au parametre de connexion : saisie d'adresse ip et port
            case R.id.parametre:
                Intent i = new Intent(EspacePersonnelActivity.this, ConnexionActivity.class);
                EspacePersonnelActivity.this.startActivity(i);

                return true;


            case R.id.quitter:
                //Pour fermer l'application
                finish();
                System.exit(0);
                return true;
        }
        return false;
    }


    //lire depuis le fichier FILI_URI_SOCKET
    private String getUriSocket() {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                FILE_URI_SOCKET);
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

    //Creation et connection de Socket
    public void initialize() {


        try {
            socket = IO.socket(getUriSocket().toString());
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("Socket", "connection");
                    socket.emit("new_user", EspacePersonnelActivity.this.getPseudo());


                }
            });
            socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {





                    Log.i("Socket", "message");
                }
            });
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("Socket", "timeout");
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("Socket", "disconnection " + args[0]);
                }
            });
            socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("Socket", "error " + args[0]);
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    //gerer interface selon état de connexion
    public void setInterface() {

        LinearLayout zepLayout = (LinearLayout) findViewById(R.id.zep_layout);
        Button login_logout_btn = (Button) this.findViewById(R.id.login_logout_btn);
        if (socket.connected() && connected == true) {

            zepLayout.setBackgroundColor(Color.GREEN);
            login_logout_btn.setText("Se déconnecter");
            login_logout_btn.setBackgroundColor(Color.RED);
            login_logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    socket.disconnect();
                    connected = false;
                    setInterface();
                }


            });

        } else {
            zepLayout.setBackgroundColor(Color.RED);
            login_logout_btn.setBackgroundColor(Color.GREEN);
            login_logout_btn.setText("Se connecter");
            login_logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    connected = true;
                    initialize();
                    if (socket.connected() == false) {
                        Clink.show(EspacePersonnelActivity.this, "réessayez une autre fois si non configurer l'adresse ip et le port depuis le menu paramètres de connexion");
                    }
                    setInterface();
                }

            });

        }
    }

    //get & set pseudo, ip
    String pseudo;
    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }


}