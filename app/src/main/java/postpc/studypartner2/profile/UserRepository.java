//package postpc.studypartner2.Profile;
//
//import android.app.Application;
//import android.os.AsyncTask;
//
//import androidx.lifecycle.LiveData;
//
//import java.util.List;
//
//import postpc.studypartner2.Utils.Log;
//
///***
// * Handles data operations: gets user info from database or from cache.
// */
//public class UserRepository {
//
//    private static final String TAG = "UserRepository";
//
//    private UserDao mUserDao;
//    private LiveData<List<User>> mAllUsers;
////    private LiveData<User> mCurrentUser;
//
//    protected UserRepository(Application application) {
//        UserRoomDatabase db = UserRoomDatabase.getDatabase(application);
//        mUserDao = db.userDao();
//        mAllUsers = mUserDao.getAllUsers();
//    }
//
//    protected LiveData<User> getUser(String uid){
//        Log.d(TAG, "getUser: ");
//        return mUserDao.loadUser(uid);
////        new insertAsyncLoadTask(mUserDao).execute(uid);
//    }
//
//    LiveData<List<User>> getAllUsers() {
//        return mAllUsers;
//    }
//
//    /**
//     *
//     * @param user
//     */
//    public void insert(User user) {
//        //  must call this on a non-UI thread
//        Log.d(TAG, "insert: repository inserting user ");
//        new insertUserToDBAsync(mUserDao).execute(user);
//    }
//
//    /***
//     * Insert user to room database - async.
//     */
//    private static class insertUserToDBAsync extends AsyncTask<User, Void, Void> {
//
//        private final UserDao mDao;
//
//        insertUserToDBAsync(UserDao dao) {
//            mDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(final User... params) {
//            if (params == null || params[0] == null){
//                throw new NullPointerException("doInBackground: User is null");
//            }
//            Log.d(TAG, "doInBackground: inserting user "+params[0].getUid()+" to room DB.");
//            mDao.insertUser(params[0]);
//            return null;
//        }
//    }
//
//}
//
//
