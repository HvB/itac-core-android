package fr.learning_adventure.android.itac.android_app_activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.adapter.ArtifactAdapter;
import fr.learning_adventure.android.itac.adapter.AvatarAdapter;
import fr.learning_adventure.android.itac.model.Artifact;
import fr.learning_adventure.android.itac.model.Modificateurs;
import fr.learning_adventure.android.itac.model.PassObject;
import fr.learning_adventure.android.itac.widget.Clink;
import fr.learning_adventure.android.itac.widget.LinearLayoutAbsListView;

/**
 * Created by learninglab on 03/03/16.
 */
public class EspacePersonnelActivity extends ActionBarActivity {
    private  Socket socket;
    private final static String FILE_URI_SOCKET = "uri_socket.txt";
    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_CAMERA_haute =1;
    private static int REQUEST_CAMERA_moyenne =0;
    private    ContentValues values;
    private Uri imageUri;

    private GridView listArtifactView;
    private GridView listArtifactZEPView;
    private LinearLayoutAbsListView listArtifactLayout, artifactZEPLayout;
    private RelativeLayout optionsArtifactLayout;
    private List<Artifact> listArtifact = new ArrayList<>();
    private ArtifactAdapter artifactAdapter = new ArtifactAdapter(this, listArtifact);
    private List<Artifact> listArtifactZEP = new ArrayList<>();
    private ArtifactAdapter artifactZEPAdapter = new ArtifactAdapter(this, listArtifactZEP);
    private int selectedPosition;
    private String idZEP;
    private String idZE;
    private boolean connected = false;

    //get & set pseudo, ip
    private String pseudo;

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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

