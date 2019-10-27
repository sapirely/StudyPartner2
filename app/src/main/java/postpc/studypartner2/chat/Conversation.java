package postpc.studypartner2.chat;

import java.util.ArrayList;
import java.util.HashMap;

import postpc.studypartner2.profile.User;

public class Conversation {

    private String uid1;
    private String uid2;
    private HashMap<String, User> users;
//    private User otherUser;
//    private ArrayList<Message> messages;
    private HashMap<String, Message> messages;
    private Message lastMsg;
    private Boolean unread;

    public Conversation(){

    }

    public Conversation(String uid1, String uid2, HashMap<String, Message> messages) {
        this.uid1 = uid1;
        this.uid2 = uid2;
//        this.otherUser = partner;
        this.messages = messages;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public Message getLastMessage(){
        return lastMsg;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }

    public Message getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(Message lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, User> users) {
        this.users = users;
    }

    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }

//    public User getOtherUser() {
//        return otherUser;
//    }

//    public void setOtherUser(User otherUser) {
//        this.otherUser = otherUser;
//    }
}
