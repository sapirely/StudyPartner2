package postpc.studypartner2.Profile;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.SavedStateHandle;

import java.util.List;


public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";

    private UserRepository mRepository;
    private LiveData<List<User>> mAllUsers;

    public ProfileViewModel (Application application) {
        super(application);
        mRepository = new UserRepository(application);
        mAllUsers = mRepository.getAllUsers();
    }

    LiveData<List<User>> getAllUsers() { return mAllUsers; }

    public User getUser(String uid)  {
        Log.d(TAG, "getUser: ");
        return mRepository.getUser(uid); }

    public void insertUser(User user) { mRepository.insert(user); }

//    String uid;
//    MutableLiveData<User> user = new MutableLiveData<>();


//    public void ProfileViewModel(SavedStateHandle savedStateHandle) {
//        try {
//            // Read the argument directly
//            String uid = savedStateHandle.get("uid");
//        } catch (Exception e){
//            Log.e(TAG, "ProfileViewModel: can't get uid from savedStateHandle", e);
//        }
//        try {
//            User user = savedStateHandle.get("uid");
//        } catch (Exception e){
//            Log.e(TAG, "ProfileViewModel: can't get user from savedStateHandle", e);
//        }
//
//    }


}
