package postpc.studypartner2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.chat.MyLocation;
import postpc.studypartner2.notifications.Token;
import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;

import static android.view.View.GONE;
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

    private ConstraintLayout splashScreen;

    // Firebase Authentication
    private FirebaseAuth mAuth;

    private UserViewModel viewModel;
    private static String current_user_uid;

    // Navigation
    BottomNavigationView bottomNavigationView;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashScreen = findViewById(R.id.splash_screen);

        // request permission
        if (!checkPermissions(PERMISSIONS)){
           requestRemainingPermissions(PERMISSIONS);
        }

        mAuth = FirebaseAuth.getInstance();
        current_user_uid = mAuth.getUid();
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // for notifications
        updateToken();
        saveUIDToSP(current_user_uid);

        // determine source of intent and do stuff accordingly
        actOnIntent();
    }



    private void actOnIntent(){
        Intent intent = getIntent();
        if (intent != null) {
            String source = intent.getStringExtra(SRC_KEY);


//            if (source == null || source.isEmpty()) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
//                    saveLocation();

                // handle intent
                String isRequest = bundle.getString("isRequest");
                if (isRequest != null) {
                    // intent is from notifications
                    Log.d(TAG, "onCreate: isRequest: " + isRequest);
//                    Navigation.findNavController(this, R.id.nav_host_fragment)
//                            .navigate(R.id.action_homeFragment_to_inboxHolderFragment, bundle);
                    goToInboxAndRequests(bundle);
                }
            }

            if (source != null) {
                actAccordingToSource(source);
            } else {
                Log.d(TAG, "actOnIntent: got to main activity with no source");
            }
        } else {
            // shouldn't get here
            Log.d(TAG, "actOnIntent: got to main activity with no intent");
        }
    }

    private void goToInboxAndRequests(Bundle bundle) {
        Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_homeFragment_to_inboxHolderFragment, bundle);
    }



    // called from register user and existing user (google calls both of them)
    private void continueAfterDB(){
        saveLocation();
        splashScreen.setVisibility(GONE);
        setUpNavigation();
    }

    private void actAccordingToSource(final String source){
        switch (source){
            case SRC_LOGIN_NEW:
                registerNewUser();
                break;
            case SRC_LOGIN_EXISTING:
                existingUser();
                break;
            case SRC_GOOGLE:
                determineIfNewGoogleUser();
                break;
        }
    }

    private void existingUser(){
        viewModel.loadUser(current_user_uid).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User loadedUser) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", loadedUser);
                saveCurrentUserToSP(loadedUser);
                continueAfterDB();
            }
        });

    }

    private void registerNewUser(){
        FirebaseUser authUser = mAuth.getCurrentUser();
        if (authUser != null){
//            Intent intent = new Intent(this, RegisterActivity.class);
//            startActivity(intent);
            User user = new User(authUser.getUid());
            viewModel.addUser(user);
            continueAfterDB();
            Log.d(TAG, "registerNewUser: added new user to db");
        } else {
            Log.d(TAG, "registerNewUser: auth user is null");
        }
    }

    private void determineIfNewGoogleUser(){
        FirebaseUser authUser = mAuth.getCurrentUser();
        if (authUser != null){
            viewModel.loadUser(authUser.getUid()).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user.getName() == null || user.getName().isEmpty()){
                        // new user
                        registerNewUser();
                    } else {
                        // existing user
                        existingUser();
                    }
                }
            });

        } else {
            Log.d(TAG, "registerNewUser: auth user is null");
        }
    }


    /***Location related **/
    private void saveLocation(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "onSuccess: got location "+location.toString());
                            MyLocation geo = new MyLocation(location.getLatitude(), location.getLongitude());
                            viewModel.updateUser(current_user_uid, "location", geo);
                        } else {
                            Log.d(TAG, "onSuccess: location is null");
                        }
                    }
                });
    }

    /**/


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

    /**
     * SP Related
     */

    private void saveUIDToSP(String uid){
        SharedPreferences sp = getSharedPreferences(SP_UID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID", uid);
        editor.apply();
    }

    private void saveCurrentUserToSP(User user){
        SharedPreferences sp = getSharedPreferences(SP_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("Current_USER", json);
        editor.apply();
    }



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
        List<String> remainingPermissions = new ArrayList<>();
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

    public static void signOut(Activity currentActivity){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(intent);
    }

}
