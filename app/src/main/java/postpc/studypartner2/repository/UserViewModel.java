package postpc.studypartner2.repository;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import postpc.studypartner2.model.Conversation;
import postpc.studypartner2.model.Message;

import postpc.studypartner2.model.Course;
import postpc.studypartner2.model.MyLocation;
import postpc.studypartner2.model.User;
import postpc.studypartner2.repository.FirestoreRepository.PartnerListType;
import postpc.studypartner2.utils.Log;

/***
 * A class that mediates between the UI and the data operations.
 */
public class UserViewModel extends AndroidViewModel {

    MutableLiveData<MyLocation> loc = new MutableLiveData<>();

    private static final String TAG = "UserViewModel";
    public static final ArrayList<String> ALL_ENV_VALUES = (new ArrayList<String>(
            Arrays.asList("quiet",
                    "lively")));

    public static final ArrayList<String> ALL_TIME_VALUES = (new ArrayList<String>(
            Arrays.asList("morning","afternoon",
                    "evening")));

    private FirestoreRepository fRepository;
    private MutableLiveData<String> mUid;

    public UserViewModel(Application application) {
        super(application);
        fRepository = new FirestoreRepository();
        mUid = new MutableLiveData<>();
    }

    /////////////////////////////////
    ////        Search         /////
    ////////////////////////////////

    /***
     * Get users by course and preferences.
     * @param courseName
     * @param studyTimes
     * @param environments
     * @return LiveData of the list of users that match the query.
     */
    public LiveData<List<User>> getUsersQuery(String courseName, List<String> studyTimes, List<String> environments) {
        return fRepository.getUsersByCourseComplex(courseName, studyTimes, environments);
    }

    /////////////////////////////////
    ////    User Settings       /////
    ////////////////////////////////

    public void addUser(User user){
        // Add to netowrk db (Firestore)
        fRepository.addUser(user);
        fRepository.createPartnerList(user.getUid());

        // Add to local db (Room)
//        mRepository.insert(user);
    }

    public LiveData<User> loadUser(String uid){
        return fRepository.loadUser(uid);
    }

    public void updateUser(String uid, String key, Object value){
        if (key.equals("environment")){
            updateEnvironments(uid, value);
        } else if (key.equals("study_time")){
            updateStudyTimes(uid, value);
        } else {
            fRepository.updateUser(uid, key, value);
        }
    }

    public LiveData<MyLocation> getLocation(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "onSuccess: got location "+location.toString());
                            MyLocation geo = new MyLocation(location.getLatitude(), location.getLongitude());
                            loc.postValue(geo);

                        } else {
                            Log.d(TAG, "onSuccess: location is null");
                        }
                    }
                });
        return loc;
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
            if (time.equals("morning")){
                if (values.contains(time)){
                    fRepository.updateUser(uid, "study_time_morning", true);
                } else {
                    fRepository.updateUser(uid, "study_time_morning", false);

                }
            }
            if (time.equals("afternoon")){
                if (values.contains(time)){
                    fRepository.updateUser(uid, "study_time_afternoon", true);
                } else {
                    fRepository.updateUser(uid, "study_time_afternoon", false);

                }
            }
            if (time.equals("evening")){
                if (values.contains(time)){
                    fRepository.updateUser(uid, "study_time_evening", true);
                } else {
                    fRepository.updateUser(uid, "study_time_evening", false);

                }
            }
        }
    }

    public LiveData<String> uploadProfileImageToStorage(String uid, Uri localUri){
        if (localUri == null){
            throw new IllegalArgumentException();
        }
        return fRepository.uploadProfileImageToStorage(uid, localUri);

    }


    /////////////////////////////////
    ////         Partners       /////
    ////////////////////////////////


    public void addPartner(String currentUID, String partnerUID){
        fRepository.addPartner(currentUID, partnerUID);
    }

    public LiveData<List<User>> getPartners(String uid){
        return fRepository.getPartnersList(uid, PartnerListType.PARTNERS);
    }

    public LiveData<List<String>> getPartnersUIDS(String uid){
        return fRepository.getPartnersUIDS(uid, PartnerListType.PARTNERS);
    }

    public void sendPartnerRequest(String userUID, String partnerUID) {
        fRepository.sendPartnerRequest(userUID, partnerUID);
    }

    public LiveData<List<User>> getRequests(String uid){
        return fRepository.getPartnersList(uid, PartnerListType.REQUESTS);
    }

    public void removeRequest(String currentUID, String partnerUID){
        fRepository.removePartnerRequest(currentUID, partnerUID);
    }


    /////////////////////////////////
    ////        Messages       /////
    ////////////////////////////////

    public void saveMessage(String uid1, User otherUser, Message msg){
        fRepository.saveMessage(uid1, otherUser, msg);
    }

    public LiveData<List<Message>> getMessages(String uid1, String uid2){
        return fRepository.getMessages(uid1, uid2);
    }

    public LiveData<List<Conversation>> getConversations(String uid){
        return fRepository.getConversations(uid);
    }

    public void updateUnreadConversation(String uid1, String uid2, Boolean isUnread){
        fRepository.updateUnreadConversation(uid1, uid2, isUnread);
    }


}
