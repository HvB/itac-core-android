/**
 *     Copyright © 2016 Yassine Siela
 *     Copyright © 2016 AIP Primeca RAO
 *     Copyright © 2016-2017 Université Savoie Mont Blanc
 *
 *     ITAC-Core-Android is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        } else {
            convertView = inflater.inflate(R.layout.artifact_image_adapter, null);
            mImage = (ImageView) convertView.findViewById(R.id.image);
            mImage.setImageBitmap(artifact.getThumbnail());
        }
        return convertView;


    }



}
