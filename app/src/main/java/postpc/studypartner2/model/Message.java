package postpc.studypartner2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/***
 * Represents a message in a conversation.
 */
public class Message implements Parcelable {

    private static final String TAG = "Message";

    private static int currentId = 0;
    private static ArrayList<Message> all = new ArrayList<>();

    private Random rand = new Random();
    private String mID = rand.nextInt(1000)+"";
    private String senderUID;
    private long sentTime;
    private String messageText;
    private String prettyTime;

    public Message(){

    }

    public Message(String senderUID, String messageText) {
        this.senderUID = senderUID;
        this.sentTime = new Date().getTime();
        this.messageText = messageText;
    }

    public Message(Parcel in) {
        senderUID = in.readString();
        sentTime = in.readLong();
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
        parcel.writeString(senderUID);
        parcel.writeLong(sentTime);
        parcel.writeString(messageText);
    }


    /*---- getters and setters ----*/

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

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getPrettySentTime() {

//        return new Date(sentTime).getTime().toString();
        return String.format("%tR", new Date(sentTime));
    }

    public void setPrettySentTime(String prettyTime){
        this.prettyTime = prettyTime;
    }
}
