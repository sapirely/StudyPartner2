package postpc.studypartner2.repository;


import android.net.Uri;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import postpc.studypartner2.model.Conversation;
import postpc.studypartner2.model.Message;
import postpc.studypartner2.model.PartnerList;
import postpc.studypartner2.model.User;
import postpc.studypartner2.utils.Log;

import static postpc.studypartner2.repository.UserViewModel.ALL_ENV_VALUES;
import static postpc.studypartner2.repository.UserViewModel.ALL_TIME_VALUES;

class FirestoreRepository {

    public enum PartnerListType {PARTNERS, REQUESTS};
    private final int MAX_STUDY_TIMES = 3;
    private final int MAX_ENVS = 2;

    private static final String TAG = "FirestoreRepository";
    private FirebaseFirestore firestoreDB;
    private FirebaseUser fbUser;
    private MutableLiveData<User> curUser;
    private MutableLiveData<List<Message>> messagesLiveData;
    private MutableLiveData<List<User>> usersQuery;
    private MutableLiveData<List<User>> partners;
    private MutableLiveData<List<Conversation>> conversationsLiveData;
    private MutableLiveData<String> uriLiveData;

    // messages
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    // save user to firebase

    public FirestoreRepository(){
        firestoreDB = FirebaseFirestore.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        curUser = new MutableLiveData<>();
        usersQuery = new MutableLiveData<>();
        partners = new MutableLiveData<>();
        messagesLiveData = new MutableLiveData<>();
        conversationsLiveData = new MutableLiveData<>();
        uriLiveData = new MutableLiveData<>();
    }

    public void addUser(User user) {
        firestoreDB.collection("users").document(user.getUid()).set(user);
        Log.d(TAG, "addUser: adding user to firebase");
    }

