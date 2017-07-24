package fr.learning_adventure.android.itac.android_app_activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
//import android.support.v13.view.ViewCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.adapter.ArtifactAdapter;
import fr.learning_adventure.android.itac.adapter.AvatarAdapter;
import fr.learning_adventure.android.itac.model.Artifact;
import fr.learning_adventure.android.itac.model.ItacConstant;
import fr.learning_adventure.android.itac.model.PassObject;
import fr.learning_adventure.android.itac.widget.Clink;
import fr.learning_adventure.android.itac.widget.LinearLayoutAbsListView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by learninglab on 03/03/16.
 */
public class EspacePersonnelActivity extends ActionBarActivity {
    private UUID deviceUid;
    private String serverLogin = "anonymous";
    private ItacConstant constantes;
    private  Socket socket;
    private final static String FILE_URI_SOCKET = "uri_socket.txt";
    private static int RESULT_EDIT_IMAGE =2;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_CAMERA_haute =1;
    private static int REQUEST_CAMERA_moyenne =0;
    private ContentValues values;
    private Uri imageUri;

    private RelativeLayout espacePersonnelLayout;
    private GridView listArtifactView;
    private GridView listArtifactZEPView;
    private LinearLayoutAbsListView listArtifactLayout, artifactZEPLayout;
    private LinearLayout optionsArtifactLayout;
    private List<Artifact> listArtifact = new ArrayList<>();
    private ArtifactAdapter artifactAdapter = new ArtifactAdapter(this, listArtifact);
    private List<Artifact> listArtifactZEP = new ArrayList<>();
    private ArtifactAdapter artifactZEPAdapter = new ArtifactAdapter(this, listArtifactZEP);
    private HashMap<String, Artifact> artifactsWaitingServeurAck = new LinkedHashMap<>();
    private HashMap<String, File> cachedFiles = new LinkedHashMap<>();
    private ProgressBar progressBar ;
    private LinearLayout zPLayout;
    private RelativeLayout zepLayout;
    private LinearLayout trashLayout;
    private LinearLayout editLayout;
    private LinearLayout trashEditLayout;
    private ImageButton logout_btn;
    private ImageButton login_btn;
    private String idZEP;
    private String idZE;
    private boolean connected = false;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;

    private EditText titre;
    private EditText message;
    private RelativeLayout  artifactLayout;
    private Button modifiedButton;
    private Button button;

    private int selectedPosition;
    private Artifact editedItem;

    private GestureDetectorCompat activityGestureDetector;
    private GestureDetector.OnGestureListener activityGestureListener;

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

