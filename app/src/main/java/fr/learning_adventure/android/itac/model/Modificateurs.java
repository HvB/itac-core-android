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

import java.io.Serializable;

/**
 * Created by learninglab on 26/04/16.
 */
public class Modificateurs implements Serializable {
    private String modificateur;
    private String dateModification;


    public Modificateurs(String modificateur, String dateModification) {
        this.modificateur = modificateur;
        this.dateModification = dateModification;
    }
    
    public String getModificateur() {
        return modificateur;
    }

    public void setModificateur(String modificateur) {
        this.modificateur = modificateur;
    }

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }
}
