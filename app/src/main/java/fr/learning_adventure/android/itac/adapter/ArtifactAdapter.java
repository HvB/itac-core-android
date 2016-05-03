package fr.learning_adventure.android.itac.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.model.Artifact;

public class ArtifactAdapter extends BaseAdapter {
    private List<Artifact> artifacts;
    private Context context;
    private TextView mTitle;
    private TextView mPseudo;
    private TextView mMessage;
    private ImageView mImage;
    private TextView mDate;
    public int count;


    public ArtifactAdapter(Context context, List<Artifact> artifacts) {
        this.context = context;
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

        // LayoutInflator to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Artifact artifact = artifacts.get(position);


        if (artifact.getType() == "message") {
            convertView = inflater.inflate(R.layout.artifact_article_adapter, null);
            mTitle = (TextView) convertView.findViewById(R.id.titre);
            mTitle.setText(artifact.getTitle());
            mDate = (TextView) convertView.findViewById(R.id.date);
            mDate.setText(artifact.getDateCreation());


        } else {
            convertView = inflater.inflate(R.layout.artifact_image_adapter, null);
            mImage = (ImageView) convertView.findViewById(R.id.image);
            mImage.setImageBitmap(BitmapFactory.decodeFile(artifact.getContenu()));
            mDate = (TextView) convertView.findViewById(R.id.date);
            mDate.setText(artifact.getDateCreation());


        }
        //convertView.setOnDragListener(new MyArtifactDragListener(artifacts.get(position)));
        return convertView;
    }


}
