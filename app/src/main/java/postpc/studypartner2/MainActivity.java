package postpc.studypartner2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import postpc.studypartner2.chat.MyLocation;
import postpc.studypartner2.notifications.Token;
import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;

import static postpc.studypartner2.utils.HelperFunctions.SP_UID;
import static postpc.studypartner2.utils.HelperFunctions.SP_USER;
import static postpc.studypartner2.utils.HelperFunctions.SRC_GOOGLE;
import static postpc.studypartner2.utils.HelperFunctions.SRC_KEY;
import static postpc.studypartner2.utils.HelperFunctions.SRC_LOGIN_EXISTING;
import static postpc.studypartner2.utils.HelperFunctions.SRC_LOGIN_NEW;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";
    private final String[] PERMISSIONS =
            {Manifest.permission.ACCESS_FINE_LOCATION};
    private final int PERMISSION_REQUEST_CODE = 101;
    private final int RC_SIGN_IN = 123;
    private final int RC_REGISTER = 124;

    // Firebase Authentication
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context mContext = MainActivity.this;
    private Intent authIntent;
    private FirebaseUser curAuthUser;

    private UserViewModel viewModel;
    private static String current_user_uid;
    private User current_logged_in_user;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request permission
        if (!checkPermissions(PERMISSIONS)){
           requestRemainingPermissions(PERMISSIONS);
        }

        mAuth = FirebaseAuth.getInstance();
        current_user_uid = mAuth.getUid();

        // determine source and do stuff
        actOnIntent();

        // set up navigation
        setUpNavigation();
    }


    private void actOnIntent(){
        Intent intent = getIntent();
        if (intent != null){
            String source = intent.getStringExtra(SRC_KEY);

            // from notifications
            if (source.isEmpty()) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    // from notification
                    String isRequest = bundle.getString("isRequest");
                    Log.d(TAG, "onCreate: isRequest: " + isRequest);
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.action_homeFragment_to_inboxHolderFragment, bundle);
                }
            } else { // login/register/google sign-in
                actAccordingToSource(source);
            }
        }
    }

    private void actAccordingToSource(final String source){
        switch (source){
            case SRC_LOGIN_NEW:
                break;
            case SRC_LOGIN_EXISTING:
                break;
            case SRC_GOOGLE:
                break;
        }
    }


    /***Location related **/
    private void saveLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "onSuccess: got location "+location.toString());
//                            GeoPoint geo = HelperFunctions.locationToGeoPoint(location);
                            MyLocation geo = new MyLocation(location.getLatitude(), location.getLongitude());
                            viewModel.updateUser(current_user_uid, "location", geo);
                        } else {
                            Log.d(TAG, "onSuccess: location is null");
                        }
                    }
                });
    }

    /***/


    /***
     * Notification related
     */

    public void updateTokenHelper(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(getCurrentUserID()).setValue(mToken);
    }

    public void updateToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                updateTokenHelper(newToken);
            }
        });
    }

    /****************************************/


    /**
     * SP Related
     */

    private void saveUIDToSP(String uid){
        SharedPreferences sp = getSharedPreferences(SP_UID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID", uid);
        editor.commit();
    }

    private void saveCurrentUserToSP(User user){
        SharedPreferences sp = getSharedPreferences(SP_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("Current_USER", json);
        editor.apply();
    }

    /****************************************/


    /** Permission related ***/

    private boolean checkPermissions(String[] permissions){
        Log.d(TAG, "checkPermissions: checking permissions");
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                Log.d(TAG, "checkPermissions: permission not granted for "+permission);
                return false;
        }
        Log.d(TAG, "checkPermissions: permissions granted");
        return true;
    }

    private void requestRemainingPermissions(String[] permissions) {
        List<String> remainingPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            Log.d(TAG, "requestRemainingPermissions: requesting permission for "+permission);
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        if (!remainingPermissions.isEmpty()) {
            requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            for(int i=0; i<grantResults.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        new AlertDialog.Builder(this)
                                .setMessage("The app needs Internet and MyLocation permissions to work correctly.")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        requestRemainingPermissions(PERMISSIONS);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }
                    return;
                }
            }
            //all is good, continue flow
        }
    }

    private void authenticateUser(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Customize layout
        AuthMethodPickerLayout loginLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.google_login_btn)
                .setEmailButtonId(R.id.emailLoginBtn)
                .build();

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAuthMethodPickerLayout(loginLayout)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }


    /***
     * navigation related
     *
     */
    private void setUpNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nvaigation_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // handle backstack
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return NavigationUI.onNavDestinationSelected(menuItem, Navigation.findNavController(getParent(), R.id.nav_host_fragment));
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    /*********************************/


    public static String getCurrentUserID(){
        return current_user_uid;
    }


}