    public LiveData<User> loadUser(final String uid){
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
                        User user = new User(uid);
                        addUser(user);
                        curUser.postValue(user);
                    }
                } else {
                    Log.d(TAG, "get failed with "+task.getException());
                    User user = new User(uid);
                    addUser(user);
                    curUser.postValue(user);
                }
            }
        });
        return curUser;
    }

    private void putPrefKey(HashMap<String, Boolean> preferences, List<String> list, String element, String DBKey){
        if (list.contains(element)){
            preferences.put(DBKey, true);
        }
    }

    private HashMap<String, Boolean> getPreferencesMap(List<String> studyTime, List<String> environment){
        HashMap<String, Boolean> preferences = new HashMap<>();
        if (environment != null) {
            for (String env : ALL_ENV_VALUES) {
                if (env == "quiet") {
                    putPrefKey(preferences, environment, env, "env_quiet");
                } else if (env == "lively") {
                    putPrefKey(preferences, environment, env, "env_lively");
                }
            }
        }

        if (studyTime != null) {
            for (String time : ALL_TIME_VALUES) {
                if (time == "morning") {
                    putPrefKey(preferences, studyTime, time, "study_time_morning");
                } else if (time == "afternoon") {
                    putPrefKey(preferences, studyTime, time, "study_time_afternoon");
                } else if (time == "evening") {
                    putPrefKey(preferences, studyTime, time, "study_time_evening");
                }
            }
        }


        return preferences;
    }


    public LiveData<List<User>> getUsersByCourseComplex(String courseName, List<String> studyTimes, List<String> environments){
//    public LiveData<List<User>> getUsersByCourseOnly(String courseName){
        Log.d(TAG, "getUsersByCourseOnly: fbuser:"+fbUser.getUid());
        String currentUid = fbUser.getUid();

        // None or all filters are selected -> simple query
        if ((studyTimes == null && environments == null) ||
                ((studyTimes.size() == MAX_STUDY_TIMES || studyTimes.size() == 0)
                        &&(environments.size() == MAX_ENVS || environments.size() == 0))){
            return complexQueryHelper(courseName, null, currentUid);
        }

        // complex query
        HashMap<String, Boolean> preferences = getPreferencesMap(studyTimes, environments);
        return complexQueryHelper(courseName, preferences, currentUid);

    }

    private LiveData<List<User>> complexQueryHelper(String courseName, HashMap<String, Boolean> preferences, String currentUid){
        CollectionReference colRef = firestoreDB.collection("users");

        // base query
        com.google.firebase.firestore.Query lessQuery = colRef.whereArrayContains("courses", courseName);

        // add all preferences
        if (preferences != null) {
            for (HashMap.Entry<String, Boolean> entry : preferences.entrySet()) {
                lessQuery = lessQuery.whereEqualTo(entry.getKey(), entry.getValue());
            }
        }

        // remove current user from results
        com.google.firebase.firestore.Query greaterQuery = lessQuery.whereGreaterThan("uid", currentUid);
        lessQuery = lessQuery.whereLessThan("uid", currentUid);

        // combine tasks and query db
        Task firstQuery = lessQuery.get();
        Task secondQuery = greaterQuery.get();

        Task combinedTask = Tasks.whenAllSuccess(firstQuery , secondQuery)
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        List<QuerySnapshot> queryDocumentSnapshots = (List<QuerySnapshot>)(Object) objects;
                        List<User> userList = new ArrayList<>();
                        for (QuerySnapshot q:queryDocumentSnapshots){
                            userList.addAll(q.toObjects(User.class));
                        }
                        usersQuery.postValue(userList);
                    }
                });
        return usersQuery;
    }


    public LiveData<List<User>> getUsersByCourseTemp(String courseName){
        Log.d(TAG, "getUsersByCourseOnly: fbuser:"+fbUser.getUid());
        String currentUid = fbUser.getUid();
        CollectionReference colRef = firestoreDB.collection("users");
        com.google.firebase.firestore.Query lessQuery = colRef.whereArrayContains("courses", courseName).whereLessThan("uid", currentUid);
        com.google.firebase.firestore.Query greaterQuery = colRef.whereArrayContains("courses", courseName).whereGreaterThan("uid", currentUid);
        Task firstQuery = lessQuery.get();
        Task secondQuery = greaterQuery.get();

        Task combinedTask = Tasks.whenAllSuccess(firstQuery , secondQuery)
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        List<QuerySnapshot> queryDocumentSnapshots = (List<QuerySnapshot>)(Object) objects;
                        List<User> userList = new ArrayList<>();
                        for (QuerySnapshot q:queryDocumentSnapshots){
                            userList.addAll(q.toObjects(User.class));
                        }
                        usersQuery.postValue(userList);
                    }
                });
        return usersQuery;
    }

    public void addPartner(String userUID, String partnerUID){
        // called when approving a request

        // add partners to both users
        addPartnerToDB(userUID, partnerUID);
        Log.d(TAG, "addPartner: added "+partnerUID+" as partner.");
        addPartnerToDB(partnerUID, userUID);
        Log.d(TAG, "addPartner: added "+userUID+" as partner.");

        removePartnerRequest(userUID, partnerUID);
    }

    public void removePartnerRequest(String userUID, String partnerUID) {
        DocumentReference userPartnersRef = firestoreDB.collection("partners").document(userUID);
        DocumentReference requesterRef = firestoreDB.collection("users").document(partnerUID);

        userPartnersRef.update("requests", FieldValue.arrayRemove(requesterRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: removed partner request successfully");
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
                        Log.d(TAG, "onSuccess: added partner successfully");
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
                        Log.d(TAG, "onSuccess: added request successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating document", e);
                    }
                });

    }

    private static String getPathFromType(PartnerListType type){
        if (type == PartnerListType.PARTNERS){
            return "approved";
        } else if (type == PartnerListType.REQUESTS){
            return "requests";
        } else {
            throw new IllegalArgumentException();
        }
    }

    public LiveData<List<User>> getPartnersList(final String uid, final PartnerListType type){

        final List<User> listUsers = new ArrayList<>();
        DocumentReference docRef = firestoreDB.collection("partners").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully got partners");
                    DocumentSnapshot document = task.getResult();
                    if (document == null){
                        Log.d(TAG, "onComplete: empty partner/request lists");
                        createPartnerList(uid);
                        return;
                    }
                    final String path = getPathFromType(type);
                    List<DocumentReference> list = (List<DocumentReference>) document.get(path);
                    if (list == null || list.isEmpty()){
                        Log.d(TAG, "onComplete: empty approved partner list ");
                        partners.postValue(listUsers);
                        return;
                    }
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference documentReference : list) {
                        if (documentReference == null){
                            Log.d(TAG, "onComplete: empty partner list - docref");
                            partners.postValue(listUsers);
                            return;
                        }
                        Task<DocumentSnapshot> documentSnapshotTask = documentReference.get();
                        tasks.add(documentSnapshotTask);
                    }
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
                        Log.d(TAG, "onComplete: failed getting partners");
                        createPartnerList(uid);
                    }
            }
        });
        return partners;
    }

    public LiveData<List<User>> createPartnerList(final String uid){
        PartnerList pl = new PartnerList(uid);
        final List<User> listUsers = new ArrayList<>();
        firestoreDB.collection("partners").document(uid).set(pl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: created new partner/requests list for "+uid);
                partners.postValue(listUsers);
            }
        });
        return partners;
    }


    public LiveData<List<String>> getPartnersUIDS(String uid, final PartnerListType type){

        final List<String> listUsers = new ArrayList<>();
        final MutableLiveData<List<String>> uids = new MutableLiveData<>();
        DocumentReference docRef = firestoreDB.collection("partners").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully got partners");
                    DocumentSnapshot document = task.getResult();
                    if (document == null){
                        Log.d(TAG, "onComplete: empty partner list");
                        uids.postValue(listUsers);
                        return;
                    }
                    final String path = getPathFromType(type);
                    final List<DocumentReference> list = (List<DocumentReference>) document.get(path);
                    if (list == null || list.isEmpty()){
                        Log.d(TAG, "onComplete: empty approved partner list ");
                        uids.postValue(listUsers);
                        return ;
                    }
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference documentReference : list) {
                        if (documentReference == null){
                            Log.d(TAG, "onComplete: empty partner list - docref");
                            uids.postValue(listUsers);
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
                                listUsers.add(((User) object).getUid());
                            }
                            uids.postValue(listUsers);
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete: failed getting partners");;
                }
            }
        });
        return uids;
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

    public void addToUserArrayField(final String uid, final String key, final String value){
        DocumentReference docRef = firestoreDB.collection("users").document(uid);
        docRef
                .update(key, FieldValue.arrayUnion(value))
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

    public void removeFromUserArrayField(final String uid, final String key, final String value){
        DocumentReference docRef = firestoreDB.collection("users").document(uid);
        docRef
                .update(key, FieldValue.arrayRemove(value))
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

    public void saveMessage(final String uid1, User otherUser, Message msg){
        final String uid2=otherUser.getUid();
        String conversationID = generateConversationID(uid1, uid2);

        final DatabaseReference dbRef = mDatabase.child("convos").child(conversationID);
//        String key = mDatabase.child("messages").child(conversationID).push().getKey();

        // check if convo initiated - if one of the uids is set to this user's uid
        if ((!dbRef.child("uid1").toString().equals(uid1)) && (!dbRef.child("uid1").toString().equals(uid2))){
            // Set up new convo
            dbRef.child("uid1").setValue(uid1);
            dbRef.child("uid2").setValue(uid2);

            firestoreDB.collection("users").document(uid1).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    dbRef.child("users").child(uid1).setValue(currentUser); // current user
                }
            });

            dbRef.child("users").child(uid2).setValue(otherUser); // other user
        }

        String key = dbRef.child("messages").push().getKey();
        msg.setmID(key);
        dbRef.child("messages").child(key).setValue(msg, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "onComplete: completed saving message to db: "+databaseError);
            }
        });
        dbRef.child("lastMsg").setValue(msg);
        dbRef.child("unread").setValue(true);
    }

    private String generateConversationID(String uid1, String uid2){
        if (uid1.compareTo(uid2) < 0) {
            // uid1 comes before uid2 (lexicographically)
            return uid1+uid2;
        } else {
            return uid2+uid1;
        }
    }

    public void updateUnreadConversation(String uid1, String uid2, Boolean isUnread) {
        String conversationID = generateConversationID(uid1, uid2);
        final DatabaseReference dbRef = mDatabase.child("convos").child(conversationID);
        dbRef.child("unread").setValue(isUnread);

        // update users
        updateUsersInConvo(dbRef, uid1, uid2);
    }

    private void updateUsersInConvo(final DatabaseReference dbRef, final String uid1, final String uid2){

        //update user 1
        firestoreDB.collection("users").document(uid1).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                dbRef.child("users").child(uid1).setValue(currentUser); // current user
            }
        });

        // update user 2
        firestoreDB.collection("users").document(uid2).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                dbRef.child("users").child(uid2).setValue(currentUser); // current user
            }
        });

    }

    public LiveData<List<Conversation>> getConversations(String uid) {
        Query myConversations = mDatabase.child("convos").orderByChild("uid1").equalTo(uid); // if the user is user 1
        Query myConversations2 = mDatabase.child("convos").orderByChild("uid2").equalTo(uid); // if the user is user 2

        final List<Conversation> conversationList = new ArrayList<>();

        myConversations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Conversation conversation = ds.getValue(Conversation.class);
                    Log.d("TAG", "got conversation ");
                    conversationList.add(conversation);
                }
                conversationsLiveData.postValue(conversationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: retrieving conversations cancelled");
            }
        });

        myConversations2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Conversation conversation = ds.getValue(Conversation.class);
                    Log.d("TAG", "got conversation ");
                    conversationList.add(conversation);
                }
                conversationsLiveData.postValue(conversationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: retrieving conversations cancelled");
            }
        });

        return conversationsLiveData;


    }

    public LiveData<String> uploadProfileImageToStorage(final String uid, final Uri localUri){
        StorageReference storageReference =
                FirebaseStorage.getInstance()
                        .getReference(uid)
                        .child(localUri.getLastPathSegment());

        storageReference.putFile(localUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        // update the profile to use the image
                                                        String uri = task.getResult().toString();
                                                        // save to db
                                                        updateUser(uid, "image_url", uri);
                                                        // return the uri to the ui
                                                        uriLiveData.postValue(uri);
                                                    }
                                                }
                                            });
                        } else {
                            Log.d(TAG, "Image upload task failed: "+
                                    task.getException());
                        }
                    }
                });

        return uriLiveData;
    }

}