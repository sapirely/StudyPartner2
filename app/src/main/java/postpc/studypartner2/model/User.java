package postpc.studypartner2.model;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//import androidx.room.PrimaryKey;
//import androidx.room.TypeConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import postpc.studypartner2.utils.Log;
// To make the class meaningful to a Room database, you need to annotate it.
// Annotations identify how each part of this class relates to an entry in the database.
// Room uses this information to generate code.

/**
 * A class that represents an app user.
 */

//@Entity(tableName = "users")
public class User implements Parcelable {

    private static final String TAG = "User";

//    @NonNull
//    @PrimaryKey
//    @ColumnInfo(name = "uid")
    private String uid;

    private String name;
    private String description;
    private String image_url;
    private List<String> courses;
    private MyLocation location;
    private boolean env_quiet, env_lively;
    private boolean study_time_morning, study_time_afternoon, study_time_evening;

    public User(){}

//    @Ignore
    public User(@NonNull String uid, String name, String description, String image_url, String coursesList_string)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.courses = coursesListFromStringConverter(coursesList_string);
    }

    public User(@NonNull String uid, String name, String description, String image_url, List<String> courses)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
//        this.loaded = false;
        this.courses = courses;
    }

    public User(String uid){
        this.uid=uid;

    }


    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
//        loaded = in.readByte() != 0;
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
//        parcel.writeByte((byte) (loaded ? 1 : 0));
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
        if (courses != null && !courses.isEmpty()) {
            for (String course : courses) {
                listOfCourses.add(new Course(course));
            }
        }
        return listOfCourses;
    }


    public MyLocation getLocation(){
        return location;
    }

    public String getPrettyLocation(Context context){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String cityName = addresses.get(0).getAddressLine(0);
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
//            String countryName = addresses.get(0).getAddressLine(2);
            return cityName+", "+stateName;
        } catch (IOException e){
            android.util.Log.d(TAG, "setUpLocation: couldn't get city: ");
            e.printStackTrace();
        }
        return "";
    }

    public void setMyLocation(MyLocation location) {
        this.location = location;
    }

    public void setLocation(double lat, double lon) {
        this.location = new MyLocation(lat, lon);
    }

    public List<String> getEnvironment(){
        List<String> envs = new ArrayList<>();
        if (isEnv_lively()){
            envs.add("lively");
        }
        if (isEnv_quiet()) {
            envs.add("quiet");
        }
        return envs;
    }

    public List<String> getStudy_time(){
        List<String> list = new ArrayList<>();
        if (isStudy_time_morning()){
            list.add("morning");
        }
        if (isStudy_time_afternoon()) {
            list.add("afternoon");
        }
        if (isStudy_time_evening()) {
            list.add("evening");
        }
        return list;
    }

    public boolean isEnv_quiet() {
        return env_quiet;
    }

    public void setEnv_quiet(boolean env_quiet) {
        this.env_quiet = env_quiet;
    }

    public boolean isEnv_lively() {
        return env_lively;
    }

    public void setEnv_lively(boolean env_lively) {
        this.env_lively = env_lively;
    }

    public boolean isStudy_time_morning() {
        return study_time_morning;
    }

    public void setStudy_time_morning(boolean study_time_morning) {
        this.study_time_morning = study_time_morning;
    }

    public boolean isStudy_time_evening() {
        return study_time_evening;
    }

    public void setStudy_time_evening(boolean study_time_evening) {
        this.study_time_evening = study_time_evening;
    }

    public boolean isStudy_time_afternoon() {
        return study_time_afternoon;
    }

    public void setStudy_time_afternoon(boolean study_time_afternoon) {
        this.study_time_afternoon = study_time_afternoon;
    }

//    public class CoursesListConverter {
////        @TypeConverter
//        public List<String> storedStringToCoursesList(String value) {
//            List<String> coursesList = Arrays.asList(value.split("\\s*,\\s*"));
//            return coursesList;
//        }
//
////        @TypeConverter
//        public String coursesListToStoredString(List<String> cl) {
//            String value = "";
//
//            for (String course :cl)
//                value += course + ",";
//
//            return value;
//        }
//    }

}
