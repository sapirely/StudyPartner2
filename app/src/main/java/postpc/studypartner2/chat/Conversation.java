package postpc.studypartner2.chat;

import java.util.ArrayList;

import postpc.studypartner2.profile.User;

public class Conversation {

    private String uid1;
    private String uid2;
    private User otherUser;
    private ArrayList<Message> messages;
    private String lastMsg;

    public Conversation(){

    }

    public Conversation(String uid1, String uid2, User partner, ArrayList<Message> messages) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.otherUser = partner;
        this.messages = messages;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage(){
        return messages.get(messages.size()-1);
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }
}
