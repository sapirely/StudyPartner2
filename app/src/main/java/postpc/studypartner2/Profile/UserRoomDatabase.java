//package postpc.studypartner2.Profile;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import androidx.annotation.NonNull;
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.sqlite.db.SupportSQLiteDatabase;
//
//import java.util.Calendar;
//import java.util.List;
//
//import postpc.studypartner2.Utils.Log;
//
//@Database(entities = {User.class}, version = 1, exportSchema = false)
//public abstract class UserRoomDatabase extends RoomDatabase {
//
//    private static final String TAG = "UserRoomDatabase";
//
//    public abstract UserDao userDao();
//
//    private static volatile UserRoomDatabase INSTANCE;
//
//    /***
//     * Returns an instance of the UserRoomDatabase. (Singleton)
//     */
//    public static UserRoomDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            Log.d(TAG, "getDatabase: Initiating room DB");
//            synchronized (UserRoomDatabase.class) {
//                if (INSTANCE == null) {
//                    // Create database here
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            UserRoomDatabase.class, "user_database")
////                            .addCallback(sRoomDatabaseCallback) // added later todo is needed
////                            .allowMainThreadQueries() // todo delete
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//
//    /***
//     * What happens when the UserRoomDatabase instance is initiated.
//     */
//    private static RoomDatabase.Callback sRoomDatabaseCallback =
//            new RoomDatabase.Callback(){
//
//                @Override
//                public void onOpen (@NonNull SupportSQLiteDatabase db){
//                    super.onOpen(db);
//                    Log.d(TAG, "onOpen: RoomDatabase.Callback");
//
//                    new PopulateDbAsync(INSTANCE).execute();
//                }
//            };
//
//
//    /***
//     * Adds a pre-configured user to the room db.
//     */
//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final UserDao mDao;
//
//        PopulateDbAsync(UserRoomDatabase db) {
//            mDao = db.userDao();
//        }
//
//        @Override
//        protected Void doInBackground(final Void... params) {
//            Log.d(TAG, "doInBackground: populating room db ");
//            java.util.Date currentTime = Calendar.getInstance().getTime();
//            User user = new User("1", "Aa", currentTime.toString(), "", "67566");
//            // todo chagne this ^
//            mDao.insertUser(user);
//            return null;
//        }
//    }
//
//}
