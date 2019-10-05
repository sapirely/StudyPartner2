package postpc.studypartner2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import postpc.studypartner2.Profile.User;
import postpc.studypartner2.Profile.UserViewModel;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";


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

    private static String current_user_uid;
    private User current_logged_in_user;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        }

//        Log.d(TAG, "onCreate: getting room DB");
        // Database set up
//        UserRoomDatabase room = UserRoomDatabase.getDatabase(this);

        // Navigation set up
        setUpNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser authCurrentUser = mAuth.getCurrentUser();
        if (authCurrentUser != null){
            Log.d(TAG, "onStart: user "+authCurrentUser.getUid()+" is logged in");
            current_user_uid = authCurrentUser.getUid();

            // init view model
            final UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);

            //load user
            viewModel.loadUser(MainActivity.getCurrentUserID()).observe(this, new Observer<User>(){

                @Override
                public void onChanged(User loadedUser) {
                    postpc.studypartner2.Utils.Log.d(TAG, "onChanged: observed user change");
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
                            }
                    } catch (Exception e){
                        // todo handle exception
                        postpc.studypartner2.Utils.Log.e(TAG, "onChanged: Error observing user. ", e);
                    }
                }
            });

            // init location services // todo
//            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // Got last known location. In some rare situations this can be null.
//                            if (location != null) {
//                                // Logic to handle location object
//                                Log.d(TAG, "onSuccess: got location "+location.toString());
//                                viewModel.updateUser(current_user_uid, "location", location.toString());
//                            } else {
//                                Log.d(TAG, "onSuccess: location is null");
//                            }
//                        }
//                    });




        } else {
            Log.d(TAG, "onStart: no user is logged in");
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

        // bundle user info
        Bundle bundle = new Bundle();
//        bundle.putParcelable("user", newUser);
        bundle.putParcelable("user", current_logged_in_user);

        // navigate to edit profile
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_profileFragment, bundle);
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
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }


    public static String getCurrentUserID(){
        // todo : is this ok? MainActivity is supposed to be always up so maybe?
        return current_user_uid;
    }

}
