package postpc.studypartner2.profile;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import postpc.studypartner2.chat.Conversation;
import postpc.studypartner2.chat.Message;

import postpc.studypartner2.profile.FirestoreRepository.PartnerListType;


public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";
    public static final ArrayList<String> ALL_ENV_VALUES = (new ArrayList<String>(
            Arrays.asList("quiet",
                    "lively")));

    public static final ArrayList<String> ALL_TIME_VALUES = (new ArrayList<String>(
            Arrays.asList("morning","afternoon",
                    "evening")));

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

    public LiveData<List<User>> getUsersByCourseOnly(String courseNum) {
//        return fRepository.getUsersByCourseOnly(courseNum);
        return fRepository.getUsersByCourseComplex(courseNum, null, null);
    }

    public LiveData<List<User>> getUsersQuery(String courseNum, List<String> studyTimes, List<String> environments) {
//        return fRepository.getUsersByCourseOnly(courseNum);
        return fRepository.getUsersByCourseComplex(courseNum, studyTimes, environments);
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

    public LiveData<String> uploadProfileImageToStorage(String uid, Uri localUri){
        if (localUri == null){
            throw new IllegalArgumentException();
        }
        return fRepository.uploadProfileImageToStorage(uid, localUri);

    }

    public void addCourse(String uid, Course course){
        fRepository.addToUserArrayField(uid, "courses", course.getName());
    }

    public void removeCourse(String uid, Course course) {
        fRepository.removeFromUserArrayField(uid, "courses", course.getName());
    }

    public void addStudyTime(String uid, String value){
        fRepository.addToUserArrayField(uid, "study_time", value);
    }

    public void removeStudyTime(String uid, String value){
        fRepository.removeFromUserArrayField(uid, "study_time", value);
    }

    public void updateUser(String uid, String key, String value){
        fRepository.updateUser(uid, key, value);
    }

    public void addEnvironment(String uid, String value){
        fRepository.addToUserArrayField(uid, "environment", value);
    }

    public void removeEnvironment(String uid, String value) {
        fRepository.removeFromUserArrayField(uid, "environment", value);
    }

    private void updateEnvironments(String uid, Object value){
        List<String> values = (List<String>)value;

        for (String env: ALL_ENV_VALUES){
            if (env == "quiet"){
                if (values.contains(env)){
                    fRepository.updateUser(uid, "env_quiet", true);
                } else {
                    fRepository.updateUser(uid, "env_quiet", false);

                }
            }
            if (env == "lively"){
                if (values.contains(env)){
                    fRepository.updateUser(uid, "env_lively", true);
                } else {
                    fRepository.updateUser(uid, "env_lively", false);

                }
            }
        }
    }

    private void updateStudyTimes(String uid, Object value){

        List<String> values = (List<String>)value;

        for (String time: ALL_TIME_VALUES){
            if (time == "morning"){
                if (values.contains(time)){
                    fRepository.updateUser(uid, "study_time_morning", true);
                } else {
                    fRepository.updateUser(uid, "study_time_morning", false);

                }
            }
            if (time == "afternoon"){
                if (values.contains(time)){
                    fRepository.updateUser(uid, "study_time_afternoon", true);
                } else {
                    fRepository.updateUser(uid, "study_time_afternoon", false);

                }
            }
            if (time == "evening"){
                if (values.contains(time)){
                    fRepository.updateUser(uid, "study_time_evening", true);
                } else {
                    fRepository.updateUser(uid, "study_time_evening", false);

                }
            }
        }
    }


    public void updateUser(String uid, String key, Object value){
        if (key == "environment"){
            updateEnvironments(uid, value);
        } else if (key == "study_time"){
            updateStudyTimes(uid, value);
        } else {
            fRepository.updateUser(uid, key, value);
        }
    }

    public void addUser(User user){
        // Add to netowrk db (Firestore)
        fRepository.addUser(user);

        // Add to local db (Room)
//        mRepository.insert(user);
    }

    public void addPartner(String currentUID, String partnerUID){
        fRepository.addPartner(currentUID, partnerUID);
    }

    public void removeRequest(String currentUID, String partnerUID){
        fRepository.removePartnerRequest(currentUID, partnerUID);
    }

    public LiveData<List<User>> getPartners(String uid){
//        return fRepository.getPartnersList(uid);
        return fRepository.getPartnersList(uid, PartnerListType.PARTNERS);
    }

    public LiveData<List<String>> getPartnersUIDS(String uid){
        return fRepository.getPartnersUIDS(uid, PartnerListType.PARTNERS);
    }

    public LiveData<List<User>> getRequests(String uid){
//        return fRepository.getPartnersList(uid);
        return fRepository.getPartnersList(uid, PartnerListType.REQUESTS);
    }

//    public LiveData<List<Conversation>> getConversations(String uid){
////        return fRepository.getConversations(uid);
//
//        MutableLiveData<List<Conversation>> liveConvos = new MutableLiveData<>();
//
//        // temp demo convo creation
//        ArrayList<Message> messages = new ArrayList<>();
//
//        String senderUID= MainActivity.getCurrentUserID();
//        String receiverUID = "123";
//        String senderName = "Sapir";
//        String messageText = "demo message";
//        messages.add(new Message(senderUID, messageText));
//
//        String uid1 = senderUID;
//        String uid2 = receiverUID;
//        final User partner = new User(receiverUID, "demo user", "this is me", "", "67521");
//
//        Conversation convo = new Conversation(uid1,uid2, partner, messages);
//        List<Conversation> conversationList = new ArrayList<>();
//        conversationList.add(convo);
//        liveConvos.postValue(conversationList);
//        return liveConvos;
//        /////////////////
//    }

    public void saveMessage(String uid1, User otherUser, Message msg){
        fRepository.saveMessage(uid1, otherUser, msg);
    }

    public LiveData<List<Message>> getMessages(String uid1, String uid2){
        return fRepository.getMessages(uid1, uid2);
    }

    public LiveData<List<Conversation>> getConversations(String uid){
        return fRepository.getConversations(uid);
    }

    public void sendPartnerRequest(String userUID, String partnerUID) {
        fRepository.sendPartnerRequest(userUID, partnerUID);
    }


//    private void insertUserToRoom(User user) { mRepository.insert(user); }

}
