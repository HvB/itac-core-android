package fr.learning_adventure.android.itac.android_app_activity;

import android.content.ClipData;
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
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
    private static Socket socket;
    private final static String FILE_URI_SOCKET = "uri_socket.txt";
    Boolean connected = true;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_CAMERA = 0;
    GridView listArtifactView;
    GridView listArtifactZEPView;
    LinearLayoutAbsListView listArtifactLayout, artifactZEPLayout;
    RelativeLayout optionsArtifactLayout;
    List<Artifact> listArtifact = new ArrayList<>();
    ArtifactAdapter artifactAdapter = new ArtifactAdapter(this, listArtifact);
    List<Artifact> listArtifactZEP = new ArrayList<>();
    ArtifactAdapter artifactZEPAdapter = new ArtifactAdapter(this, listArtifactZEP);
    private int selectedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_espacepersonnel);


        //récuperer le pseudo
        Intent intent = getIntent();
        final String pseudo = intent.getStringExtra("pseudoName");
        EspacePersonnelActivity.this.setPseudo(pseudo);
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
        final EditText titre = (EditText) EspacePersonnelActivity.this.findViewById(R.id.titre);
        final EditText message = (EditText) EspacePersonnelActivity.this.findViewById(R.id.message_input);
        final RelativeLayout artifactLayout = (RelativeLayout) this.findViewById(R.id.artifact);
        final RelativeLayout optionsArtifactLayout = (RelativeLayout) this.findViewById(R.id.optionsArtifactLayout);
        final ImageButton modifiedButton = (ImageButton) this.findViewById(R.id.send_modified_button);
        final ImageButton button = (ImageButton) this.findViewById(R.id.send_button);

        //apel aux méthodes initialize et setinterface: initialiser socket et gerer interface
        initialize();
        setInterface();


        // Action Drop Artifact
        final OnDragListener myOnDragListener = new OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {


                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:

                        break;
                    case DragEvent.ACTION_DROP:

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
                            srcList.remove(position);
                            destList.add(passedItem);

                            if (destList == listArtifactZEP) {
                                passedItem.setProprietaire(pseudo);
                                passedItem.setTypeConteneur("ZE");
                                passedItem.setIdConteneur("test" + String.valueOf(selectedPosition));
                                if (passedItem.getType() == "message")
                                    socket.emit("EVT_ReceptionArtefactIntoZE", pseudo, String.valueOf(selectedPosition), "test" + String.valueOf(selectedPosition), passedItem.toJSONMessage().toString());
                                else
                                    socket.emit("EVT_ReceptionArtefactIntoZE", pseudo, String.valueOf(selectedPosition), "test" + String.valueOf(selectedPosition), passedItem.toJSONImage().toString());
                            }

                           if (destList==listArtifact)
                           {if (passedItem.getType().equals("message"))
                               socket.emit("EVT_EnvoieArtefactdeZEPversEP",passedItem.getIdAr().toString(),"test" + String.valueOf(selectedPosition));
                           else
                                socket.emit("EVT_EnvoieArtefactdeZEPversEP",passedItem.getIdAr().toString(),"test" + String.valueOf(selectedPosition));
                         }


                        }

                        //Réponse envoie artefact vers ZE
                        socket.on("EVT_NewArtefactInZE", new Emitter.Listener() {

                            @Override
                            public void call(Object... args) {

                                final int id = (int) args[2];

                                EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listArtifactZEP.get(listArtifactZEP.size() - 1).setIdAr(String.valueOf(id));
                                    }
                                });
                            }
                        });


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
                        v.setBackgroundColor(Color.parseColor("#ef9a9a"));
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundColor(Color.parseColor("#e9e8dd"));
                        break;
                    case DragEvent.ACTION_DROP:

                        PassObject passObj = (PassObject) event.getLocalState();
                        int position = passObj.position;
                        View view = passObj.view;
                        final Artifact passedItem = passObj.artifact;
                        List<Artifact> srcList = passObj.srcList;

                        AbsListView oldParent = (AbsListView) view.getParent();
                        ArtifactAdapter srcAdapter = (ArtifactAdapter) oldParent.getAdapter();
                        if (v == trashLayout) {
                            srcList.remove(position);
                        } else if (v == editLayout && passedItem.getType() == "message") {
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

                                    } else if ((message.getText().toString().equals(""))) {
                                        Clink.show(EspacePersonnelActivity.this, "veuillez inserer un message");

                                    } else {
                                        if (!(passedItem.getCreator().isEmpty())) {
                                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
                                            String date = df.format(Calendar.getInstance().getTime());
                                            Modificateurs mod = new Modificateurs(pseudo, date);
                                            if (passedItem.getModificateurs().isEmpty()) {
                                                List<Modificateurs> listModificateurs = new ArrayList<>();
                                            }
                                            List<Modificateurs> listModificateurs = passedItem.getModificateurs();
                                            listModificateurs.add(mod);
                                            passedItem.setModificateurs(listModificateurs);
                                            passedItem.setTitle(titre.getText().toString());
                                            passedItem.setContenu(message.getText().toString());
                                            passedItem.setDateDerniereModification(date);
                                            artifactAdapter.notifyDataSetChanged();
                                            message.setText("");
                                            titre.setText("");
                                            artifactLayout.setVisibility(View.INVISIBLE);
                                            modifiedButton.setVisibility(View.GONE);
                                            button.setVisibility(View.VISIBLE);
                                        }


                                    }

                                }
                            });
                        } else if (v == zPLayout) {
                            passedItem.setTypeConteneur("ZP");
                            srcList.remove(position);
                            if (passedItem.getType() == "message")
                                socket.emit("EVT_ReceptionArtefactIntoZP", pseudo, String.valueOf(selectedPosition), "test" + String.valueOf(selectedPosition), passedItem.toJSONMessage().toString());
                            else
                                socket.emit("EVT_ReceptionArtefactIntoZP", pseudo, String.valueOf(selectedPosition), "test" + String.valueOf(selectedPosition), passedItem.toJSONImage().toString());

                        }
                        //Réponse envoie artefact vers ZE
                        socket.on("EVT_NewArtefactInZP", new Emitter.Listener() {


                            @Override
                            public void call(Object... args) {

                                final int id = (int) args[2];


                                EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listArtifactZEP.get(listArtifactZEP.size() - 1).setIdAr(String.valueOf(id));
                                    }
                                });
                            }
                        });

                        srcAdapter.notifyDataSetChanged();
                        trashEditLayout.setVisibility(View.GONE);
                        zPLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);


                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundColor(Color.parseColor("#e9e8dd"));

                    default:
                        break;
                }

                return true;
            }

        };

        //drag artifact on long clic
        OnItemLongClickListener myOnItemLongClickListener = new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Artifact selectedItem = (Artifact) (parent.getItemAtPosition(position));
                ArtifactAdapter associatedAdapter = (ArtifactAdapter) (parent.getAdapter());
                List<Artifact> associatedList = associatedAdapter.getList();

                PassObject passObj = new PassObject(view, selectedItem, associatedList, position);

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, passObj, 0);
                view.setVisibility(View.INVISIBLE);
                zPLayout.setVisibility(View.VISIBLE);
                trashEditLayout.setVisibility(View.VISIBLE);
                optionsArtifactLayout.setVisibility(View.GONE);
                return true;
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


        listArtifactLayout.setAbsListView(listArtifactView);
        artifactZEPLayout.setAbsListView(listArtifactZEPView);

        listArtifactZEPView.setAdapter(artifactZEPAdapter);
        listArtifactView.setAdapter(artifactAdapter);

        listArtifactZEPView.setOnItemLongClickListener(myOnItemLongClickListener);
        listArtifactView.setOnItemLongClickListener(myOnItemLongClickListener);


        //Affichage de l'artifact
        listArtifactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
                    startActivity(intent);
                }
            }
        });


        //gestion de l'affichage du layout d'ajout aartifact
        final ImageButton buttonLoadImage = (ImageButton) findViewById(R.id.buttonLoadPicture);
        final ImageButton buttonTakeImage = (ImageButton) this.findViewById(R.id.buttonTakePicture);
        final ImageButton addArtifactBtn = (ImageButton) this.findViewById(R.id.addArtifact);
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

                } else if ((message.getText().toString().equals(""))) {
                    Clink.show(EspacePersonnelActivity.this, "veuillez saisir un message");

                } else {
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

                    message.setText("");
                    titre.setText("");
                    artifactLayout.setVisibility(View.INVISIBLE);


                }

            }
        });

        ImageButton exitButton = (ImageButton) this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });


        //envoie artefact de ZE vers ZP
        socket.on("EVT_EnvoieArtefactdeZEversZP", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                //final JSONObject object = (JSONObject) args[0];
                final String id = (String) args[0];
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

                    }
                });
            }
        });


        //envoie artefact de ZP vers ZE
        socket.on("EVT_EnvoieArtefactdeZPversZE", new Emitter.Listener() {
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
                        Log.i("titre",artifact.getTitle().toString());
                       // Log.i("modificateurs",artifact.getModificateurs().toString());
                        Log.i("contenu",artifact.getContenu().toString());
                        listArtifactZEP.add(artifact);
                        artifactZEPAdapter.notifyDataSetChanged();

                    }
                });
            }
        });


        //réception de l'image
