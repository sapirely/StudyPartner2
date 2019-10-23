package postpc.studypartner2.partners;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a document in the collection "partners" in the DB.
 */
public class PartnerList {

    private static final String TAG = "PartnerList";

    private String uid;
    private List<DocumentReference> approved;
    private List<DocumentReference> requests;

    public PartnerList(){
    }

    public PartnerList(String uid) {

        this.uid = uid;
        approved = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public PartnerList(String uid, List<DocumentReference> approved) {
        this.uid = uid;
        this.approved = approved;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<DocumentReference> getApproved() {
        return approved;
    }

    public void setApproved(List<DocumentReference> approved) {
        this.approved = approved;
    }

    public List<DocumentReference> getRequests() {
        return requests;
    }

    public void setRequests(List<DocumentReference> requests) {
        this.requests = requests;
    }

}
