package postpc.studypartner2.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.GeoPoint;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
import postpc.studypartner2.model.Conversation;
import postpc.studypartner2.model.User;

public class HelperFunctions {

    private static final String TAG = "HelperFunctions";

    // Global Variables
    //SP tags
    public final static String SP_USER = "SP_USER";
    public final static String SP_UID = "SP_UID";

    // intent extras
    // sources
    public final static String SRC_KEY = "source";
    public final static String SRC_LOGIN_NEW = "login_new";
    public final static String SRC_LOGIN_EXISTING = "login_existing";
    public final static String SRC_GOOGLE = "login_google";



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

    public static void showPopup(Context context, View currentView, User user) {
        // set up pop up
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_profile, null);
        TextView profileName = popupView.findViewById(R.id.profile_name);
        TextView profileDesc = popupView.findViewById(R.id.profile_desc);
        ImageView profilePic = popupView.findViewById(R.id.profile_image);
        TextView loc = popupView.findViewById(R.id.profile_popup_location);

        loadImage(context, user.getImage_url(), profilePic);
        profileName.setText(user.getName());
        profileDesc.setText(user.getDescription());
        if (!user.getPrettyLocation(context).isEmpty()) {
            loc.setText(user.getPrettyLocation(context));
        }

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(currentView, Gravity.CENTER, 0, 0);

    }

    private static void loadImage(Context context, String image_uri, ImageView imageView) {
        Glide.with(context)
                .load(image_uri)
                .placeholder(R.drawable.default_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
