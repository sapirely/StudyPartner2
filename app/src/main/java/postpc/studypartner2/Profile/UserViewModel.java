package postpc.studypartner2.Profile;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import postpc.studypartner2.Utils.Log;


public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";

    private UserRepository mRepository;
    private FirestoreRepository fRepository;
    private LiveData<List<User>> mAllUsers;
    private boolean isUserInRoom;

    public UserViewModel(Application application) {
        super(application);
        mRepository = new UserRepository(application);
        fRepository = new FirestoreRepository();
        mAllUsers = mRepository.getAllUsers();
    }

    LiveData<List<User>> getAllUsers() {
        return mAllUsers; }

    public LiveData<Boolean> isUserRegistered(String uid){
        android.util.Log.d(TAG, "isUserRegistered: ");
        return fRepository.isUserRegistered(uid);
    }

    public LiveData<User> getUser(String uid)  {
        Log.d(TAG, "getUser: ");
        return mRepository.getUser(uid);
    }

    public LiveData<User> loadUser(String uid){
        return fRepository.loadUser(uid);
    }

    public void addUser(User user){
        // Add to netowrk db (Firestore)
        fRepository.addUser(user);

        // Add to local db (Room)
        mRepository.insert(user);
    }

//    private void insertUserToRoom(User user) { mRepository.insert(user); }

}
