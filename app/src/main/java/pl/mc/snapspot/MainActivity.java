package pl.mc.snapspot;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Activity representing main app view - displaying list of places.
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private LocationListener locationListener;
    private LocationManager lm;
    private Looper looper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO divide this method into smaller ones
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setup Toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // setup location-related classes
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new pl.mc.snapspot.utils.LocationListener(this);
        HandlerThread thread = new HandlerThread("locationThread");
        thread.start();
        looper = thread.getLooper();
        // link the LocationListener and request an update every 5 seconds or every 10 meters
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener, looper);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener, looper);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // detach the LocationListener
        if (locationListener != null) {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.removeUpdates(locationListener);
            locationListener = null;
        }
        // detach the location thread
        if (looper != null) {
            looper.quit();
            looper = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {
            this.updateLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateLocation() {
        lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, looper);
        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
    }

    public void onLocationChange(final Location location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // refresh values in UI
                TextView latitude = (TextView) findViewById(R.id.lat_label);
                latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
                TextView longitude = (TextView) findViewById(R.id.long_label);
                longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
                TextView accuracy = (TextView) findViewById(R.id.accuracy_label);
                accuracy.setText("Accuracy: " + String.valueOf(location.getAccuracy()) + "m " +
                        "(" + location.getProvider() + " provider)");
            }
        });
    }
}
