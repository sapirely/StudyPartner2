package postpc.studypartner2.profile;


import android.telephony.mbms.MbmsErrors;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.chat.Conversation;
import postpc.studypartner2.chat.Message;
import postpc.studypartner2.utils.Log;

class FirestoreRepository {

    private static final String TAG = "FirestoreRepository";
    private FirebaseFirestore firestoreDB;
    private FirebaseUser fbUser;
    private MutableLiveData<User> curUser;
    private MutableLiveData<List<Message>> messagesLiveData;
    private MutableLiveData<List<User>> usersQuery;
    private MutableLiveData<List<User>> partners;
    private MutableLiveData<Boolean> isRegistered;

    // messages
    private DatabaseReference mDatabase;


    // save user to firebase

    public FirestoreRepository(){
        firestoreDB = FirebaseFirestore.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        curUser = new MutableLiveData<>();
        usersQuery = new MutableLiveData<>();
        partners = new MutableLiveData<>();
        messagesLiveData = new MutableLiveData<>();
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
        // todo: remove current user: two queries with <uid and >uid
        // https://stackoverflow.com/questions/47251919/firestore-how-to-perform-a-query-with-inequality-not-equals
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

    public void addPartner(String userUID, String partnerUID){
        // called when approving a request

        // add partners to both users
        addPartnerToDB(userUID, partnerUID);
        android.util.Log.d(TAG, "addPartner: added "+partnerUID+" as partner.");
        addPartnerToDB(partnerUID, userUID);
        android.util.Log.d(TAG, "addPartner: added "+userUID+" as partner.");

        removePartnerRequest(userUID, partnerUID);
    }

    private void removePartnerRequest(String userUID, String partnerUID) {
        DocumentReference userPartnersRef = firestoreDB.collection("partners").document(userUID);
        DocumentReference requesterRef = firestoreDB.collection("users").document(partnerUID);

        userPartnersRef.update("requests", FieldValue.arrayRemove(requesterRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                android.util.Log.d(TAG, "onSuccess: removed partner request successfully");
            }
        });
    }

    private void addPartnerToDB(String userUID, String partnerUID){
        DocumentReference userPartnersRef = firestoreDB.collection("partners").document(userUID);
        DocumentReference partnerRef = firestoreDB.collection("users").document(partnerUID);

        userPartnersRef.update("approved", FieldValue.arrayUnion(partnerRef))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        android.util.Log.d(TAG, "onSuccess: added partner successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating document", e);
                    }
                });
    }

    public void sendPartnerRequest(String userUID, String partnerUID){
        DocumentReference partnerRequestsRef = firestoreDB.collection("partners").document(partnerUID);
        DocumentReference requesterRef = firestoreDB.collection("users").document(userUID);

        partnerRequestsRef.update("requests", FieldValue.arrayUnion(requesterRef))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        android.util.Log.d(TAG, "onSuccess: added request successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating document", e);
                    }
                });

    }

    public LiveData<List<User>> getPartners(String uid){
        final List<User> listUsers = new ArrayList<>();
        DocumentReference docRef = firestoreDB.collection("partners").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    android.util.Log.d(TAG, "onComplete: successfully got partners");
                    DocumentSnapshot document = task.getResult();
                    if (document == null){
                        android.util.Log.d(TAG, "onComplete: empty partner list");
                        return;
                    }
                    List<DocumentReference> list = (List<DocumentReference>) document.get("approved");
                    if (list.isEmpty()){
                        android.util.Log.d(TAG, "onComplete: empty approved partner list ");
                        return;
                    }
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference documentReference : list) {
                        if (documentReference == null){
                            android.util.Log.d(TAG, "onComplete: empty partner list - docref");
                            return;
                        }
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

    public LiveData<List<Message>> getMessages(String uid1, String uid2){
        String conversationID = generateConversationID(uid1, uid2);
        final List<Message> allMessages = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference yourRef = mDatabase.child("convos").child(conversationID).child("messages");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Message msg = ds.getValue(Message.class);
                        Log.d("TAG", "got message "+msg.getmID());
                        allMessages.add(msg);
                }
                messagesLiveData.postValue(allMessages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        yourRef.addListenerForSingleValueEvent(eventListener);
        return messagesLiveData;
    }

    public void saveMessage(String uid1, String uid2, Message msg){
        String conversationID = generateConversationID(uid1, uid2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbRef = mDatabase.child("convos").child(conversationID);
//        String key = mDatabase.child("messages").child(conversationID).push().getKey();

        if ((dbRef.child("uid1").toString() != uid1) && (dbRef.child("uid1").toString() != uid2)){
            // Set up new convo
            dbRef.child("uid1").setValue(uid1);
            dbRef.child("uid2").setValue(uid2);
        }

        String key = dbRef.child("messages").push().getKey();
        msg.setmID(key);
        dbRef.child("messages").child(key).setValue(msg, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                android.util.Log.d(TAG, "onComplete: completed saving message to db: "+databaseError);
            }
        });
        dbRef.child("lastMsg").setValue(msg);
    }

    private String generateConversationID(String uid1, String uid2){
        if (uid1.compareTo(uid2) < 0) {
            // uid1 comes before uid2 (lexicographically)
            return uid1+uid2;
        } else {
            return uid2+uid1;
        }
    }

//    public LiveData<List<Conversation>> getConversations(String uid) {
//
//    }
}