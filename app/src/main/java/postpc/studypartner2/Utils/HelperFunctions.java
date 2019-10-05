package postpc.studypartner2.Utils;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class HelperFunctions {

    public static GeoPoint locationToGeoPoint(Location loc)
    {
        return new GeoPoint(loc.getLatitude(), loc.getLongitude());
    }
}
