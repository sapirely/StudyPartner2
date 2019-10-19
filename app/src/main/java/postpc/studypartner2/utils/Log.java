package postpc.studypartner2.utils;

public class Log {

    private static final String PREFIX = "*****************";

    public static void d(String TAG, String msg){
        android.util.Log.d(TAG, PREFIX+" "+msg);
    }

    public static void e(String TAG, String msg, Exception e){
        String s = PREFIX+" "+msg;
        android.util.Log.d(TAG, s, e);
    }
}
