package postpc.studypartner2.utils;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class HelperFunctions {

    // Global Variables
    //SP tags
    public final static String SP_USER = "SP_USER";
    public final static String SP_UID = "SP_UID";

    public static GeoPoint locationToGeoPoint(Location loc)
    {
        return new GeoPoint(loc.getLatitude(), loc.getLongitude());
    }
}
