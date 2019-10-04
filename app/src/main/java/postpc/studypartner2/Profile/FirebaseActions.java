package postpc.studypartner2.Profile;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

// Singleton
public class FirebaseActions {

    private static final String TAG = "FirebaseActions";

    private final String USERS_DB = "users";

    // static variable single_instance of type Singleton
    private static FirebaseActions firebaseActionsInstance = null;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private FirebaseActions(){
        auth = getFirebaseAuth();
        db = getFirestore();
    }

    /**
     * static method to create instance of FirebaseActions class
     * @return instance of FirebaseActions.
     */
    public static FirebaseActions getInstance()
    {
        if (firebaseActionsInstance == null)
            firebaseActionsInstance = new FirebaseActions();
        return firebaseActionsInstance;
    }

    public FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    public FirebaseFirestore getFirestore(){
        return FirebaseFirestore.getInstance();
    }

    public void addUserToDB(String uid, User user){
        db.collection(USERS_DB).document(uid).set(user);
    }

    public User getUserFromDB(String uid){
        User user;
        DocumentReference docRef = db.collection(USERS_DB).document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
            }
        });


        //todo
        return null;
    }


}
