package postpc.studypartner2.chat;

import com.google.firebase.firestore.GeoPoint;

public class MyLocation {

    private double latitude;
    private double longitude;

    public MyLocation() {
    }

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
