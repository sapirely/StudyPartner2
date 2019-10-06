package postpc.studypartner2.Profile;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.Partners.PartnerList;
import postpc.studypartner2.Utils.Log;

class FirestoreRepository {

    private static final String TAG = "FirestoreRepository";
    private FirebaseFirestore firestoreDB;
    private FirebaseUser fbUser;
    private MutableLiveData<User> curUser;
    private MutableLiveData<List<User>> usersQuery;
    private MutableLiveData<List<User>> partners;
    private MutableLiveData<Boolean> isRegistered;

    // save user to firebase

    public FirestoreRepository(){
        firestoreDB = FirebaseFirestore.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        curUser = new MutableLiveData<>();
        usersQuery = new MutableLiveData<>();
        partners = new MutableLiveData<>();
    }

    public void addUser(User user) {
//        DocumentReference documentReference = firestoreDB.collection("users").document(uid);
        firestoreDB.collection("users").document(user.getUid()).set(user);
        android.util.Log.d(TAG, "addUser: adding user to firebase");
//        new insertUserToFirestoreAsync(firestoreDB).execute(user);
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
                        curUser.postValue(new User("-1", null, null, null, ""));
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
        return curUser;
    }

    public LiveData<List<User>> getUsersByCourse(String courseNum){
        CollectionReference colRef = firestoreDB.collection("users");
        colRef.whereArrayContains("courses", courseNum).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    android.util.Log.d(TAG, "onComplete: successful query");
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    if (queryDocumentSnapshots.isEmpty()){
                        android.util.Log.d(TAG, "onComplete: empty query ");
                    } else {
                        android.util.Log.d(TAG, "onComplete: posting value");
                        usersQuery.postValue(queryDocumentSnapshots.toObjects(User.class));
                    }
                }
            }
        });
        return usersQuery;
    }

//    public LiveData<List<User>> getPartners(String uid){
    public LiveData<List<User>> getPartners(String uid){
//        CollectionReference partnersRef = firestoreDB.collection("partners");
//        partnersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        final List<User> listUsers = new ArrayList<>();
        DocumentReference docRef = firestoreDB.collection("partners").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    android.util.Log.d(TAG, "onComplete: successfully got partners");
                    DocumentSnapshot document = task.getResult();
                    List<DocumentReference> list = (List<DocumentReference>) document.get("approved");
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference documentReference : list) {
                        Task<DocumentSnapshot> documentSnapshotTask = documentReference.get();
                        tasks.add(documentSnapshotTask);
                    }
//                    docrefToUserHelper(listUsers, tasks);
                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objectList) {
                            //Do what you need to do with your list
                            for (Object object : objectList) {
                                object = ((DocumentSnapshot) object).toObject(User.class);
                                listUsers.add((User) object);
                            }
                            partners.postValue(listUsers);
                        }
                    });
                } else {
                        android.util.Log.d(TAG, "onComplete: failed getting partners");;
                    }
            }
        });
        return partners;
    }


    private void docrefToUserHelper(final List<User> userList, List<Task<DocumentSnapshot>> tasks){
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objectList) {
                //Do what you need to do with your list
                for (Object object : objectList) {
                    object = ((DocumentSnapshot) object).toObject(User.class);
                    userList.add((User) object);
                }
            }
        });
    }

    public LiveData<List<User>> getLastQuery(){
        return usersQuery;
    }

    public void updateUser(final String uid, final String key, final Object value){
        DocumentReference docRef = firestoreDB.collection("users").document(uid);
        docRef
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated:"+key+" : "+value);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating document", e);
                    }
                });
    }

//

//    public LiveData<Boolean> isUserRegistered(String uid){
//        firestoreDB.collection("users")
//                .whereEqualTo("uid", uid)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if (document.getData().isEmpty()){
//                                    isRegistered.postValue(false);
//                                } else {
//                                    isRegistered.postValue(true);
//                                }
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            isRegistered.postValue(false);
//                            Log.d(TAG, "Error getting documents: "+ task.getException());
//                        }
//                    }
//                });
//        return isRegistered;
//
//    }

}