package postpc.studypartner2.profile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.google.type.Date;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.chat.Conversation;
import postpc.studypartner2.chat.Message;

import static android.content.Context.MODE_PRIVATE;
import static postpc.studypartner2.utils.HelperFunctions.SP_USER;


public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";

//    private UserRepository mRepository; // todo uncomment - room
    private FirestoreRepository fRepository;
    private LiveData<List<User>> mAllUsers;
    private MutableLiveData<List<User>> mQueryUsers;
    private boolean isUserInRoom;
    private MutableLiveData<String> mUid;

    public UserViewModel(Application application) {
        super(application);
//        mRepository = new UserRepository(application); // todo uncomment - room
//        SharedPreferences sp = application.getApplicationContext().getSharedPreferences() // todo sp
        fRepository = new FirestoreRepository();
        mQueryUsers = new MutableLiveData<>();
        mUid = new MutableLiveData<>();
//        mAllUsers = mRepository.getAllUsers(); // todo uncomment - room
    }

    LiveData<List<User>> getAllUsers() {
        return mAllUsers; }

//    public LiveData<Boolean> isUserRegistered(String uid){
//        android.util.Log.d(TAG, "isUserRegistered: ");
//        return fRepository.isUserRegistered(uid);
//    }

//    public LiveData<User> getUser(String uid)  {
//        Log.d(TAG, "getUser: ");
//        return mRepository.getUser(uid);
//    }

    public LiveData<String> getLoggedInUID(){
        return mUid;
    }

    public void setLoggedInUID(String uid){
        mUid.postValue(uid);
    }

    public LiveData<User> loadUser(String uid){
        return fRepository.loadUser(uid);
    }

    public LiveData<List<User>> getUsersByCourse(String courseNum) {
        return fRepository.getUsersByCourse(courseNum);
    }

//    public LiveData<List<User>> getLastQuery(){
//        return fRepository.getLastQuery();
//    }
//
//    public LiveData<List<User>> getLastUsersQuery(){
//        return mQueryUsers;
//    }
//    public void setLastUsersQuery(List<User> users){
//        mQueryUsers.postValue(users);
//    }

    public void updateUser(String uid, String key, String value){
        fRepository.updateUser(uid, key, value);
    }

    public void updateUser(String uid, String key, Object value){
        fRepository.updateUser(uid, key, value);
    }

    public void addUser(User user){
        // Add to netowrk db (Firestore)
        fRepository.addUser(user);

        // Add to local db (Room)
//        mRepository.insert(user);
    }

    public LiveData<List<User>> getPartners(String uid){
//        return fRepository.getPartners(uid);
        return fRepository.getPartners(uid);
    }

    public LiveData<List<Conversation>> getConversations(String uid){
        //todo

        MutableLiveData<List<Conversation>> liveConvos = new MutableLiveData<>();

        // temp demo convo creation
        ArrayList<Message> messages = new ArrayList<>();

        String senderUID= MainActivity.getCurrentUserID();
        String receiverUID = "123";
        String senderName = "Sapir";
        String messageText = "demo message";
        messages.add(new Message(senderUID, messageText));

        String uid1 = senderUID;
        String uid2 = receiverUID;
        final User partner = new User(receiverUID, "demo user", "this is me", "", "67521");

        Conversation convo = new Conversation(uid1,uid2, partner, messages);
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.add(convo);
        liveConvos.postValue(conversationList);
        return liveConvos;
        /////////////////
    }

    public void saveMessage(String uid1, String uid2, Message msg){
        fRepository.saveMessage(uid1, uid2, msg);
    }

    public LiveData<List<Message>> getMessages(String uid1, String uid2){
        return fRepository.getMessages(uid1, uid2);
    }


//    private void insertUserToRoom(User user) { mRepository.insert(user); }

}