    //lire l'URL de la ssocket depuis les preferences
    private String getUriSocket() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddr = sharedPreferences.getString(getString(R.string.pref_key_server_addr),"127.0.0.1");
        String serverPort = sharedPreferences.getString(getString(R.string.pref_key_server_port),"8080");
        Log.i("getUri", "server address : "+serverAddr);
        Log.i("getUri", "server port : "+serverPort);
        short val = 8080;
        try {
            val = Short.parseShort(serverPort);
        } catch (NumberFormatException e){
            Log.i("getUri","port number is not valid,using 8080");
            val = 8080;
        }
        String urlSocket = "http://"+serverAddr+":"+ val;
        Log.i("getUri", "server websocket url : "+urlSocket);
        return urlSocket;
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
        // recuperation des constantes
        constantes = ItacConstant.getInstance(this.getApplicationContext());
        // recuperation de preferences de l'app
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String uuid = sharedPreferences.getString(getString(R.string.pref_key_device_uuid),"");
        if (uuid == null || uuid.equals("")){
            deviceUid = UUID.randomUUID();
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString(getString(R.string.pref_key_device_uuid), deviceUid.toString());
            editor.commit();
        } else {
            deviceUid = UUID.fromString(uuid);
        }
        preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                closeWebSocket();
                //initializeWebSocket();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener);
        // construction de l'IHM
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_espacepersonnel);
        //récuperer le pseudo
        Intent intent = getIntent();
        this.setPseudo(intent.getStringExtra("pseudoName"));

        // recuperation info avatar
        selectedPosition = intent.getExtras().getInt("avatarPosition");
        AvatarAdapter imageAdapter = new AvatarAdapter(this);

        ImageView imageView = (ImageView) findViewById(R.id.imageAvatar);
        imageView.setImageResource(imageAdapter.mThumbIds[selectedPosition]);
        TextView pseudoView = (TextView) findViewById(R.id.pseudo);
        pseudoView.setText(pseudo);

        espacePersonnelLayout = (RelativeLayout) findViewById(R.id.espacePersonnelLayout);
        trashLayout = (LinearLayout) findViewById(R.id.trashLayout);
        editLayout = (LinearLayout) findViewById(R.id.editLayout);
        zPLayout = (LinearLayout) findViewById(R.id.zp_Layout);
        trashEditLayout = (LinearLayout) findViewById(R.id.trashEditLayout);

        titre = (EditText) EspacePersonnelActivity.this.findViewById(R.id.titre);
        message = (EditText) EspacePersonnelActivity.this.findViewById(R.id.message_input);
        artifactLayout = (RelativeLayout) this.findViewById(R.id.artifact);
        optionsArtifactLayout = (LinearLayout) this.findViewById(R.id.optionsArtifactLayout);
        modifiedButton = (Button) this.findViewById(R.id.send_modified_button);
        button = (Button) this.findViewById(R.id.send_button);
        progressBar = (ProgressBar)this.findViewById(R.id.progress);
        zepLayout = (RelativeLayout) findViewById(R.id.zep_layout);
        logout_btn = (ImageButton) this.findViewById(R.id.logout_btn);
        login_btn = (ImageButton) this.findViewById(R.id.login_btn);
        //mettre arriere plans transparent
        espacePersonnelLayout.getBackground().setAlpha(200);

        //gestion des grid view
        artifactZEPLayout = (LinearLayoutAbsListView) findViewById(R.id.artifactZEPLayout);
        listArtifactZEPView = (GridView) findViewById(R.id.listArtifactZEPView);

        listArtifactLayout = (LinearLayoutAbsListView) findViewById(R.id.listArtifactLayout);
        listArtifactView = (GridView) findViewById(R.id.listArtifactView);

        //initialiser socket
        // ST plus de connexion automatique
        //initializeWebSocket();

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

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final float density = metrics.density;
        final float densityX = metrics.xdpi;
        final float densityY = metrics.ydpi;

        // gestion du drag and drop
        final OnDragListener espacePersoOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                boolean res = false;
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED :
                        Log.v("epOnDragListener", "drag started");
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED :
                        Log.v("epOnDragListener", "drag ended");
                        //onDragEng();
                        res = true;
                        break;
                    case DragEvent.ACTION_DROP :
                        Log.v("epOnDragListener", "drag drop");
                        // on envoie l'artefact en EP
                        sendSelectedArtifactToEP();
                        trashEditLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);
                        res = true;
                        break;
                    default:
                        Log.v("epOnDragListener", "drag other");
                        break;
                }
                return res;
            }
        };

        final OnDragListener zoneEchangeOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                boolean res = false;
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED :
                        Log.v("zeOnDragListener", "drag started");
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED :
                        Log.v("zeOnDragListener", "drag ended");
                        //onDragEng();
                        res = true;
                        break;
                    case DragEvent.ACTION_DROP :
                        Log.v("zeOnDragListener", "drag drop");
                        // on envoie l'artefact en ZE
                        sendSelectedArtifactToZE();
                        trashEditLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);
                        res = true;
                        break;
                    default:
                        Log.v("zeOnDragListener", "drag other");
                        break;
                }
                return res;
            }
        };

        final OnDragListener zonePartageOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                boolean res = false;
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED :
                        Log.v("zpOnDragListener", "drag started");
                        res=true;
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.v("zpOnDragListener", "drag entered");
                        v.setBackgroundColor(Color.parseColor("#70eac8"));
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.v("zpOnDragListener", "drag exited");
                        v.setBackgroundColor(Color.parseColor("#323232"));
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.v("zpOnDragListener", "drag ended");
                        v.setBackgroundColor(Color.parseColor("#323232"));
                        //onDragEng();
                        res = true;
                        break;
                    case DragEvent.ACTION_DROP :
                        Log.v("zpOnDragListener", "drag drop");
                        // on envoie l'artefact en ZP
                        sendSelectedArtifactToZp();
                        trashEditLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);
                        res = true;
                        break;
                    default:
                        Log.v("zpOnDragListener", "drag other");
                        break;
                }
                return res;
            }
        };

        final OnDragListener editOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                boolean res = false;
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED :
                        Log.v("editOnDragListener", "drag started");
                        res=true;
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.v("editOnDragListener", "drag entered");
                        v.setBackgroundColor(Color.parseColor("#70eac8"));
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.v("editOnDragListener", "drag exited");
                        v.setBackgroundColor(Color.parseColor("#323232"));
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.v("editOnDragListener", "drag ended");
                        v.setBackgroundColor(Color.parseColor("#323232"));
                        //onDragEng();
                        res = true;
                        break;
                    case DragEvent.ACTION_DROP :
                        Log.v("editOnDragListener", "drag drop");
                        // on edite l'artefact
                        editSelectedArtifact();
                        trashEditLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);
                        res = true;
                        break;
                    default:
                        Log.v("editOnDragListener", "drag other");
                        break;
                }
                return res;
            }
        };

        final OnDragListener deleteOnDragListener = new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                boolean res = false;
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED :
                        Log.v("trashOnDragListener", "drag started");
                        res=true;
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.v("trashOnDragListener", "drag entered");
                        v.setBackgroundColor(Color.parseColor("#70eac8"));
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.v("trashOnDragListener", "drag exited");
                        v.setBackgroundColor(Color.parseColor("#323232"));
                        res = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.v("trashOnDragListener", "drag ended");
                        v.setBackgroundColor(Color.parseColor("#323232"));
                        onDragEng();
                        res = true;
                        break;
                    case DragEvent.ACTION_DROP :
                        Log.v("trashOnDragListener", "drag drop");
                        // on detruit l'artefact
                        deleteSelectedArtifact();
                        trashEditLayout.setVisibility(View.GONE);
                        optionsArtifactLayout.setVisibility(View.VISIBLE);
                        res = true;
                        break;
                    default:
                        Log.v("trashOnDragListener", "drag other");
                        break;
                }
                return res;
            }
        };

        // Action Drop Artifact
        final OnDragListener defaultOnDragListener = new OnDragListener() {
            private float x0, y0;
            private double max;
            private long t0 = 0L;
            @Override
            public boolean onDrag(View v, DragEvent event) {
                PassObject passObj = (PassObject) event.getLocalState();
                switch (event.getAction()) {
                    // never called ???
                    case DragEvent.ACTION_DRAG_STARTED :
                        Log.v("defaultOnDragListener", "drag started");
                        x0 = 0.0f;
                        y0 = 0.0f;
                        t0 = 0;
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION :
                        //Log.v("defaultOnDragListener", "x : " + event.getX());
                        //Log.v("defaultOnDragListener", "y : " + event.getY());
                        long t = SystemClock.uptimeMillis();
                        float x = event.getX();
                        float y = event.getY();
                        if ((t-t0 < 500)  && (t-t0)> 5) {
                            float dx = (x - x0)/density;
                            float dy = (y - y0)/density;
                            long dt = t - t0;
                            double velocity = Math.sqrt(dx*dx + dy*dy) / dt;
                            //double velocity = Math.sqrt(dx*dx + dy*dy) / dt;
                            if (max < velocity) max = velocity;
//                            Log.v("defaultOnDragListener", "dt : " + dt);
//                            Log.v("defaultOnDragListener", "dx : " + dx);
//                            Log.v("defaultOnDragListener", "dy : " + dy);
//                            Log.v("defaultOnDragListener", "vitesse : " + velocity);
//                            Log.v("defaultOnDragListener", "max : " + max);
                            if (velocity > 7) {
                                boolean transfered = sendSelectedArtifactToZp();
                                if (transfered) {
                                    setPassObject(null);
                                    trashEditLayout.setVisibility(View.GONE);
                                    optionsArtifactLayout.setVisibility(View.VISIBLE);
                                    if (shadowBuilder != null && shadowBuilder.getView() != null){
                                        shadowBuilder.getView().setVisibility(View.GONE);
                                    }
//                                ViewCompat.cancelDragAndDrop(v);
//                                View nv = new View(EspacePersonnelActivity.this);
//                                ViewCompat.updateDragShadow(v, new View.DragShadowBuilder(nv));
                                }
                            }
                        }
                        x0  = x;
                        y0  = y;
                        t0 = t;
                        Log.v("defaultOnDragListener", "drag location");
                        break;
                    case DragEvent.ACTION_DROP :
                        Log.v("defaultOnDragListener", "drag drop");
                        x0 = 0.0f;
                        y0 = 0.0f;
                        t0 = 0;
                        break;
                    // never called ???
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.v("defaultOnDragListener", "drag ended");
                        x0 = 0.0f;
                        y0 = 0.0f;
                        t0 = 0;
                        onDragEng();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.v("defaultOnDragListener", "drag entered");
                        x0 = 0.0f;
                        y0 = 0.0f;
                        t0 = 0;
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.v("defaultOnDragListener", "drag exited");
                        break;
                    default:
                        Log.v("defaultOnDragListener", "drag other");
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

//        listArtifactLayout.setOnDragListener(myOnDragListener);
//        artifactZEPLayout.setOnDragListener(myOnDragListener);
//        trashLayout.setOnDragListener(myArtefactOnDragListener);
//        editLayout.setOnDragListener(myArtefactOnDragListener);
//        zPLayout.setOnDragListener(myArtefactOnDragListener);
//        espacePersonnelLayout.setOnDragListener(myArtefactOnDragListener);
        listArtifactLayout.setOnDragListener(espacePersoOnDragListener);
        artifactZEPLayout.setOnDragListener(zoneEchangeOnDragListener);
        trashLayout.setOnDragListener(deleteOnDragListener);
        editLayout.setOnDragListener(editOnDragListener);
        zPLayout.setOnDragListener(zonePartageOnDragListener);
        espacePersonnelLayout.setOnDragListener(defaultOnDragListener);


        listArtifactLayout.setAbsListView(listArtifactView);
        artifactZEPLayout.setAbsListView(listArtifactZEPView);

        listArtifactZEPView.setAdapter(artifactZEPAdapter);
        listArtifactView.setAdapter(artifactAdapter);

        final View avatarLayout = this.findViewById(R.id.avatarlayout);
        artifactZEPAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged(){
                if ((listArtifactZEPView.getNumColumns() < 0) || (artifactZEPAdapter.getCount() < listArtifactZEPView.getNumColumns())){
                    avatarLayout.setVisibility(View.VISIBLE);
                } else if (artifactZEPAdapter.getCount() > listArtifactZEPView.getNumColumns()){
                    avatarLayout.setVisibility(View.GONE);
                }
            }
        });

        //listArtifactZEPView.setOnItemClickListener(myOnItemClickListener);
        //listArtifactView.setOnItemClickListener(myOnItemClickListener);

        //Affichage de l'artifact
//        listArtifactView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
//                Artifact artifact = (Artifact) parent.getItemAtPosition(position);
//                if (artifact.getType().equals("message")) {
//                    Intent intent = new Intent(EspacePersonnelActivity.this, ArtifactArticleActivity.class);
//                    intent.putExtra("title", artifact.getTitle());
//                    intent.putExtra("message", artifact.getContenu());
//                    intent.putExtra("pseudo", artifact.getCreator());
//                    intent.putExtra("date", artifact.getDateCreation());
//                    intent.putExtra("avatarPosition", selectedPosition);
//                    intent.putExtra("modificateurs", artifact.getModificateurs().toString());
//                    Log.d("EspacePersonnelActivity", "avant affichage de l'artefact, voici la liste des modificateurs: "+artifact.getModificateurs());
//                    //Start details activity
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(EspacePersonnelActivity.this, ArtifactImageActivity.class);
//                    intent.putExtra("pseudo", artifact.getCreator());
//                    intent.putExtra("image", artifact.getContenu());
//                    intent.putExtra("date", artifact.getDateCreation());
//                    intent.putExtra("created", artifact.getCreated());
//                    intent.putExtra("modificateurs", artifact.getModificateurs().toString());
//                    startActivity(intent);
//                }
//                return true;
//            }
//        });

        // gestion des gestes utilisateur
        activityGestureListener = new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                Log.v("activityGestureListener","onLongPress");
                displaySelectedArtifact();
                //super.onLongPress(e);
            }
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Log.v("activityGestureListener","onFling");
//                return super.onFling(e1, e2, velocityX, velocityY);
//            }
//            @Override
//            public boolean onDown(MotionEvent e) {
//                Log.v("activityGestureListener","onDown");
//                return super.onDown(e);
//            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.v("activityGestureListener","onScroll");
                startDragSeletedArtefact();
                return true;
                //return super.onScroll(e1,e2, distanceX,distanceY);
            }
        };
        activityGestureDetector = new GestureDetectorCompat(this, activityGestureListener);

        //gestion de l'affichage du layout d'ajout aartifact
        final Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        final Button buttonTakeImage = (Button) this.findViewById(R.id.buttonTakePicture);
        final Button addArtifactBtn = (Button) this.findViewById(R.id.addArtifact);
        addArtifactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artifactLayout.setVisibility(View.VISIBLE);
                optionsArtifactLayout.setVisibility(View.GONE);
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
                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String date = fmt.format(Calendar.getInstance().getTime());
                    artefact.setDateCreation(date);
                    artefact.setType(Artifact.ARTIFACT_TYPE_MESSAGE);
                    Log.d("EspacePersonnelActivity", "initialisation de la liste de modificateurs (vide)");
                    artefact.setModificateurs(new JSONArray());
                    listArtifact.add(artefact);
                    artifactAdapter.notifyDataSetChanged();
                    message.setText("");
                    titre.setText("");
                    artifactLayout.setVisibility(View.GONE);
                    optionsArtifactLayout.setVisibility(View.VISIBLE);
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
                artifactLayout.setVisibility(View.GONE);
                optionsArtifactLayout.setVisibility(View.VISIBLE);
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

        // on verifie si on a une camera ou non
        PackageManager pm = getPackageManager();
        // si ce n'est pas le cas on desactive le bouton pour prendre des photos
        if(! pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            buttonTakeImage.setEnabled(false);
        }
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

        final GestureDetector.OnGestureListener avatarListener = new GestureDetector.SimpleOnGestureListener(){
            private int visibility;
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.v("avatarListener", "on touch scroll event..."+Math.abs((e2.getX()-e1.getX())/density));
                if (Math.abs((e2.getX()-e1.getX())/density)>5) {
                    zPLayout.setVisibility(visibility);
                }
                return true;
                //return super.onScroll(e1,e2, distanceX,distanceY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.v("avatarListener", "on touch down event...");
                if (zPLayout.getVisibility() == View.VISIBLE){
                    visibility = View.GONE;
                } else {
                    visibility = View.VISIBLE;
                }
                //return super.onDown(e);
                return true;
            }
        };
        final GestureDetectorCompat avatarScrollDetector = new GestureDetectorCompat(this, avatarListener);
        final View.OnTouchListener avatarOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("avatarTouchListener", "on touch event...");
                avatarScrollDetector.onTouchEvent(event);
                return true;
            }
        };
        final View avatarView = this.findViewById(R.id.imageAvatar);
        avatarView.setOnTouchListener(avatarOnTouchListener);
    }

    @Override
    protected void onDestroy() {
        closeWebSocket();
        // suppression des fichiers image temporaires
        for (File temp : cachedFiles.values()){
            temp.delete();
            // on met la base des medias a jour
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(temp)));
        }
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
                if (listArtifactZEP.size() > 0) {
                    Clink.show(EspacePersonnelActivity.this, "votre zone d'échange contient des élèments, veuiller la vider pour se déconnecter");
                } else {
                    // fermeture de la connection actuelle
                    // closeWebSocket();
                    // saisie nouvelle URL
                    Intent i = new Intent(EspacePersonnelActivity.this, ConnexionActivity.class);
                    i.putExtra("uri", getUriSocket());
                    EspacePersonnelActivity.this.startActivity(i);
                }
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
                if (listArtifactZEP.size() > 0) {
                    Clink.show(EspacePersonnelActivity.this, "votre zone d'échange contient des élèments, veuiller la vider pour se déconnecter");
                } else {
                    finish();
                }
                return true;
        }
        return false;
    }

    private void onDisconnection(){
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
        try {
            logout_btn.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
            // display progressbar
            progressBar.setVisibility(View.VISIBLE);
            Log.i("WebSocket URL", getUriSocket());
            // creation de la socket
            // recuperation de preferences de l'app
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            serverLogin = sharedPreferences.getString(getString(R.string.pref_key_server_login),"anonymous");
            final String serverPassword = sharedPreferences.getString(getString(R.string.pref_key_server_password),"");
            final String uuid = sharedPreferences.getString(getString(R.string.pref_key_device_uuid),"");
            if (uuid == null || uuid.equals("")){
                deviceUid = UUID.randomUUID();
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString(getString(R.string.pref_key_device_uuid), deviceUid.toString());
                editor.commit();
            } else {
                deviceUid = UUID.fromString(uuid);
            }
            Log.i("initializeWebSocket", "connection login : "+serverLogin+", device UUID: "+deviceUid.toString());

            if (socket != null){
                Log.i("initializeWebSocket", "reutilisation de la socket existante");
            } else {
                Log.i("initializeWebSocket", "creation de la socket");
                URI uri = URI.create(getUriSocket());
                socket = IO.socket(uri);

                // gestion des evenements lies au foctionnement de la websocket
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "connected");
                        Log.i("initializeWebSocket", "EVT_DemandeConnexionZEP : " + pseudo + ", " + String.valueOf(selectedPosition));
                        if (socket == null) {
                            Log.i("initializeWebSocket", "socket is null, can't send EVT_DemandeConnexionZEP : " + pseudo + ", " + String.valueOf(selectedPosition));
                        } else {
                            socket.emit("EVT_DemandeConnexionZEP", pseudo, String.valueOf(selectedPosition), serverLogin, serverPassword, deviceUid.toString());
                        }
                    }
                });
                socket.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "reconnecting");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logout_btn.setVisibility(View.VISIBLE);
                                login_btn.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
                socket.on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "reconnect attempt");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logout_btn.setVisibility(View.VISIBLE);
                                login_btn.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
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
                socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("Socket", "connexion error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Clink.show(EspacePersonnelActivity.this, "veuillez verifier les parametres de connexion");
                                progressBar.setVisibility(View.GONE);
                                //login_btn.setVisibility(View.VISIBLE);
                                //logout_btn.setVisibility(View.GONE);
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
                                progressBar.setVisibility(View.GONE);
                                //login_btn.setVisibility(View.VISIBLE);
                                //logout_btn.setVisibility(View.GONE);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                login_btn.setVisibility(View.VISIBLE);
                                logout_btn.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                // gestion des evenements lies au fonctionnement du serveur ITAC
                //envoie artefact de ZE vers ZP
                socket.on(constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_ZP, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        //final JSONObject object = (JSONObject) args[0];
                        // ToDo corriger ligne suivant
                        final String id = ("" + args[0]);
                        Log.i("evt", constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_ZP+" : " + id);
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
                socket.on(constantes.EVT_ENVOIE_ARTEFACT_DE_ZP_VERS_ZE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String data = (String) args[0];
                        JSONObject object = null;
                        try {
                            object = new JSONObject(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Artifact artifact = new Artifact(object);
                        // on devient le nouveau proprietaire de l'objet
                        artifact.setProprietaire(pseudo);
                        artifact.setCreated("false");
                        listArtifactZEP.add(artifact);
                        Log.i("evt", constantes.EVT_ENVOIE_ARTEFACT_DE_ZP_VERS_ZE+" : " + data);
                        EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                artifactZEPAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

                //Réponse envoie artefact vers ZE
                socket.on(constantes.EVT_RECEPTION_ARTEFACT_INTO_ZE, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        final String uuid = (String) args[2];
                        Log.i("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_ZE+" : " + uuid);
                        Artifact artifact = artifactsWaitingServeurAck.get(uuid);
                        if (artifact == null) {
                            Log.e("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_ZE+", artefact inconnu : " + uuid);
                        } else {
                            Log.e("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_ZE+", transfert artefact en ZEP : " + uuid);
                            //artifact.setCreated("false");
                            listArtifactZEP.add(artifact);
                            artifactsWaitingServeurAck.remove(uuid);
                            EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (artifactsWaitingServeurAck.isEmpty()) {
                                        artifactZEPAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

                //Réponse envoie artefact vers ZP
                socket.on(constantes.EVT_RECEPTION_ARTEFACT_INTO_ZP, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        final String uuid = (String) args[2];
                        Log.i("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_ZP+" : " + uuid);
                        Artifact artifact = artifactsWaitingServeurAck.get(uuid);
                        if (artifact == null) {
                            Log.e("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_ZP+", artefact inconnu : " + uuid);
                        } else {
                            Log.e("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_ZP+", acquitement transfert artefact en ZP : " + uuid);
                            artifactsWaitingServeurAck.remove(uuid);
                            EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (artifactsWaitingServeurAck.isEmpty()) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

                //Réponse envoie artefact vers EP
                socket.on(constantes.EVT_RECEPTION_ARTEFACT_INTO_EP, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        final String uuid = (String) args[1];
                        Log.i("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_EP+" : " + uuid);
                        Artifact artifact = artifactsWaitingServeurAck.get(uuid);
                        if (artifact == null) {
                            Log.e("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_EP+", artefact inconnu : " + uuid);
                        } else {
                            Log.e("evt", constantes.EVT_RECEPTION_ARTEFACT_INTO_EP+", acquitement transfert artefact en EP : " + uuid);
                            listArtifact.add(artifact);
                            artifactsWaitingServeurAck.remove(uuid);
                            EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    artifactAdapter.notifyDataSetChanged();
                                    if (artifactsWaitingServeurAck.isEmpty()) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

                //Reception message acquitant la connexion
                socket.on(constantes.EVT_REPONSE_OK_CONNEXION_ZEP, new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        Object ZEP = args[1];
                        idZE = (String) args[0];
                        idZEP = String.valueOf(ZEP);
                        connected = true;
                        Log.i("evt", constantes.EVT_REPONSE_OK_CONNEXION_ZEP+" : " + idZE + ", "+idZEP);
                        // on met a jour l'interface
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                zepLayout.setBackgroundResource(R.drawable.rounded_corner_green);
                                logout_btn.setVisibility(View.VISIBLE);
                                login_btn.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                //Réponse message acquitant un refus de connexion
                socket.on(constantes.EVT_REPONSE_NOK_CONNEXION_ZEP, new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String reason;
                        if (args.length > 0) reason = (String) args[0];
                        else reason = "Connection impossible, cause indeterminée";
                        Log.i("evt", constantes.EVT_REPONSE_NOK_CONNEXION_ZEP+" : ");
                        connected = false;
                        EspacePersonnelActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Clink.show(EspacePersonnelActivity.this, reason);
                                progressBar.setVisibility(View.GONE);
                                login_btn.setVisibility(View.VISIBLE);
                                logout_btn.setVisibility(View.GONE);
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
                Log.i("initializeWebSocket", "fermeture de la socket...");
                socket.close();
                Log.i("initializeWebSocket", "reouverture de la socket...");
                socket.connect();
            }

            //} catch (URISyntaxException e) {
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("error", e.toString());
        }
    }

    //Fin de la connexion au srveur ITAC et fermeture de la WebSocket
    private void closeWebSocket() {
        progressBar.setVisibility(View.GONE);
        login_btn.setVisibility(View.VISIBLE);
        logout_btn.setVisibility(View.GONE);
        onDisconnection();
        if (socket != null) {
            if (socket.connected()) {
                Log.i("closeWebSocket", "deconnection du serveur...");
                socket.emit(constantes.EVT_DECONNEXION, pseudo, idZE);
                Log.i("closeWebSocket", constantes.EVT_DECONNEXION + " : " + pseudo + ", " + idZE);
            }
            Log.i("closeWebSocket", "fermeture de la socket");
            socket.disconnect();
            // IMPERATIF : il faut supprimer les listeners attaches a la websocket
            socket.off();
            socket = null;
        }
    }

    //selectionner l'image depuis la galerie ou l'appareil photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //selection de l'image depuis la galerie
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data ) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            File pictureFile = new File(picturePath);
            if (pictureFile.exists() && pictureFile.canRead()) {
                Artifact artifact = new Artifact(getPseudo());
                artifact.setContenu(pictureFile.getAbsolutePath());
                artifact.setType(Artifact.ARTIFACT_TYPE_IMAGE);
                artifact.setCreated("true");
                // on remplace le fichier original par une copy supprimable
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                boolean ok = artifact.copyImage(destination);
                if (ok) {
                    artifact.setContenu(destination.getAbsolutePath());
                    // on marque le fichier temporaire comme devant etre supprime
                    destination.deleteOnExit();
                    cachedFiles.put(artifact.getIdAr(), destination);
                    String date = fmt.format(Calendar.getInstance().getTime());
                    artifact.setDateCreation(date);

                    listArtifact.add(artifact);
                    artifactAdapter.notifyDataSetChanged();
                } else {
                    Clink.show(EspacePersonnelActivity.this, "Fichier image non existant ou inutilisable");
                    // on met la base des medias a jour
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
                }
            } else {
                Clink.show(EspacePersonnelActivity.this, "Fichier image non existant ou inutilisable");
                // on met la base des medias a jour
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
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
            artifact.setType(Artifact.ARTIFACT_TYPE_IMAGE);
            artifact.setContenu(destination.getAbsolutePath());
            artifact.setCreated("true");
            // on marque le fichier temporaire comme devant etre supprime
            destination.deleteOnExit();
            cachedFiles.put(artifact.getIdAr(), destination);
            String date = fmt.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);
            artifactAdapter.notifyDataSetChanged();
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
            artifact.setType(Artifact.ARTIFACT_TYPE_IMAGE);
            artifact.setCreated("true");
            //// on remplace le fichier original par une copy supprimable
            //File destination = new File(Environment.getExternalStorageDirectory(),
            //        System.currentTimeMillis() + ".jpg");
            //boolean ok = artifact.copyImage(destination);
            //if (ok) artifact.setContenu(destination.getAbsolutePath());
            // En fait, on considère que la photo n'a pas besoin d'etre conservee sur le telephone...
            File destination = new File(imageurl);
            // on marque le fichier temporaire comme devant etre supprime
            destination.deleteOnExit();
            cachedFiles.put(artifact.getIdAr(), destination);
            String date = fmt.format(Calendar.getInstance().getTime());
            artifact.setDateCreation(date);
            listArtifact.add(artifact);
            artifactAdapter.notifyDataSetChanged();
        } else if (requestCode == RESULT_EDIT_IMAGE && resultCode == RESULT_OK){
            if (editedItem != null){
                // l'image est modifiee, on efface l'ancienne miniature
                editedItem.setThumbnail(null);
                artifactAdapter.notifyDataSetChanged();
                if (editedItem.getModificateurs() == null){
                    editedItem.setModificateurs(new JSONArray());
                }
                Log.v("EspacePersonnelActivity", "Edition artefact, "+editedItem.getIdAr()+", liste de modificateurs avant modification : "+editedItem.getModificateurs());
                JSONObject modificateur = new JSONObject();
                try {
                    modificateur.putOpt(Artifact.JSON_MODIFICATEUR, pseudo);
                    modificateur.putOpt(Artifact.JSON_DATEMODIFICATION, fmt.format(new Date()));
                    editedItem.getModificateurs().put(modificateur);
                    Log.d("EspacePersonnelActivity", "Edition artefact, "+editedItem.getIdAr()+", ajout modificateurs "+modificateur);
                } catch (JSONException e) {
                    Log.e("EspacePersonnelActivity", "Edition artefact, "+editedItem.getIdAr()+", erreur lors du decodage des modificateurs",e);
                }
                Log.v("EspacePersonnelActivity", "Edition artefact, "+editedItem.getIdAr()+", liste de modificateurs apres modification : "+editedItem.getModificateurs());
            }
        }
        artifactAdapter.notifyDataSetChanged();
    }

    private PassObject passObj= null;

    public void setPassObject(PassObject passObj){
        if (this.passObj != null && this.passObj.view != null) {
            final View v = this.passObj.view;
            v.setVisibility(View.VISIBLE);
        }
        this.passObj = passObj;
    }

    public void startDragSeletedArtefact(){
        startDrag(passObj);
    }

    private View.DragShadowBuilder shadowBuilder;

    public void startDrag(PassObject passObj){
        if (passObj != null && passObj.artifact != null) {
            // if (passObj.srcList == listArtifact) {
            Log.i("startDrag", "start drag artifact");
            final Artifact passedItem = passObj.artifact;
            final View view = passObj.view;
            List<Artifact> srcList = passObj.srcList;
            int position = passObj.position;
            view.setBackgroundResource(0);
            ClipData data = ClipData.newPlainText("", "");
            // View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, passObj, 0);
            view.setVisibility(View.INVISIBLE);
            // affichage outils sur l'artefact
            trashEditLayout.setVisibility(View.VISIBLE);
            optionsArtifactLayout.setVisibility(View.GONE);
            // }
        }
    }

    public boolean sendSelectedArtifactToZp(){
        return sendToZp(passObj);
    }
    public boolean sendToZp(PassObject passObj){
        boolean transfered = false;
        Log.i("sendToZp", constantes.EVT_NEW_ARTEFACT_IN_ZP + " : " + pseudo + ", " + idZEP + ", " + idZE);
        if (passObj != null) {
            final Artifact passedItem = passObj.artifact;
            List<Artifact> srcList = passObj.srcList;
            int position = passObj.position;
            if (passObj.srcList == listArtifact) {
                Log.i("sendToZp", constantes.EVT_NEW_ARTEFACT_IN_ZP + " : " + pseudo + ", " + idZEP + ", " + idZE);
                if (connected && (socket != null) && socket.connected()) {
                    if (passedItem.getType().equals("message")) {
                        socket.emit(constantes.EVT_NEW_ARTEFACT_IN_ZP, pseudo, idZEP, idZE, passedItem.toJSONMessage().toString());
                        Log.v("sendToZp", "art : "+ passedItem.toJSONMessage().toString());
                    } else {
                        socket.emit(constantes.EVT_NEW_ARTEFACT_IN_ZP, pseudo, idZEP, idZE, passedItem.toJSONImage().toString());
                        Log.v("sendToZp", "art : "+ passedItem.toJSONImage().toString());
                    }
                    Log.i("sendToZp", constantes.EVT_NEW_ARTEFACT_IN_ZP + " : mise de l'artefact en zone d'attente : " + passedItem.getIdAr());
                    srcList.remove(position);
                    //on met l'artefact en zone d'attente
                    artifactsWaitingServeurAck.put(passedItem.getIdAr(), passedItem);
                    progressBar.setVisibility(View.VISIBLE);
                    artifactAdapter.notifyDataSetChanged();
                    transfered = true;
                } else if (!connected) {
                    Log.e("sendToZp", "not connected on server, can't send " + constantes.EVT_NEW_ARTEFACT_IN_ZP + " : " + pseudo + ", " + idZEP + ", " + idZE);
                    Clink.show(EspacePersonnelActivity.this, "veuillez vous connecter");
                    //smooth scroll to EP
                    listArtifactLayout.absListView.smoothScrollToPosition(position);
                } else /* if (socket == null || !socket.connected()) */ {
                    Log.i("sendToZp", "socket is null, can't send " + constantes.EVT_NEW_ARTEFACT_IN_ZP + " : " + pseudo + ", " + idZEP + ", " + idZE);
                    //smooth scroll to EP
                    listArtifactLayout.absListView.smoothScrollToPosition(position);
                }
            } else {
                //smooth scroll to ZE
                artifactZEPLayout.absListView.smoothScrollToPosition(position);
            }
        }
//        trashEditLayout.setVisibility(View.GONE);
//        optionsArtifactLayout.setVisibility(View.VISIBLE);
//        setPassObject(null);
        return transfered;
    }

    public boolean sendSelectedArtifactToZE(){
        return sendToZE(passObj);
    }
    public boolean sendToZE(PassObject passObj){
        boolean transfered = false;
        Log.i("sendToZE", constantes.EVT_NEW_ARTEFACT_IN_ZE + " : " + pseudo + ", " + idZEP + ", " + idZE);
        if (passObj != null) {
            final Artifact passedItem = passObj.artifact;
            List<Artifact> srcList = passObj.srcList;
            int position = passObj.position;
            if (passObj.srcList == listArtifact) {
                Log.i("sendToZE", constantes.EVT_NEW_ARTEFACT_IN_ZE + " : " + pseudo + ", " + idZEP + ", " + idZE);
                if (connected && (socket != null)  && socket.connected()) {
                    // pas utile normalement...
                    //passedItem.setProprietaire(pseudo);
                    passedItem.setTypeConteneur("ZE");
                    passedItem.setIdConteneur(idZE);
                    if (passedItem.getType().equals("message")) {
                        socket.emit(constantes.EVT_NEW_ARTEFACT_IN_ZE, pseudo, idZEP, idZE, passedItem.toJSONMessage().toString());
                        Log.v("sendToZE", "art : "+ passedItem.toJSONMessage().toString());
                    } else {
                        socket.emit(constantes.EVT_NEW_ARTEFACT_IN_ZE, pseudo, idZEP, idZE, passedItem.toJSONImage().toString());
                        Log.v("sendToZE", "art : "+ passedItem.toJSONImage().toString());
                    }
                    Log.i("sendToZE", constantes.EVT_NEW_ARTEFACT_IN_ZE + " : mise de l'atefact en zone d'attente : " + passedItem.getIdAr());
                    srcList.remove(position);
                    // listArtifactZEP.add(passedItem);
                    // on met l'artefact en zone d'attente
                    artifactsWaitingServeurAck.put(passedItem.getIdAr(), passedItem);
                    progressBar.setVisibility(View.VISIBLE);
                    artifactAdapter.notifyDataSetChanged();
                    // artifactZEPAdapter.notifyDataSetChanged();
                    // smooth scroll to bottom of ZE
                    artifactZEPLayout.absListView.smoothScrollToPosition(artifactZEPAdapter.getCount() - 1);
                    transfered = true;
                } else if (!connected) {
                    Log.e("sendToZE", "not connected on server, can't send " + constantes.EVT_NEW_ARTEFACT_IN_ZE + " : " + pseudo + ", " + idZEP + ", " + idZE);
                    Clink.show(EspacePersonnelActivity.this, "veuillez vous connecter");
                    //smooth scroll to EP
                    listArtifactLayout.absListView.smoothScrollToPosition(position);
                } else /* if (socket == null || !socket.connected()) */ {
                    Log.e("sendToZE", "socket is null, can't send " + constantes.EVT_NEW_ARTEFACT_IN_ZE + " : " + pseudo + ", " + idZEP + ", " + idZE);
                    //smooth scroll to EP
                    listArtifactLayout.absListView.smoothScrollToPosition(position);
                }
            } else {
                //smooth scroll to ZE
                artifactZEPLayout.absListView.smoothScrollToPosition(position);
            }
//            trashEditLayout.setVisibility(View.GONE);
//            optionsArtifactLayout.setVisibility(View.VISIBLE);
//            setPassObject(null);
        }
        return transfered;
    }

    public boolean sendSelectedArtifactToEP(){
        return sendToEP(passObj);
    }
    public boolean sendToEP(PassObject passObj){
        boolean transfered = false;
        Log.i("sendToEP", constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP + " : " + pseudo + ", " + idZEP + ", " + idZE);
        if (passObj != null) {
            final Artifact passedItem = passObj.artifact;
            List<Artifact> srcList = passObj.srcList;
            int position = passObj.position;
            if (passObj.srcList == listArtifactZEP) {
                Log.i("myArtefactOnDrag", constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP + " : " + pseudo + ", " + idZEP + ", " + idZE);
                if (!connected || socket == null || !socket.connected()) {
                    Log.i("myArtefactOnDrag", "not connected or socket is null, can't send " + constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP + " : " + pseudo + ", " + idZEP + ", " + idZE);
                    Log.i("myArtefactOnDrag", "Transfert direct en Espace prive - pas d'attente : " + passedItem.getIdAr());
                    // on met l'artefact en espace prive directement
                    srcList.remove(position);
                    listArtifact.add(passedItem);
                    artifactZEPAdapter.notifyDataSetChanged();
                    artifactAdapter.notifyDataSetChanged();
                } else {
                    // on est connecte on signalle le modif au serveur et on attend la notification
                    if (passedItem.getType().equals("message")) {
                        socket.emit(constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP, passedItem.getIdAr(), idZE, idZEP);
                    }
                    else {
                        socket.emit(constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP, passedItem.getIdAr(), idZE, idZEP);
                    }
                    Log.i("myArtefactOnDrag", constantes.EVT_ENVOIE_ARTEFACT_DE_ZE_VERS_EP + " : passage direct de l'artefact en espace prive : " + passedItem.getIdAr());
                    // on met l'artefact en zone d'attente
                    srcList.remove(position);
                    // listArtifact.add(passedItem);
                    artifactsWaitingServeurAck.put(passedItem.getIdAr(), passedItem);
                    progressBar.setVisibility(View.VISIBLE);
                    artifactZEPAdapter.notifyDataSetChanged();
                    // artifactAdapter.notifyDataSetChanged();
                }
                // smooth scroll to bottom of EP
                listArtifactLayout.absListView.smoothScrollToPosition(artifactAdapter.getCount() - 1);
                transfered = true;
            } else {
                //smooth scroll to current position in ZE
                listArtifactLayout.absListView.smoothScrollToPosition(position);
            }
            //zPLayout.setVisibility(View.GONE);
//            trashEditLayout.setVisibility(View.GONE);
//            optionsArtifactLayout.setVisibility(View.VISIBLE);
//            setPassObject(null);
        }
        return transfered;
    }

    public void displaySelectedArtifact() {
        if (passObj != null) {
            displayArtifact(passObj.artifact);
        }
    }
    public void displayArtifact(Artifact artifact){
        if (artifact != null) {
            if (artifact.getType().equals("message")) {
                Intent intent = new Intent(EspacePersonnelActivity.this, ArtifactArticleActivity.class);
                intent.putExtra("title", artifact.getTitle());
                intent.putExtra("message", artifact.getContenu());
                intent.putExtra("pseudo", artifact.getCreator());
                intent.putExtra("date", artifact.getDateCreation());
                intent.putExtra("avatarPosition", selectedPosition);
                intent.putExtra("modificateurs", artifact.getModificateurs().toString());
                Log.d("EspacePersonnelActivity", "avant affichage de l'artefact, voici la liste des modificateurs: " + artifact.getModificateurs());
                //Start details activity
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(EspacePersonnelActivity.this, ArtifactImageActivity.class);
                intent.putExtra("pseudo", artifact.getCreator());
                intent.putExtra("image", artifact.getContenu());
                intent.putExtra("date", artifact.getDateCreation());
                intent.putExtra("created", artifact.getCreated());
                intent.putExtra("modificateurs", artifact.getModificateurs().toString());
                startActivity(intent);
            }
            trashEditLayout.setVisibility(View.GONE);
            optionsArtifactLayout.setVisibility(View.VISIBLE);
        }
    }

    public void deleteSelectedArtifact(){
        delete(passObj);
    }
    public void delete(PassObject passObj){
        if (passObj != null){
            final Artifact passedItem = passObj.artifact;
            List<Artifact> srcList = passObj.srcList;
            int position = passObj.position;
            if (passObj.srcList == listArtifact) {
                srcList.remove(position);
                artifactAdapter.notifyDataSetChanged();
            }
            trashEditLayout.setVisibility(View.GONE);
            optionsArtifactLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onDragEng(){
        setPassObject(null);
        // pas une bonne idee car trashEditLayout doit recevoir un event ACTION_DRAG_ENDED
        // trashEditLayout.setVisibility(View.GONE);
        // optionsArtifactLayout.setVisibility(View.VISIBLE);
    }

    public void editSelectedArtifact(){
        edit(passObj);
        setPassObject(null);
    }
    public void edit(PassObject passObj){
        if (passObj != null){
            final Artifact passedItem = passObj.artifact;
            List<Artifact> srcList = passObj.srcList;
            int position = passObj.position;
            if (passObj.srcList != listArtifact) {
                trashEditLayout.setVisibility(View.GONE);
                optionsArtifactLayout.setVisibility(View.VISIBLE);
            } else if (passedItem.getType().equals("image") && srcList == listArtifact) {
                editedItem = null;
                Intent intent = new Intent(Intent.ACTION_EDIT);
                // on cree, si necessaire, le fichier qui correspond a l'image
                File imageFile = passedItem.saveImage();
                Uri uri = null;
                if (imageFile != null) {
                    // si besion, on marque le fichier comme devant etre supprime
                    // (normalement c'est un fichier temporaire, soit on l'a cree soit c'est une copie)
                    imageFile.deleteOnExit();
                    cachedFiles.put(passedItem.getIdAr(), imageFile);
                    uri = Uri.fromFile(new File(passedItem.getContenu()));
                    intent.setDataAndType(uri, "image/jpeg");
                }
                if (uri != null && intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, RESULT_EDIT_IMAGE);
                    editedItem = passedItem;
                } else {
                    Clink.show(EspacePersonnelActivity.this, "Ce type n'est pas modifiable");
                }
                trashEditLayout.setVisibility(View.GONE);
                optionsArtifactLayout.setVisibility(View.VISIBLE);
            } else if (passedItem.getType().equals("message")) {
                titre.setText(passedItem.getTitle());
                message.setText(passedItem.getContenu());
                artifactLayout.setVisibility(View.VISIBLE);
                optionsArtifactLayout.setVisibility(View.GONE);
                trashEditLayout.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                modifiedButton.setVisibility(View.VISIBLE);
                modifiedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (titre.getText().toString().equals("")) {
                            Clink.show(EspacePersonnelActivity.this, "veuillez saisir le titre de l'article");
                        }
                        else if (titre.getText().toString().length() > 25) {
                            Clink.show(EspacePersonnelActivity.this, "le titre de l'article est trés grand");
                        }
                        else if ((message.getText().toString().equals(""))) {
                            Clink.show(EspacePersonnelActivity.this, "veuillez saisir un message");
                        }
                        else {
                            if (!(passedItem.getCreator().isEmpty())) {
                                if (passedItem.getModificateurs() == null) {
                                    passedItem.setModificateurs(new JSONArray());
                                }
                                Log.d("EspacePersonnelActivity", "Edition artefact, " + passedItem.getIdAr() + ", liste de modificateurs avant modification : " + passedItem.getModificateurs());
                                JSONObject modificateur = new JSONObject();
                                DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                try {
                                    modificateur.putOpt(Artifact.JSON_MODIFICATEUR, pseudo);
                                    modificateur.putOpt(Artifact.JSON_DATEMODIFICATION, fmt.format(new Date()));
                                    //modificateur.putOpt(Artifact.JSON_LOGIN, serverLogin);
                                    modificateur.putOpt(Artifact.JSON_DEVICE_UUID, deviceUid.toString());
                                    passedItem.getModificateurs().put(modificateur);
                                    Log.d("EspacePersonnelActivity", "Edition artefact, " + passedItem.getIdAr() + ", ajout modificateurs " + modificateur);
                                } catch (JSONException e) {
                                    Log.e("EspacePersonnelActivity", "Edition artefact, " + passedItem.getIdAr() + ", erreur lors du decodage des modificateurs", e);
                                }
                                Log.d("EspacePersonnelActivity", "Edition artefact, " + passedItem.getIdAr() + ", liste de modificateurs apres modification : " + passedItem.getModificateurs());
                                passedItem.setTitle(titre.getText().toString());
                                passedItem.setContenu(message.getText().toString());
                                artifactAdapter.notifyDataSetChanged();
                                message.setText("");
                                titre.setText("");
                                artifactLayout.setVisibility(View.GONE);
                                modifiedButton.setVisibility(View.GONE);
                                button.setVisibility(View.VISIBLE);
                                trashEditLayout.setVisibility(View.GONE);
                                optionsArtifactLayout.setVisibility(View.VISIBLE);

                                hideSoftKeyboard(EspacePersonnelActivity.this);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        activityGestureDetector.onTouchEvent(ev);
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN :
//                Log.v("activityDsptTouchEvent","down");
//                break;
//            case MotionEvent.ACTION_UP :
//                Log.v("activityDsptTouchEvent","up");
//                break;
//            default :
//                Log.v("activityDsptTouchEvent","other");
//                break;
//        }
        boolean res =  super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN :
//                Log.v("activityDsptTouchEvent","after down");
//                break;
            case MotionEvent.ACTION_UP :
                Log.v("activityDsptTouchEvent","after up");
                // de-selection current artefact
                if (passObj != null) {
                    setPassObject(null);
                }
                break;
//            default :
//                Log.v("activityDsptTouchEvent","after other");
//                break;
        }
        return res;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
