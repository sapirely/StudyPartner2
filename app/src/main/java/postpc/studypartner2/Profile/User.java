package postpc.studypartner2.Profile;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
// To make the class meaningful to a Room database, you need to annotate it.
// Annotations identify how each part of this class relates to an entry in the database.
// Room uses this information to generate code.

/**
 * A class that represents an app user.
 */

@Entity(tableName = "users")
public class User implements Parcelable {

    private static final String TAG = "User";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "uid")
    private String uid;

    private String name;
    private String description;
    private String image_url;
    private boolean loaded;
    private List<String> coursesList;

    public User(){}

    @Ignore
    public User(@NonNull String uid, String name, String description, String image_url, String coursesList_string)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.loaded = false;
        this.coursesList = coursesListFromStringConverter(coursesList_string);
    }

    public User(@NonNull String uid, String name, String description, String image_url, List<String> coursesList)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.loaded = false;
        this.coursesList = coursesList;
    }

    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
        loaded = in.readByte() != 0;
        // maybe need to add coursesList todo
    }

    private List<String> coursesListFromStringConverter(String coursesList_string){
        List<String> coursesList;
        try {
            coursesList = Arrays.asList(coursesList_string.split("\\s*,\\s*"));
        } catch (Exception e){
            Log.d(TAG, "coursesListConverter: empty courses list in user initiation");
            coursesList = Arrays.asList("");
        }
        return coursesList;
    }

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(image_url);
        parcel.writeByte((byte) (loaded ? 1 : 0));
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /* Getters and Setters */

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setCoursesList(List<String> coursesList){
        this.coursesList = coursesList;
    }

    public void addCourseToList(String courseNum){
        this.coursesList.add(courseNum);
    }

    public List<String> getCoursesList(){
        return this.coursesList; 
    }

    public class CoursesListConverter {
        @TypeConverter
        public List<String> storedStringToCoursesList(String value) {
            List<String> coursesList = Arrays.asList(value.split("\\s*,\\s*"));
            return coursesList;
        }
    
        @TypeConverter
        public String coursesListToStoredString(List<String> cl) {
            String value = "";
    
            for (String course :cl)
                value += course + ",";
    
            return value;
        }
    }

}
