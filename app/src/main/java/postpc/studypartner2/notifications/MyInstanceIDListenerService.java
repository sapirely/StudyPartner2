//package postpc.studypartner2.notifications;
//
//import android.app.Activity;
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class MyInstanceIDListenerService  extends FirebaseMessagingService {
//
//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String newToken = instanceIdResult.getToken();
//                if (user != null){
//                    updateToken(user, newToken);
//                }
//            }
//        });
//
//    }
//
//    private void updateToken(final FirebaseUser user, String newToken) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token = new Token(newToken);
//        ref.child(user.getUid()).setValue(token);
//    }
//
////    @Override
////    public void onMessageReceived(RemoteMessage remoteMessage) {}
//
//}
