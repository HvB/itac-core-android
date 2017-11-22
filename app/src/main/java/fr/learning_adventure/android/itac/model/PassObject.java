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
package fr.learning_adventure.android.itac.model;

import android.view.View;

import java.util.List;

/**
 * Created by learninglab on 20/04/16.
 */
public class PassObject {
    public View view;
    public Artifact artifact;
    public List<Artifact> srcList;
    public int position;

    public PassObject(View v, Artifact i, List<Artifact> s, int position) {
        view = v;
        artifact = i;
        srcList = s;
        this.position = position;
    }
}