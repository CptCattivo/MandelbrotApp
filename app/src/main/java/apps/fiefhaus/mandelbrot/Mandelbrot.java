package apps.fiefhaus.mandelbrot;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fiefhaus on 04.02.2016.
 */
public class Mandelbrot {

    private static final String TAG = "Mandelbrot_class";
    public static final int LOCATION_INTERN = 0;
    public static final int LOCATION_EXTERN = 1;
    public static final int LOCATION_GALlERY = 2;

    private int xPixels, yPixels;

    // Bestimmt die Anzahl der Iterationen
    private int max_iter;

    private Bitmap def_bm_mb,
                    cur_bm_mb;

    // Komplexe Koordinaten, die den Standardbereich der Mandelbrotmenge definieren
    private Complex def_min,
                    def_max;

    // Komplexe Koordinaten, die den aktuell sichtbaren Bereich der Mandelbrotmenge definieren
    private Complex cur_min,
            cur_max;

    public Mandelbrot(Complex def_min, Complex def_max, int xPixels, int yPixels, int max_iter) {

        this.xPixels = xPixels;
        this.yPixels = yPixels;
        this.def_min = def_min;
        this.def_max = def_max;
        this.max_iter = max_iter;

        cur_min = def_min;
        cur_max = def_max;

        def_bm_mb = generateBitmap(def_min, def_max);
        cur_bm_mb = def_bm_mb;
    }

    public Bitmap getBitmap() {

        return cur_bm_mb;
    }

    private Bitmap generateBitmap(Complex min, Complex max) {

        Complex c = new Complex();

        double max_betrag = 4.0;    // Gesetzter Wert, bei dessen erreichen ein Punkt NICHT in der
        // Mandelbrotmenge liegt
        int counter = 0;

        int iter;               // Wert der tatsächlich benötigten Iterationen pro Pixel

        int[] colors = new int[xPixels * yPixels];         // Farben sind in Android int Werte

        for (int y = 0; y < yPixels; y++) {

            // Pixelkoordinaten auf die imaginäre Achse abgebildet
            c.im = min.im + ((max.im - min.im) * y) / yPixels;

            for (int x = 0; x < xPixels; x++) {

                // Pixelkoordinaten auf die reelle Achse abgebildet
                c.re = min.re + ((max.re - min.re) * x) / xPixels;

                iter = julia(c, max_betrag, max_iter);

                colors[counter++] = colorize(iter, max_iter);
            }
        }

        return Bitmap.createBitmap(colors, xPixels, yPixels, Bitmap.Config.ARGB_8888);
    }


    private int julia(Complex c, double max_betrag, int max_iter) {

        double x = c.re;
        double y = c.im;
        double xtemp;

        int iter = 0;

        while (x * x + y * y < max_betrag && iter < max_iter) {

            xtemp = x * x - y * y + c.re;
            y = 2 * x * y + c.im;
            x = xtemp;

            iter++;
        }

        return iter;
    }

    private int colorize(int iter, int max_iter) {

        //TODO Verschiedene Farbschemen
        int color,
                max_color_value = 255;

        if (iter == max_iter) {
            color = 0xFF000000;
        } else {
            int farbverteilung = max_iter / 3;

            int alpha, r, g, b;
            int r_max = 0 * farbverteilung + farbverteilung / 2,
                    g_max = 1 * farbverteilung + farbverteilung / 2,
                    b_max = 2 * farbverteilung + farbverteilung / 2;
            int r_diff, g_diff, b_diff;

            alpha = 0xFF;

            r_diff = Math.abs(r_max - iter) * max_color_value / farbverteilung;
            r = r_diff < 0 ? 0 : 255 - r_diff;

            g_diff = Math.abs(g_max - iter) * max_color_value / farbverteilung;
            g = g_diff < 0 ? 0 : 255 - g_diff;

            b_diff = Math.abs(b_max - iter) * max_color_value / farbverteilung;
            b = b_diff < 0 ? 0 : 255 - b_diff;

            color = (alpha << 24) | (r << 16) | (g << 8) | b;
        }

        return color;
    }

    public Bitmap zoom(float zoom_x1, float zoom_y1, float zoom_x2, float zoom_y2) {

        // Komplexe Koordinaten, die den Bereich für den Zoom definieren
        Complex zoom_min = new Complex(),
                zoom_max = new Complex();

        // Komplexe Koordinaten für den Zoombereich berechnen
        if (zoom_x1 < zoom_x2) {

            zoom_min.re = cur_min.re + ((cur_max.re - cur_min.re) * zoom_x1 / xPixels);
            zoom_max.re = cur_min.re + ((cur_max.re - cur_min.re) * zoom_x2 / xPixels);

            if (zoom_y1 < zoom_y2) {

                zoom_min.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y1 / yPixels);
                zoom_max.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y2 / yPixels);
            }

            else {

                zoom_min.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y2 / yPixels);
                zoom_max.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y1 / yPixels);
            }
        }

        else {

            zoom_min.re = cur_min.re + ((cur_max.re - cur_min.re) * zoom_x2 / xPixels);
            zoom_max.re = cur_min.re + ((cur_max.re - cur_min.re) * zoom_x1 / xPixels);

            if (zoom_y1 > zoom_y2) {

                zoom_min.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y2 / yPixels);
                zoom_max.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y1 / yPixels);
            }

            else {

                zoom_min.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y1 / yPixels);
                zoom_max.im = cur_min.im + ((cur_max.im - cur_min.im) * zoom_y2 / yPixels);
            }
        }

        // Der Zoombereich wird zum aktuellen bereich
        cur_min = zoom_min;
        cur_max = zoom_max;

        return cur_bm_mb = generateBitmap(cur_min, cur_max);
    }

    public String storeImage(Context context, int location) {

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String imageName = "Mandelbrot_" + timeStamp + ".jpg";

        File storageDir;

        if (location == LOCATION_INTERN) {
            ContextWrapper cw = new ContextWrapper(context);
            storageDir = cw.getDir("Files", Context.MODE_PRIVATE);
        }

        else if (location == LOCATION_EXTERN) {

            storageDir = new File(Environment.getExternalStorageDirectory().getPath() +"/Android/data/" + context.getPackageName() + "/Files");

            if (! storageDir.exists()) {

                if (! storageDir.mkdirs()) {

                    Toast.makeText(context, "Saving Image Failed!", Toast.LENGTH_LONG).show();
                    return null;
                }
            }
        }

        else {
            Log.e(TAG, "Not a valid Location");
            return null;
        }

        File image = new File(storageDir.getPath() + File.separator + imageName);

        try {
            FileOutputStream fos = new FileOutputStream(image);
            cur_bm_mb.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "Image Successfull Stored", Toast.LENGTH_LONG).show();

        return storageDir.getPath();
    }
}
