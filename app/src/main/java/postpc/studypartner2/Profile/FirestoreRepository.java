package postpc.studypartner2.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import postpc.studypartner2.Utils.Log;

class FirestoreRepository {

    private final String TAG = "FIREBASE_REPOSITORY";
    private FirebaseFirestore firestoreDB;
    private FirebaseUser fbUser;
    private MutableLiveData<User> curUser;

    // save user to firebase

    public FirestoreRepository(){
        firestoreDB = FirebaseFirestore.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        curUser = new MutableLiveData<>();
    }

    public void addUser(User user) {
//        DocumentReference documentReference = firestoreDB.collection("users").document(uid);
        firestoreDB.collection("users").document(user.getUid()).set(user);
    }

    public LiveData<User> loadUser(String uid){
        DocumentReference docRef = firestoreDB.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        curUser.postValue(document.toObject(User.class));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
        return curUser;
    }

}