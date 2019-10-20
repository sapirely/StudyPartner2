package postpc.studypartner2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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
import postpc.studypartner2.utils.HelperFunctions;

import static postpc.studypartner2.utils.HelperFunctions.SP_UID;
import static postpc.studypartner2.utils.HelperFunctions.SP_USER;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";
    private final String[] PERMISSIONS =
            {Manifest.permission.ACCESS_FINE_LOCATION};
    private final int PERMISSION_REQUEST_CODE = 101;

    private final int RESULT_NOT_REGISTERED = 777;
    public final static int LOGIN_REQUEST = 888;
    public final static int REGISTER_REQUEST = 789;
    public final static String KEY_AUTH = "key_auth";
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
        if (!checkPermissions(PERMISSIONS)){
           requestRemainingPermissions(PERMISSIONS);
        }

        initApp();

    }

    private void actOnIntent(){
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String isRequest = bundle.getString("isRequest");
                Log.d(TAG, "onCreate: isRequest: " + isRequest);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_homeFragment_to_inboxHolderFragment, bundle);
            }
        }
    }

    private void initApp(){
        current_logged_in_user = new User();

        // Auth set up
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Log.d(TAG, "onCreate: didn't get user from auth ");
            authenticateUser();
        } else {
            Log.d(TAG, "onCreate: got user "+currentUser.getUid());
            current_user_uid = currentUser.getUid();
//            viewModel.setLoggedInUID(currentUser.getUid());
        }
        // Navigation set up
        setUpNavigation();
        actOnIntent();
    }

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

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser authCurrentUser = mAuth.getCurrentUser();
        if (authCurrentUser != null){
            Log.d(TAG, "onStart: user "+authCurrentUser.getUid()+" is logged in");

            // update the user id
            current_user_uid = authCurrentUser.getUid();

            updateToken();
            saveUIDToSP(authCurrentUser.getUid());

            loadUser(authCurrentUser);
            saveLocation();

        } else {
            Log.d(TAG, "onStart: no user is logged in");
        }
    }

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

    public User loadUserFromSP(){
        SharedPreferences sp = getSharedPreferences(SP_USER, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("Current_USER", "");
        User user = gson.fromJson(json, User.class);
        return user;
    }

    private void loadUser(final FirebaseUser authCurrentUser){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.loadUser(MainActivity.getCurrentUserID()).observe(this, new Observer<User>(){

            @Override
            public void onChanged(User loadedUser) {
                postpc.studypartner2.utils.Log.d(TAG, "onChanged: observed user change");
                try {
                    if (loadedUser.getUid() == "-1"){
                        Log.d(TAG, "onChanged: user doesn't exist in db");
                        // go to edit profile screen
                        firstLogIn(viewModel, authCurrentUser);
                    } else {
                        Log.d(TAG, "onChanged: got user, continue in home fragment");
                        // send user data to home
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", loadedUser);
                        saveCurrentUserToSP(loadedUser);
                    }
                } catch (Exception e){
                    // todo handle exception
                    postpc.studypartner2.utils.Log.e(TAG, "onChanged: Error observing user. ", e);
                }
            }
        });
    }

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

    private void firstLogIn(UserViewModel viewModel, FirebaseUser authCurrentUser){
        Log.d(TAG, "firstLogIn: user: "+authCurrentUser.getUid());
        // register
        Intent registerIntent = new Intent(this, RegisterActivity.class);
//        startActivityForResult(registerIntent, RC_REGISTER);
//        User newUser = new User(getCurrentUserID(), authCurrentUser.getDisplayName(), "", "", "");
        current_logged_in_user.setUid(getCurrentUserID());
        current_logged_in_user.setName(authCurrentUser.getDisplayName());
        current_logged_in_user.setImage_url("");
        viewModel.addUser(current_logged_in_user);
        saveCurrentUserToSP(current_logged_in_user);

        // bundle user info
        Bundle bundle = new Bundle();
//        bundle.putParcelable("user", newUser);
        bundle.putParcelable("user", current_logged_in_user);

        // navigate to edit profile
//        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_profileFragment, bundle);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_profileFragment);
        Toast.makeText(this, "please edit your profile", Toast.LENGTH_LONG).show();
    }

    private void authenticateUser(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Customize layout
        AuthMethodPickerLayout loginLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.googleLoginBtn)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response;

        switch (requestCode){
            // Fire auth sign in
            case RC_SIGN_IN:
                response = IdpResponse.fromResultIntent(data);
                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    Log.d(TAG, "onActivityResult: Sign in succeeded");
                    current_user_uid = response.getIdpToken();
                    if (response == null) {
                        // the user canceled the sign-in flow using the back button
                        Log.d(TAG, "onActivityResult: User canceled sign in");
                    } else {
                        // handle error - todo
                        Exception e = response.getError();
                        Log.w(TAG, "onActivityResult: error: ", e);
                    }
                }
                break;
            case RC_REGISTER:
                response = IdpResponse.fromResultIntent(data);
                if (resultCode == RESULT_OK) {
                    // Successfully Registered
                    current_logged_in_user.setDescription(data.getStringExtra("Description"));
                    current_logged_in_user.addCourseToList(data.getStringExtra("CourseNum"));

                    Log.d(TAG, "onActivityResult: Registration succeeded");
                    if (response == null) {
                        // the user canceled the sign-in flow using the back button
                        Log.d(TAG, "onActivityResult: User canceled registration");
                    } else {
                        // handle error - todo
                        Exception e = response.getError();
                        Log.w(TAG, "onActivityResult: error: ", e);
                    }
                }
                break;

        }
    }

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


    public static String getCurrentUserID(){
        // todo : is this ok? MainActivity is supposed to be always up so maybe?
        return current_user_uid;
    }

//    public void navigateToChat(){
//        Navigation.findNavController(this, R.id.nav_host_fragment)
//                .navigate(R.id.action_homeFragment_to_chatFragment);
//    }

}
