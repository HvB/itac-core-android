package fr.learning_adventure.android.itac.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.learning_adventure.android.itac.R;
import fr.learning_adventure.android.itac.model.Artifact;
import fr.learning_adventure.android.itac.model.RoundedImage;

public class ArtifactAdapter extends BaseAdapter {
    private List<Artifact> artifacts;
    private Context context;
    private TextView mTitle;
    private ImageView mImage;
    private TextView mDate;
    RoundedImage roundedImage;
    private int mPosition;
    private ViewGroup mParent;


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
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        mPosition=position;
        mParent = parent;

        // LayoutInflator to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Artifact artifact = artifacts.get(position);


        if (artifact.getType().equals("message")) {
            convertView = inflater.inflate(R.layout.artifact_article_adapter, null);
            mTitle = (TextView) convertView.findViewById(R.id.titre);
            mTitle.setText(artifact.getTitle());
//            mDate = (TextView) convertView.findViewById(R.id.date);
//            mDate.setText(artifact.getDateCreation());


        } else {
            convertView = inflater.inflate(R.layout.artifact_image_adapter, null);
            mImage = (ImageView) convertView.findViewById(R.id.image);
            mImage.setImageBitmap(artifact.getThumbnail());
            /*
            if (artifact.getCreated().equals("true")) {
                mImage = (ImageView) convertView.findViewById(R.id.image);
                Bitmap bmOrigine = BitmapFactory.decodeFile(artifact.getContenu());

                //2048 est la resolution maximale au dela de laquelle on ne peut pas afficher une image sur la tablet Asus, cette valeur differe d'une valeur à une autre

                if (bmOrigine.getHeight() > 2048 && bmOrigine.getWidth() > 2048){
                    Bitmap bm = BitmapFactory.decodeFile(artifact.getContenu(),options);
                    mImage.setImageBitmap(bm);

                }else {

                    mImage.setImageBitmap(bmOrigine);
                }


                //mImage.setImageBitmap(BitmapFactory.decodeFile(artifact.getContenu()));
            } else

            {
                mImage = (ImageView) convertView.findViewById(R.id.image);
                byte[] decodedString = Base64.decode(artifact.getContenu(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte.getHeight() > 2048 && decodedByte.getWidth() > 2048){
                    Bitmap bm =BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length,options);
                    mImage.setImageBitmap(bm);

                }else {

                    mImage.setImageBitmap(decodedByte);
                }
                mImage.setImageBitmap(decodedByte);
            }
            */


        }
        //convertView.setOnTouchListener(new OnTouchListener());


        return convertView;


    }



}
