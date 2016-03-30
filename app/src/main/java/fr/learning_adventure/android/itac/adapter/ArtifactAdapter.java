package fr.learning_adventure.android.itac.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import fr.learning_adventure.android.itac.model.Artifact;

/**
 * Created by learninglab on 30/03/16.
 */
public class ArtifactAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Artifact> artifacts;

    public void prepareList(Artifact artifact) {
        artifacts = new ArrayList<Artifact>();
        artifacts.add(artifact);
    }
    // Constructor
    public ArtifactAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return artifacts.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }
}
