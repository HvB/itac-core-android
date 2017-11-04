package fr.learning_adventure.android.itac.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.android_app_activity.EspacePersonnelActivity;
import fr.learning_adventure.android.itac.model.Artifact;
import fr.learning_adventure.android.itac.model.PassObject;
import fr.learning_adventure.android.itac.model.RoundedImage;

public class ArtifactAdapter extends BaseAdapter {
    private List<Artifact> artifacts;
    private Context context;
    private EspacePersonnelActivity mainActivity;
    private TextView mTitle;
    private ImageView mImage;
    private TextView mDate;
    private RoundedImage roundedImage;
    private int mPosition;
    private ViewGroup mParent;

    public ArtifactAdapter(EspacePersonnelActivity context, List<Artifact> artifacts) {
        this.context = context;
        this.mainActivity = context;
        this.artifacts = artifacts;
    }

    @Override
    public int getCount() {
        // Number of times getView method call depends upon gridValues.length
        return artifacts.size();
    }

    @Override
    public Object getItem(int position) {
        return artifacts.get(position);
    }

    public List<Artifact> getList() {
        return artifacts;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        mPosition=position;
        mParent = parent;

        // LayoutInflator to call external grid_item.xml file
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Artifact artifact = artifacts.get(position);

        if (artifact.getType().equals("message")) {
            convertView = inflater.inflate(R.layout.artifact_article_adapter, null);
            mTitle = (TextView) convertView.findViewById(R.id.titre);
            mTitle.setText(artifact.getTitle());
        } else {
            convertView = inflater.inflate(R.layout.artifact_image_adapter, null);
            mImage = (ImageView) convertView.findViewById(R.id.image);
            Bitmap img = artifact.getThumbnail();
            if ( img != null) {
                mImage.setImageBitmap(img);
            } else {
                Log.e("ArtifactAdapter_getView", "oups thumbnail est null !!!");
            }
        }
        final PassObject passObj = new PassObject(convertView, artifact, artifacts, position);

        final View view = convertView;
        convertView.setOnTouchListener((new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mainActivity.setPassObject(passObj);
                }
                return true;
            }
        }));
        return convertView;
    }
}