    //retourner si l'imageview contient une image ou non
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }
        return hasImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_espacepersonnel);

        //récuperer le pseudo
        Intent intent = getIntent();
        final String pseudo = intent.getStringExtra("pseudoName");
        EspacePersonnelActivity.this.setPseudo(pseudo);
        // recuperation info avatar
        selectedPosition = intent.getExtras().getInt("avatarPosition");
        AvatarAdapter imageAdapter = new AvatarAdapter(this);

        ImageView imageView = (ImageView) findViewById(R.id.imageAvatar);
        imageView.setImageResource(imageAdapter.mThumbIds[selectedPosition]);
        TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        pseudoView.setText(pseudo);
        final LinearLayout trashLayout = (LinearLayout) findViewById(R.id.trashLayout);
        final LinearLayout editLayout = (LinearLayout) findViewById(R.id.editLayout);
        final LinearLayout zPLayout = (LinearLayout) findViewById(R.id.zp_Layout);
        final LinearLayout trashEditLayout = (LinearLayout) findViewById(R.id.trashEditLayout);
        final RelativeLayout espacePersonnelLayout = (RelativeLayout) findViewById(R.id.espacePersonnelLayout);
        final EditText titre = (EditText) EspacePersonnelActivity.this.findViewById(R.id.titre);
        final EditText message = (EditText) EspacePersonnelActivity.this.findViewById(R.id.message_input);
        final RelativeLayout artifactLayout = (RelativeLayout) this.findViewById(R.id.artifact);
        final RelativeLayout optionsArtifactLayout = (RelativeLayout) this.findViewById(R.id.optionsArtifactLayout);
        final Button modifiedButton = (Button) this.findViewById(R.id.send_modified_button);
        final Button button = (Button) this.findViewById(R.id.send_button);
        final RelativeLayout zepLayout = (RelativeLayout) findViewById(R.id.zep_layout);
        final ImageButton logout_btn = (ImageButton) this.findViewById(R.id.logout_btn);
        final ImageButton login_btn = (ImageButton) this.findViewById(R.id.login_btn);

        //mettre arriere plans transparent
        espacePersonnelLayout.getBackground().setAlpha(200);

        //initialiser socket
        initializeWebSocket();

        //Boutton qui permet de gerer la deconnexion du serveur
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listArtifactZEP.size() > 0) {
                    Clink.show(EspacePersonnelActivity.this, "votre zone d'échange contient des élèments, veuiller la vider pour se déconnecter");
                } else {
                    Log.i("btn logout", "deconnection du serveur...");
                    closeWebSocket();
                }
            }

        });

        //Boutton qui permet de gerer la reconnexion au serveur
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("btn login", "socket tentative de connection...");
                initializeWebSocket();
            }
        });

        // Action Drop Artifact
        final OnDragListener myOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED :
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED :
                        break;
                    case DragEvent.ACTION_DRAG_EXITED :
                        break;
                    case DragEvent.ACTION_DROP :
                        PassObject passObj = (PassObject) event.getLocalState();
                        final int position = passObj.position;
                        View view = passObj.view;
                        final Artifact passedItem = passObj.artifact;
                        final List<Artifact> srcList = passObj.srcList;
                        AbsListView oldParent = (AbsListView) view.getParent();
                        ArtifactAdapter srcAdapter = (ArtifactAdapter) oldParent.getAdapter();

                        LinearLayoutAbsListView newParent = (LinearLayoutAbsListView) v;
                        ArtifactAdapter destAdapter = (ArtifactAdapter) (newParent.absListView.getAdapter());
                        final List<Artifact> destList = destAdapter.getList();

                        if (srcList != destList) {
                            if (destList == listArtifactZEP) {
                                if (login_btn.getVisibility() == View.VISIBLE) {
                                    Clink.show(EspacePersonnelActivity.this, "veuillez vous connecter");
                                } else {
                                    srcList.remove(position);
                                    //destList.add(passedItem);
                                    passedItem.setProprietaire(pseudo);
                                    passedItem.setTypeConteneur("ZE");
                                    passedItem.setIdConteneur(idZE);
                                    if (passedItem.getType().equals("message")) {
                                        socket.emit("EVT_NewArtefactInZE", pseudo, idZEP, idZE, passedItem.toJSONMessage().toString());
                                    } else {
                                        socket.emit("EVT_NewArtefactInZE", pseudo, idZEP, idZE, passedItem.toJSONImage().toString());
                                    }
                                    Log.i("myOnDragListener", "EVT_NewArtefactInZE : "+ pseudo + ", "+idZEP+", "+ idZE);
                                }
                            }
                        }

                        srcAdapter.notifyDataSetChanged();
                        destAdapter.notifyDataSetChanged();

                        //smooth scroll to bottom
                        newParent.absListView.smoothScrollToPosition(destAdapter.getCount() - 1);
                        zPLayout.setVisibility(View.GONE);
                        trashEditLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        passObj = (PassObject) event.getLocalState();
                        view = passObj.view;
                        view.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        };

        // Action Drop Artifact
        final OnDragListener myArtefactOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if (v != espacePersonnelLayout) {
                            v.setBackgroundColor(Color.parseColor("#70eac8"));
                        }
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        if (v != espacePersonnelLayout) {
                            v.setBackgroundColor(Color.parseColor("#323232"));
                        }
                        break;
                    case DragEvent.ACTION_DROP:
                        PassObject passObj = (PassObject) event.getLocalState();
                        int position = passObj.position;
                        View view = passObj.view;
                        final Artifact passedItem = passObj.artifact;
                        List<Artifact> srcList = passObj.srcList;

                        AbsListView oldParent = (AbsListView) view.getParent();
                        ArtifactAdapter srcAdapter = (ArtifactAdapter) oldParent.getAdapter();
                        if (v == trashLayout && srcList == listArtifact) {
                            srcList.remove(position);
                        }
                        else if ((v == editLayout && passedItem.getType().equals("image")) && srcList == listArtifact) {
                            Clink.show(EspacePersonnelActivity.this, "Ce type n'est pas modifiable");
                        }
                        else if ((v == editLayout && passedItem.getType().equals("message")) && srcList == listArtifact) {
                            titre.setText(passedItem.getTitle());
                            message.setText(passedItem.getContenu());
                            artifactLayout.setVisibility(View.VISIBLE);
                            button.setVisibility(View.GONE);
                            modifiedButton.setVisibility(View.VISIBLE);
                            modifiedButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (titre.getText().toString().equals("")) {
                                        Clink.show(EspacePersonnelActivity.this, "veuillez saisir le titre de l'article");
                                    } else if (titre.getText().toString().length() > 25) {
                                        Clink.show(EspacePersonnelActivity.this, "le titre de l'article est trés grand");
                                    } else if ((message.getText().toString().equals(""))) {
                                        Clink.show(EspacePersonnelActivity.this, "veuillez saisir un message");
                                    } else {
                                        if (!(passedItem.getCreator().isEmpty())) {
//                                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
//                                            String date = df.format(Calendar.getInstance().getTime());
//                                            Modificateurs mod = new Modificateurs(pseudo, date);
//                                            if (passedItem.getModificateurs().isEmpty()) {
//                                                List<Modificateurs> listModificateurs = new ArrayList<>();
//                                            }
//                                            List<Modificateurs> listModificateurs = passedItem.getModificateurs();
//                                            listModificateurs.add(mod);
                                            // passedItem.setModificateurs(listModificateurs);
                                            passedItem.setTitle(titre.getText().toString());
                                            passedItem.setContenu(message.getText().toString());
                                            // passedItem.setDateDerniereModification(date);
                                            artifactAdapter.notifyDataSetChanged();
                                            message.setText("");
                                            titre.setText("");
                                            artifactLayout.setVisibility(View.INVISIBLE);
                                            modifiedButton.setVisibility(View.GONE);
                                            button.setVisibility(View.VISIBLE);
                                            hideSoftKeyboard(EspacePersonnelActivity.this);
                                        }
                                    }
                                }
                            });
                        } else if (v == zPLayout) {
                            if (login_btn.getVisibility() == View.VISIBLE) {
                                Clink.show(EspacePersonnelActivity.this, "veuillez vous connecter");
                            }
                            else if (srcList == listArtifactZEP) {
                                Clink.show(EspacePersonnelActivity.this, "cette action est disponible depuis la table");
                            }
                            else {
                                srcList.remove(position);
                                if (passedItem.getType().equals("message")) {
                                    Log.i("art : ", passedItem.toJSONMessage().toString());
                                    socket.emit("EVT_NewArtefactInZP", pseudo, idZEP, idZE, passedItem.toJSONMessage().toString());
                                } else {
                                    socket.emit("EVT_NewArtefactInZP", pseudo, idZEP, idZE, passedItem.toJSONImage().toString());
                                    Log.i("art : ", passedItem.toJSONMessage().toString());
                                }
                                Log.i("myArtefactOnDrag", "NewArtefactInZP : "+ pseudo + ", "+idZEP+", "+ idZE);
                            }
                        } else if (v == espacePersonnelLayout && (srcList != listArtifact)) {
                            if (passedItem.getType().equals("message")) {
                                socket.emit("EVT_Envoie_ArtefactdeZEversEP", passedItem.getIdAr(), idZE, idZEP);
                            } else {
                                socket.emit("EVT_Envoie_ArtefactdeZEversEP", passedItem.getIdAr(), idZE, idZEP);
                            }
                            Log.i("myArtefactOnDrag", "EVT_Envoie_ArtefactdeZEversEP : "+ pseudo + ", "+idZEP+", "+ idZE);
                            srcList.remove(position);
                            listArtifact.add(passedItem);
                            artifactAdapter.notifyDataSetChanged();
                        }

                        srcAdapter.notifyDataSetChanged();
                        trashEditLayout.setVisibility(View.GONE);
                        zPLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (v != espacePersonnelLayout) {
                            v.setBackgroundColor(Color.parseColor("#323232"));
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        };

        //drag artifact on long clic
        AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                           int position, long id) {
                view.setBackgroundColor(Color.parseColor("#70eac8"));
                Artifact selectedItem = (Artifact) (parent.getItemAtPosition(position));
                ArtifactAdapter associatedAdapter = (ArtifactAdapter) (parent.getAdapter());
                final List<Artifact> associatedList = associatedAdapter.getList();
                for (int i=0;i<associatedList.size();i++) {
                   if( associatedList.get(i)!=selectedItem) {
                        associatedAdapter.getView(i,null,parent).setBackgroundResource(0);
                    }
                }

                final PassObject passObj = new PassObject(view, selectedItem, associatedList, position);
                view.setOnTouchListener((new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            view.setBackgroundResource(0);
                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                            view.startDrag(data, shadowBuilder, passObj, 0);
                            view.setVisibility(View.INVISIBLE);
                            return true;
                        } else {
                            return false;
                        }
                      }
                }));
                zPLayout.setVisibility(View.VISIBLE);
                if (associatedList == listArtifact) {
                    trashEditLayout.setVisibility(View.VISIBLE);
                    optionsArtifactLayout.setVisibility(View.GONE);
                }
            }

        };

        //gestion des grid view
        listArtifactZEPView = (GridView) findViewById(R.id.listArtifactZEPView);
        artifactZEPLayout = (LinearLayoutAbsListView) findViewById(R.id.artifactZEPLayout);
        listArtifactView = (GridView) findViewById(R.id.listArtifactView);
        listArtifactLayout = (LinearLayoutAbsListView) findViewById(R.id.listArtifactLayout);

        listArtifactLayout.setOnDragListener(myOnDragListener);
        artifactZEPLayout.setOnDragListener(myOnDragListener);
        trashLayout.setOnDragListener(myArtefactOnDragListener);
        editLayout.setOnDragListener(myArtefactOnDragListener);
        zPLayout.setOnDragListener(myArtefactOnDragListener);
        espacePersonnelLayout.setOnDragListener(myArtefactOnDragListener);

        listArtifactLayout.setAbsListView(listArtifactView);
        artifactZEPLayout.setAbsListView(listArtifactZEPView);

        listArtifactZEPView.setAdapter(artifactZEPAdapter);
        listArtifactView.setAdapter(artifactAdapter);

        listArtifactZEPView.setOnItemClickListener(myOnItemClickListener);
        listArtifactView.setOnItemClickListener(myOnItemClickListener);

        //Affichage de l'artifact
        listArtifactView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                Artifact artifact = (Artifact) parent.getItemAtPosition(position);
                if (artifact.getType().equals("message")) {
                    Intent intent = new Intent(EspacePersonnelActivity.this, ArtifactArticleActivity.class);
                    intent.putExtra("title", artifact.getTitle());
                    intent.putExtra("message", artifact.getContenu());
                    intent.putExtra("pseudo", artifact.getCreator());
                    intent.putExtra("date", artifact.getDateCreation());
                    intent.putExtra("dateDerniereModification", artifact.getDateDerniereModification());
                    intent.putExtra("modificateurs", (Serializable) artifact.getModificateurs());
                    intent.putExtra("avatarPosition", selectedPosition);
                    //Start details activity
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(EspacePersonnelActivity.this, ArtifactImageActivity.class);
                    intent.putExtra("pseudo", artifact.getCreator());
                    intent.putExtra("image", artifact.getContenu());
                    intent.putExtra("date", artifact.getDateCreation());
                    intent.putExtra("created", artifact.getCreated());
                    startActivity(intent);
                }
                return true;
            }
        });

        //gestion de l'affichage du layout d'ajout aartifact
        final Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        final Button buttonTakeImage = (Button) this.findViewById(R.id.buttonTakePicture);
        final Button addArtifactBtn = (Button) this.findViewById(R.id.addArtifact);
        addArtifactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artifactLayout.setVisibility(View.VISIBLE);
            }
        });

        //ajout de article
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titre.getText().toString().equals("")) {
                    Clink.show(EspacePersonnelActivity.this, "veuillez saisir le titre de l'article");
                } else if (titre.getText().toString().length() > 25) {
                    Clink.show(EspacePersonnelActivity.this, "le titre de l'article est trés grand");
                } else if ((message.getText().toString().equals(""))) {
                    Clink.show(EspacePersonnelActivity.this, "veuillez saisir un message");
                } else {
                    hideSoftKeyboard(EspacePersonnelActivity.this);
                    Artifact artefact = new Artifact(getPseudo());
                    artefact.setTitle(titre.getText().toString());
                    artefact.setContenu(message.getText().toString());
                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    artefact.setDateCreation(date);
                    artefact.setType("message");
                    List<Modificateurs> listModificateurs = new ArrayList<>();
                    artefact.setModificateurs(listModificateurs);
                    listArtifact.add(artefact);
                    artifactAdapter.notifyDataSetChanged();
                    if (listArtifactView.getHeight() > 400) {
                        listArtifactView.getLayoutParams().height = 400;
                    }
                    message.setText("");
                    titre.setText("");
                    artifactLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button exitButton = (Button) this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(EspacePersonnelActivity.this);
                message.setText("");
                titre.setText("");
                artifactLayout.setVisibility(View.INVISIBLE);
            }
        });

        //parcours gallery et selection image
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        buttonTakeImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(REQUEST_CAMERA_haute == 0) {
                    values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_CAMERA_haute);
                }
                else {
                    startActivityForResult(intent, REQUEST_CAMERA_moyenne);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        closeWebSocket();
        super.onDestroy();
    }

    // Création du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        switch (item.getItemId()) {
            //accés au parametre de connexion : saisie d'adresse ip et port
            case R.id.parametre:
                // fermeture de la connection actuelle
                closeWebSocket();
                // saisie nouvelle URL
                Intent i = new Intent(EspacePersonnelActivity.this, ConnexionActivity.class);
                i.putExtra("uri", getUriSocket());
                EspacePersonnelActivity.this.startActivity(i);
                return true;
            case R.id.hauteResolution:
                REQUEST_CAMERA_haute= 0;
                REQUEST_CAMERA_moyenne= 1;
                return true;
            case R.id.moyenneResolution:
                REQUEST_CAMERA_haute= 1;
                REQUEST_CAMERA_moyenne=0;
                return true;
            case R.id.quitter:
                //Pour fermer l'application
                finish();
                return true;
        }
        return false;
    }

    private void onDisconnection(){
        final RelativeLayout zepLayout = (RelativeLayout) findViewById(R.id.zep_layout);
        final ImageButton logout_btn = (ImageButton) this.findViewById(R.id.logout_btn);
        final ImageButton login_btn = (ImageButton) this.findViewById(R.id.login_btn);
        Log.i("onDisconnection", "on ete deconnecte du serveur...");
        connected=false;
        // on met a jour l'interface...
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logout_btn.setVisibility(View.GONE);
                login_btn.setVisibility(View.VISIBLE);
                zepLayout.setBackgroundResource(R.drawable.rounded_corner_red);
            }
        });
    }
    //Creation de la WebSocket et lancement de la connexion
    private void initializeWebSocket() {
        final RelativeLayout zepLayout = (RelativeLayout) findViewById(R.id.zep_layout);
        final ImageButton logout_btn = (ImageButton) this.findViewById(R.id.logout_btn);
        final ImageButton login_btn = (ImageButton) this.findViewById(R.id.login_btn);
        try {
            Log.i("WebSocket URL", getUriSocket());
            // creation de la socket
            if (socket != null){
                Log.i("initializeWebSocket", "reutilisation de la socket existante");
            } else {
                Log.i("initializeWebSocket", "creation de la socket");
                socket = IO.socket(getUriSocket());

                // gestion des evenements lies au foctionnement de la websocket
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "connection");
                        socket.emit("EVT_DemandeConnexionZEP", pseudo, String.valueOf(selectedPosition));
                        Log.i("initializeWebSocket", "EVT_DemandeConnexionZEP : " + pseudo + ", " + String.valueOf(selectedPosition));
                    }
                });
                socket.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "reconnecting");
                    }
                });
                socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "message");
                    }
                });
                socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "connexion error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Clink.show(EspacePersonnelActivity.this, "veuillez verifier les parametres de connexion");
                            }
                        });
                    }
                });
                socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "timeout");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Clink.show(EspacePersonnelActivity.this, "veuillez verifier les parametres de connexion");
                            }
                        });
                    }
                });
                socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "disconnection " + args[0]);
                        EspacePersonnelActivity.this.onDisconnection();
                    }
                });
                socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "error " + args[0]);

                    }
                });

                // gestion des evenements lies au fonctionnement du serveur ITAC
                //envoie artefact de ZE vers ZP
                socket.on("EVT_Envoie_ArtefactdeZEversZP", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        //final JSONObject object = (JSONObject) args[0];
                        final String id = ("" + args[0]);
                        EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < listArtifactZEP.size(); i++) {
                                    Artifact art = listArtifactZEP.get(i);
                                    if (art.getIdAr().equals(id)) {
                                        listArtifactZEP.remove(i);
                                        artifactZEPAdapter.notifyDataSetChanged();
                                    }
                                }
                                Log.i("evt", "EVT_Envoie_ArtefactdeZEversZP : " + id);
                            }
                        });
                    }
                });

                //envoie artefact de ZP vers ZE
                socket.on("EVT_Envoie_ArtefactdeZPversZE", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String data = (String) args[0];
                        EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject object = null;
                                try {
                                    object = new JSONObject(data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Artifact artifact = new Artifact(object);
                                artifact.setCreated("false");
                                listArtifactZEP.add(artifact);
                                artifactZEPAdapter.notifyDataSetChanged();
                                //Log.i("artifact :",artifact.toJSONMessage().toString());
                                Log.i("evt", "EVT_Envoie_ArtefactdeZPversZE : " + data);
                            }
                        });
                    }
                });

                //Réponse envoie artefact vers ZE
                socket.on("EVT_ReceptionArtefactIntoZE", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        final String data = (String) args[2];
                        JSONObject object = null;
                        int arId = 0;
                        try {
                            object = new JSONObject(data);
                            arId = object.getInt("idAr");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final int id = arId;
                        Log.i("EVT_NewArtefactInZE : ", "" + id);
                        //listArtifact.add(passedItem);
                        Artifact artifact = new Artifact(object);
                        artifact.setCreated("false");
                        listArtifactZEP.add(artifact);
                        EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //                        listArtifactZEP.get(listArtifactZEP.size() - 1).setIdAr(String.valueOf(id));
                                artifactZEPAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

                //Reception message acquitant la connexion
                socket.on("EVT_ReponseOKConnexionZEP", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        Object ZEP = args[1];
                        idZE = (String) args[0];
                        idZEP = String.valueOf(ZEP);
                        connected = true;
                        // on met a jour l'interface
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                zepLayout.setBackgroundResource(R.drawable.rounded_corner_green);
                                logout_btn.setVisibility(View.VISIBLE);
                                login_btn.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                //Réponse message acquitant un refus de connexion
                socket.on("EVT_ReponseNOKConnexionZEP", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Clink.show(EspacePersonnelActivity.this, "le nombre maximal de connexion au serveur est dépassé");
                            }
                        });
                    }
                });
            }
            // connexion a la websocket
            if (! socket.connected()){
                Log.i("initializeWebSocket", "initialisation de la connection");
                socket.connect();
            } else {
                Log.i("initializeWebSocket", "la socket est deja connectee...");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.i("error", e.toString());
        }
    }

    //Fin de la connexion au srveur ITAC et fermeture de la WebSocket
    private void closeWebSocket() {
        if (socket.connected()) {
            Log.i("closeWebSocket", "deconnection du serveur...");
            socket.emit("EVT_Deconnexion", pseudo, idZE);
            Log.i("closeWebSocket", "EVT_Deconnexion : "+ pseudo + ", " + idZE);
        }
        Log.i("closeWebSocket", "fermeture de la socket");
        socket.disconnect();
        // IMPERATIF : il faut supprimer les listeners attaches a la websocket
        socket.off();
        socket = null;
    }

    //selectionner l'image depuis la galerie ou l'appareil photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //selection de l'image depuis la galerie
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Artifact artifact = new Artifact(getPseudo());
            artifact.setContenu(picturePath);
            artifact.setType("image");
            artifact.setCreated("true");

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);
            artifactAdapter.notifyDataSetChanged();
            if (listArtifactView.getHeight() > 400) {
                listArtifactView.getLayoutParams().height = 400;
            }
        }

        //prendre un photo
        else if (requestCode == REQUEST_CAMERA_moyenne && resultCode == RESULT_OK && null != data) {
            Log.i("cam","moyenne");
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Artifact artifact = new Artifact(getPseudo());
            artifact.setContenu(destination.getAbsolutePath());
            artifact.setType("image");
            artifact.setCreated("true");

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);
            artifactAdapter.notifyDataSetChanged();
            if (listArtifactView.getHeight() > 400) {
                listArtifactView.getLayoutParams().height = 400;
            }
        }
        //prendre un photo
       else if (requestCode == REQUEST_CAMERA_haute && resultCode == RESULT_OK ) {
            Bitmap thumbnail;
            String imageurl = null;
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                imageurl = getRealPathFromURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Artifact artifact = new Artifact(getPseudo());
            artifact.setContenu(imageurl);
            artifact.setType("image");
            artifact.setCreated("true");

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);
            artifactAdapter.notifyDataSetChanged();
            if (listArtifactView.getHeight() > 400) {
                listArtifactView.getLayoutParams().height = 400;
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
