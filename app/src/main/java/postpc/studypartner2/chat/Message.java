package postpc.studypartner2.chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Message implements Parcelable {

    private static int currentId = 0;
    private static ArrayList<Message> all = new ArrayList<>();

    private String id;
    private long sentTime;
    private String senderName;
    private String messageText;

    public Message(String senderName, String messageText){
        this.id = ""+currentId++; // insert id (string) and update
        // todo: change this to firebase id
        this.messageText = messageText;
        this.senderName = senderName;
        this.sentTime = new Date().getTime();
        all.add(this);
    }

    public Message(String messageText){
        this.id = ""+currentId++; // insert id (string) and update
        // todo: change this to firebase id
        this.messageText = messageText;
        this.senderName = "me";
        this.sentTime = new Date().getTime();
        all.add(this);
    }

    public Message(Parcel in) {
        id = in.readString();
        sentTime = in.readLong();
        senderName = in.readString();
        messageText = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeLong(sentTime);
        parcel.writeString(senderName);
        parcel.writeString(messageText);
    }


    /*---- getters and setters ----*/

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
