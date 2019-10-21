package postpc.studypartner2.profile;


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
import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.chat.Conversation;
import postpc.studypartner2.chat.Message;
import postpc.studypartner2.utils.Log;

class FirestoreRepository {

    public enum PartnerListType {PARTNERS, REQUESTS};

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

    public LiveData<List<User>> getUsers(String courseName){
        android.util.Log.d(TAG, "getUsers: fbuser:"+fbUser.getUid());
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

    public void removePartnerRequest(String userUID, String partnerUID) {
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

    private static String getPathFromType(PartnerListType type){
        if (type == PartnerListType.PARTNERS){
            return "approved";
        } else if (type == PartnerListType.REQUESTS){
            return "requests";
        } else {
            throw new IllegalArgumentException();
        }
    }

    public LiveData<List<User>> getPartnersList(String uid, final PartnerListType type){

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
                        partners.postValue(listUsers);
                        return;
                    }
                    final String path = getPathFromType(type);
                    List<DocumentReference> list = (List<DocumentReference>) document.get(path);
                    if (list == null || list.isEmpty()){
                        android.util.Log.d(TAG, "onComplete: empty approved partner list ");
                        partners.postValue(listUsers);
                        return ;
                    }
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference documentReference : list) {
                        if (documentReference == null){
                            android.util.Log.d(TAG, "onComplete: empty partner list - docref");
                            partners.postValue(listUsers);
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
//            addConversationToUsersLists(uid1, uid2);
            dbRef.child("uid1").setValue(uid1);
            dbRef.child("uid2").setValue(uid2);
//            dbRef.child("otherUser").setValue(otherUser);
//            DocumentReference user1ref = firestoreDB.collection("users").document(uid1);
//            DocumentReference user2ref = firestoreDB.collection("users").document(uid2);

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

    public LiveData<List<Conversation>> getConversations(String uid) {
        Query myConversations = mDatabase.child("convos").orderByChild("uid1").equalTo(uid);
        Query myConversations2 = mDatabase.child("convos").orderByChild("uid2").equalTo(uid);

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
                android.util.Log.d(TAG, "onCancelled: retrieving conversations cancelled");
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
                android.util.Log.d(TAG, "onCancelled: retrieving conversations cancelled");
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

//    public LiveData<List<Conversation>> getConversations(String uid) {
//        final List<Conversation> conversationList = new ArrayList<>();
//        DocumentReference docRef = firestoreDB.collection("conversations").document(uid);
//
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    android.util.Log.d(TAG, "onComplete: successfully got conversations");
//                    DocumentSnapshot document = task.getResult();
//
//                    List<DocumentReference> list = (List<DocumentReference>) document.get("convosList");
//                    if (list == null){
//                        android.util.Log.d(TAG, "onComplete: empty approved partner list ");
//                        return;
//                    }
//
//                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
//                    for (DocumentReference documentReference : list) {
//                        if (documentReference == null){
//                            android.util.Log.d(TAG, "onComplete: empty partner list - docref");
//                            return;
//                        }
//                        Task<DocumentSnapshot> documentSnapshotTask = documentReference.get();
//                        tasks.add(documentSnapshotTask);
//                    }
//                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
//                        @Override
//                        public void onSuccess(List<Object> objectList) {
//                            //Do what you need to do with your list
//                            for (Object object : objectList) {
//                                object = ((DocumentSnapshot) object).toObject(Conversation.class);
//                                conversationList.add((Conversation) object);
//                            }
//                            conversations.postValue(conversationList);
//                        }
//                    });
//                } else {
//                    android.util.Log.d(TAG, "onComplete: failed getting partners");;
//                }
//            }
//        });
//        return conversations;
//    }

//    private void addConversationToUsersLists(String uid1, String uid2) {
//        String conversationID = generateConversationID(uid1, uid2);
//
//        // add to first user
//        addConversationToUsersListsInDB(uid1, conversationID);
//        // add to second user
//        addConversationToUsersListsInDB(uid2, conversationID);
//    }
//
//    private void addConversationToUsersListsInDB(String uid, String conversationID) {
//        DatabaseReference userRef = mDatabase.child("conversations").child(conversationID);
//        DatabaseReference convoRef = mDatabase.child("convos").child(conversationID);
//
//        userRef.child(conversationID).setValue(convoRef);
//    }
}