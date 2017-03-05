package apps.fiefhaus.mandelbrot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    //TODO "Bild speichern"-Funktion
    //TODO Auf Facebook posten
    //TODO UI verbessern
    //TODO Horizontale ansicht
    //TODO Optional: 2-Finger-Zoom
    //Added another Comment to Test Git

    private static final String TAG = "Mandelbrot_main";

    private Mandelbrot mb;

    private ImageView iv_mandelbrot;
    private EditText et_iterations;

    // Bestimmt die Anzahl der Iterationen
    private int max_iter;

    // Komplexe Koordinaten, die den Standardbereich der Mandelbrotmenge definieren
    private Complex def_min = new Complex(-2.5, -1),
                    def_max = new Complex(1, 1);

    // Zoomkoordinaten
    private float zoom_x1, zoom_y1, zoom_x2, zoom_y2;

    // Maximale Anzahl x und y Pixel der ImageView (Höhe und Breite)
    private int xPixels, yPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        iv_mandelbrot = (ImageView) findViewById(R.id.iv_mandelbrot);
        et_iterations = (EditText) findViewById(R.id.et_iterations);

        iv_mandelbrot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = MotionEventCompat.getActionMasked(event);

                switch (action) {

                    case MotionEvent.ACTION_DOWN: {

                        zoom_x1 = event.getX();
                        zoom_y1 = event.getY();

                        break;
                    }

                    case MotionEvent.ACTION_UP: {

                        zoom_x2 = event.getX();
                        zoom_y2 = event.getY();

                        iv_mandelbrot.setImageBitmap(mb.zoom(zoom_x1, zoom_y1, zoom_x2, zoom_y2));

                        break;
                    }
                }

                return true;
            }
        });
    }

    public void generateMandelbrot(View v) {

        max_iter = Integer.valueOf(et_iterations.getText().toString());
        xPixels = iv_mandelbrot.getWidth();
        yPixels = iv_mandelbrot.getHeight();

        mb = new Mandelbrot(def_min, def_max, xPixels, yPixels, max_iter);

        iv_mandelbrot.setImageBitmap(mb.getBitmap());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        File image;
        String imagePath;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        else if (id == R.id.action_storeImageIntern) {
            imagePath = mb.storeImage(getApplicationContext(), Mandelbrot.LOCATION_INTERN);
            addImageToGallery(imagePath);
        }

        else if (id == R.id.action_storeImageToSD) {
            imagePath = mb.storeImage(getApplicationContext(), Mandelbrot.LOCATION_EXTERN);
            addImageToGallery(imagePath);
        }

        return super.onOptionsItemSelected(item);
    }

    private void addImageToGallery(String imagePath) {

        //TODO Funktioniert das auf Smartphones?
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
