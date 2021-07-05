package com.example.mobilemechanic.Util;

//import android.location.Location
//import com.mapbox.android.core.location.LocationEngineCallback
//import com.mapbox.android.core.location.LocationEngineResult
//import com.okellosoftwarez.letsmovedriver.ui.notifications.NotificationsFragment
//import java.lang.ref.WeakReference

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobilemechanic.R;
import com.example.mobilemechanic.bridge.MainActivity;
import com.example.mobilemechanic.viewScreen.NavigationScreen;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

public class locationUpdater implements LocationEngineCallback<LocationEngineResult> {

    public static Location location;
//    Location location;
    private final WeakReference<NavigationScreen> activityWeakReference;

    public locationUpdater(NavigationScreen activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

//    var  location : Location? = null
//    private val weakFragmentReference : WeakReference<NotificationsFragment> = WeakReference(fragment)

    /**
     * The LocationEngineCallback interface's method which fires when the device's location has changed.
     *
     * @param result the LocationEngineResult object which has the last known location within it.
     */

    @Override
    public void onSuccess(LocationEngineResult result) {
        NavigationScreen activity = activityWeakReference.get();

        if (activity != null) {
            location = result.getLastLocation();

            if (location == null) {
                return;
            }

            // Create a Toast which displays the new location's coordinates
//            Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
//                    String.valueOf(result.getLastLocation().getLatitude()), String.valueOf(result.getLastLocation().getLongitude())),
//                    Toast.LENGTH_SHORT).show();

// Pass the new location to the Maps SDK's LocationComponent
//            if (activity.mapboxMap != null && result.getLastLocation() != null) {
//                activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
//            }
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        Log.d("LocationChangeActivity", exception.getLocalizedMessage());
        NavigationScreen activity = activityWeakReference.get();
        if (activity != null) {
            Toast.makeText(activity, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
//    override fun onSuccess(result: LocationEngineResult?) {
//
//        if (weakFragmentReference.get() == null || result == null || result.lastLocation == null) {
//            return
//        }
//        else {
//             location = result.lastLocation
//        }
//
//    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location can not be captured
     *
     * @param exception the exception message
     */
//    override fun onFailure(exception: Exception) {
//        if (weakFragmentReference.get() == null)
//            return
////        Toast.makeText(weakFragmentReference, "Error : " + exception.localizedMessage, Toast.LENGTH_SHORT).show()
//    }
//}