//        socket.on("EVT_Envoie_ArtefactdeZEversZP", new Emitter.Listener() {
//
//            public void call(final Object... args) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String data = (String) args[0];
//                        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
//                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                        //ImageView image = (ImageView) findViewById(R.id.imageReceived);
//                        //image.setImageBitmap(decodedByte);
//
//                    }
//                });
//            }
//
//        });

        //Réception message
        socket.on("EVT_ReponseOKConnexionZEP", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {

                String data = (String) args[0];
                Log.i("message :", data);
            }


        });


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
        final RelativeLayout zepLayout = (RelativeLayout) findViewById(R.id.zep_layout);

        try {
            socket = IO.socket(getUriSocket().toString());
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("Socket", "connection");
                    socket.emit("EVT_DemandeConnexionZEP", EspacePersonnelActivity.this.getPseudo(), String.valueOf(selectedPosition));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            zepLayout.setBackgroundResource(R.drawable.rounded_corner_green);


                        }
                    });
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

        final RelativeLayout zepLayout = (RelativeLayout) findViewById(R.id.zep_layout);
        ImageButton login_logout_btn = (ImageButton) this.findViewById(R.id.login_logout_btn);
        if (socket.connected() && connected == true) {

            zepLayout.setBackgroundResource(R.drawable.rounded_corner_green);
            login_logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    socket.disconnect();
                    connected = false;
                    setInterface();
                }


            });

        } else {
            zepLayout.setBackgroundResource(R.drawable.rounded_corner_red);
            login_logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    connected = true;
                    initialize();
                    setInterface();
                }

            });
        }
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
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);

            artifactAdapter.notifyDataSetChanged();


        }

        //prendre un photo
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && null != data) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
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
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy 'à 'HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);
            artifactAdapter.notifyDataSetChanged();

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

    //retourner si l'imageview contient une image ou non
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }


}