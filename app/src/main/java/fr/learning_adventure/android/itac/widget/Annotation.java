package fr.learning_adventure.android.itac.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by learninglab on 24/05/16.
 */
public class Annotation {

        public Annotation(Context context) {
            super();
        }

        // Dessinons sur la totalité de l'écran
        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(0, 0, 0);

            // Instance de Paint pour définir l'attribut couleur de notre point, ainsi que
            // sa taille.
            Paint paint = new Paint();

            // Nous allons dessiner nos points par rapport à la résolution de l'écran
            int iWidth = canvas.getWidth(); // Largeur
            int iHeight = canvas.getHeight(); // Hauteur

            Random rand = new Random();
            //Affichons 10000 points de toutes les couleurs
            for (int i=0; i < 10000; i++)
            {
                // Affecter une couleur de manière aléatoire
                paint.setARGB(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                // Puis dessiner nos points dans le canevas
                canvas.drawPoint(rand.nextInt(iWidth), rand.nextInt(iHeight), paint);
            }
        }
    }


