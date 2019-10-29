package postpc.studypartner2.utils;

import postpc.studypartner2.BuildConfig;

public class Log {

    private static final String PREFIX = "*****************";

    /***
     * Overrides standard debug log function so it can be turned off furing release mode.
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg){

        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, PREFIX + " " + msg);
        }
    }

    public static void e(String tag, String msg, Exception e){
        String s = PREFIX+" "+msg;
        android.util.Log.d(tag, s, e);
    }
}
