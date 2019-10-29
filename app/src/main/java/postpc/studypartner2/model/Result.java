package postpc.studypartner2.model;

public class Result extends User {

    private String uid;

    public Result(){

    }

    public Result(String uid){
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
