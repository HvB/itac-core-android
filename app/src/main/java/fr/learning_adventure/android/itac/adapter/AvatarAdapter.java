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

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import fr.learning_adventure.android.itac.R;

/**
 * Created by yassine on 12/04/2016.
 */
public class AvatarAdapter extends BaseAdapter {
    private Context mContext;
    public int selectedPosition;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    // Constructor
    public AvatarAdapter(Context c) {
        mContext = c;
        this.selectedPosition = -1;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public Integer getSelectedAvatar() {
        if (this.selectedPosition < 0) {
            return 0;
        } else {
            return this.mThumbIds[this.selectedPosition];
        }
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            float density = metrics.density;
            int size = (int)(80*density);
            imageView.setLayoutParams(new GridView.LayoutParams(size,size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        if (position == this.getSelectedPosition()){
            imageView.setBackgroundResource(R.drawable.rounded_corner_green);
        } else {
            imageView.setBackgroundResource(0);
        }
        return imageView;
    }


    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.avatar1, R.drawable.avatar2,
            R.drawable.avatar3, R.drawable.avatar4,
            R.drawable.avatar5, R.drawable.avatar6,
            R.drawable.avatar7, R.drawable.avatar8,

    };
}
