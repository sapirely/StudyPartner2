package postpc.studypartner2.utils;

import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.chat.Conversation;
import postpc.studypartner2.profile.User;

public class HelperFunctions {

    private static final String TAG = "HelperFunctions";

    // Global Variables
    //SP tags
    public final static String SP_USER = "SP_USER";
    public final static String SP_UID = "SP_UID";

    public static GeoPoint locationToGeoPoint(Location loc)
    {
        return new GeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public static String determineOtherUserUIDFromConversation(Conversation conversation){
        String uid;
        if (conversation.getUid1().equals(MainActivity.getCurrentUserID())){
            // uid1 = current user, otherUser uid should be uid2
            return conversation.getUid2();
        } else {
            return conversation.getUid1();
        }
    }

    public static int getDistanceBetweenTwoUsers(User u1, User u2) {

        if (u1==null){
            u1 = new User();
//            u1.setMyLocation(new GeoPoint(37.4219983,-122.084)); // todo remove
            u1.setLocation(37.4219983,-122.084); // todo remove
        }
//        else if (u1==null || u2==null){
        if (u2==null){
            Log.d(TAG, "getDistanceBetweenTwoUsers: one of the users is null");
            return -1;
        }
        if (u1.getLocation()==null || u2.getLocation()==null){
            Log.d(TAG, "getDistanceBetweenTwoUsers: one of the locations is null");
            return -1;
        }
        float[] distance = new float[2];
        double lat1 = u1.getLocation().getLatitude();
        double lat2 = u2.getLocation().getLatitude();
        double lon1 = u1.getLocation().getLongitude();
        double lon2 = u2.getLocation().getLongitude();

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

//            return distance[0];
        return (int) metresToKm(Math.floor(distance[0]));
    }

    public static String getStringDistanceBetweenTwoUsers(User u1, User u2){
        int dist = getDistanceBetweenTwoUsers(u1,u2);
        if (dist == 0){
            return "< 1";
        } else if (dist == -1) {
            return "N/A";
        } else {
            return (dist+"");
        }
    }

    public static double metresToKm(double distance){
        return distance/1000;
    }
}
