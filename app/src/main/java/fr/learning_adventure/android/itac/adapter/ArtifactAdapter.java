package fr.learning_adventure.android.itac.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
        options.inSampleSize = 8;

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
            if (artifact.getCreated().equals("true")) {
                Bitmap bm = BitmapFactory.decodeFile(artifact.getContenu(),options);
                mImage = (ImageView) convertView.findViewById(R.id.image);
                mImage.setImageBitmap(bm);


                //mImage.setImageBitmap(BitmapFactory.decodeFile(artifact.getContenu()));
            } else

            {
                mImage = (ImageView) convertView.findViewById(R.id.image);
                byte[] decodedString = Base64.decode(artifact.getContenu(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


                mImage.setImageBitmap(decodedByte);
            }


        }
        return convertView;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);


        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
