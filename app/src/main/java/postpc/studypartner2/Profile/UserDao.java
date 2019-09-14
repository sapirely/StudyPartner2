package postpc.studypartner2.Profile;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("SELECT * FROM users where uid = :user_id LIMIT 1")
    User loadUser(String user_id);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * from users ORDER BY uid ASC")
    LiveData<List<User>> getAllUsers();

//    @Query("SELECT * FROM user")
//    List<User> loadAll();
//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    List<User> loadAllByUserId(int... userIds);
//    @Query("SELECT * FROM user where name LIKE :first AND last_name LIKE :last LIMIT 1")
//    User loadOneByNameAndLastName(String first, String last);
//    @Insert
//    void insertAll(User... users);

}
