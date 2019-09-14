package postpc.studypartner2.Profile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.type.Date;

import java.util.Calendar;

@Database(entities = {User.class}, version = 1)
public abstract class UserRoomDatabase extends RoomDatabase {

    private static final String TAG = "UserRoomDatabase";

    public abstract UserDao userDao();

    private static volatile UserRoomDatabase INSTANCE;

    public static UserRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            Log.d(TAG, "getDatabase: Initiating room DB");
            synchronized (UserRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserRoomDatabase.class, "user_database")
//                            .addCallback(sRoomDatabaseCallback) // added later todo is needed
//                            .allowMainThreadQueries() // todo delete
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    Log.d(TAG, "onOpen: RoomDatabase.Callback");
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final UserDao mDao;

        PopulateDbAsync(UserRoomDatabase db) {
            mDao = db.userDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Log.d(TAG, "doInBackground: populating room db ");
            java.util.Date currentTime = Calendar.getInstance().getTime();
            User user = new User("1", "Aa", currentTime.toString(), "");
            mDao.insertUser(user);
            return null;
        }
    }

}
