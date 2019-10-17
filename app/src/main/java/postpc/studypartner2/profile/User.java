package postpc.studypartner2.profile;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import postpc.studypartner2.chat.MyLocation;
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
    private List<String> courses;
    private MyLocation location;
//    private GeoPoint myLocation;
//    private HashMap<String, Double> myLocation;

    public User(){}

    @Ignore
    public User(@NonNull String uid, String name, String description, String image_url, String coursesList_string)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.loaded = false;
        this.courses = coursesListFromStringConverter(coursesList_string);
    }

    public User(@NonNull String uid, String name, String description, String image_url, List<String> courses)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.loaded = false;
        this.courses = courses;
    }


    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
        loaded = in.readByte() != 0;
        // maybe need to add courses todo
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

    public void setCourses(List<String> courses){
        this.courses = courses;
    }

    public void addCourseToList(String courseNum){
        this.courses.add(courseNum);
    }

    public List<String> getCourses(){
        return this.courses;
    }

    public List<Course> getCoursesList_courseType(){
        List<Course> listOfCourses = new ArrayList<>();
        for (String course: courses){
            listOfCourses.add(new Course(course, ""));
        }
        return listOfCourses;
    }

//    public GeoPoint getMyLocation() {
//        return myLocation;
//    }

    public MyLocation getLocation(){
        return location;
    }

//    public void setMyLocation(GeoPoint myLocation) {
//        this.myLocation = myLocation;
//    }

    public void setMyLocation(MyLocation location) {
        this.location = location;
    }

    public void setLocation(double lat, double lon) {
        this.location = new MyLocation(lat, lon);
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
