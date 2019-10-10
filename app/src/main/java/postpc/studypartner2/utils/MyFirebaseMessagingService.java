package postpc.studypartner2.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;

/**
 * For notifications: will handle the reception and display of notifications.
 * Extends FirebaseMessagingService class in order to receive messages from
 * the FCM server. This service handles the reception and display of notifications.
 * Override OnMessageReceived() function within the service so that it will be
 * called whenever a new notification message is received.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String ADMIN_CHANNEL_ID = "admin_channel";
    private static final String TAG = "MyFirebaseMessagingServ";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random rand = new Random();
        int notificationID =  rand.nextInt(3000);

         /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them.
      */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder  notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_friend)
                .setContentTitle("Add Friend")
            .setContentText("someone added you")
            .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        String adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        Log.d(TAG, "setupChannels: setting up notification channel");

        NotificationChannel adminChannel = new NotificationChannel
                (ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("this is a notification");
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.YELLOW);
        notificationManager.createNotificationChannel(adminChannel);
    }

//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//         replaces the other service. todo
//        String curUserID = MainActivity.getCurrentUserID();
//        FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+curUserID);
//    }


}
