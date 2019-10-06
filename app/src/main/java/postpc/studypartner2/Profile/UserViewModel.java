package postpc.studypartner2.Profile;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


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


//    private void insertUserToRoom(User user) { mRepository.insert(user); }

}
