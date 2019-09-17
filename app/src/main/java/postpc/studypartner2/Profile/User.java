package postpc.studypartner2.Profile;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
// To make the class meaningful to a Room database, you need to annotate it.
// Annotations identify how each part of this class relates to an entry in the database.
// Room uses this information to generate code.

/**
 * A class that represents an app user.
 */

@Entity(tableName = "users")
public class User implements Parcelable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "uid")
    private String uid;

    private String name;
    private String description;
    private String image_url;
    private boolean loaded;

    public User(){}

    @Ignore
    public User(@NonNull String uid, String name, String description, String image_url)
    {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.image_url = image_url;
        this.loaded = false;
    }



    protected User(Parcel in) {
        uid = in.readString();
        name = in.readString();
        description = in.readString();
        image_url = in.readString();
        loaded = in.readByte() != 0;
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
}
