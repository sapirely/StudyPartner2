package postpc.studypartner2.calendar;

import postpc.studypartner2.BuildConfig;

public class TimeSlot {
    private String startingTime;
    private String endingTime;
    private boolean selected;

    public TimeSlot(String startingTime, String endingTime){
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.selected = false;
    }

    public TimeSlot(int startingTime, int endingTime){
        if (BuildConfig.DEBUG && !(startingTime < 24 && startingTime >= 0) && (endingTime < 24 && endingTime >= 0)){
            throw new AssertionError("illegal times for TimeSlot");
        }
        this.startingTime = formatTime(startingTime);
        this.endingTime = formatTime(endingTime);
    }

    private String formatTime(int time){
        String sTime = time+":00";
        if (time < 10){
            sTime = "0"+sTime;
        }
        return sTime;
    }

    public String getAll(){
        return startingTime+" - "+endingTime;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
