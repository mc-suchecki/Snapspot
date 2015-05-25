package pl.mc.snapspot.utils;

import android.location.Location;
import android.os.Bundle;

import pl.mc.snapspot.MainActivity;

/**
 * Class responsible for listening to location changes and invoking UI updates.
 */
public class LocationListener implements android.location.LocationListener {

    private final long ONE_MINUTE = 60000;
    private Location lastLocation = null;

    private MainActivity activity;

    public LocationListener(final MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onLocationChanged(final Location newLocation) {
        lastLocation = getBetterLocation(lastLocation, newLocation);
        activity.onLocationChange(lastLocation);
    }

    private Location getBetterLocation(final Location older, final Location newer) {
        // new location is always better than no location
        if (older == null) {
            return newer;
        }

        // if time difference is significant, return more recent location
        long timeDifference = newer.getTime() - older.getTime();
        if (timeDifference > ONE_MINUTE) {
            return newer;
        }

        // if time difference is small, check accuracy
        return newer.getAccuracy() < older.getAccuracy() ? newer : older;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO ???
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO ???
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO prompt user to enable the GPS
    }
}
