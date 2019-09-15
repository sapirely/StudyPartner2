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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import postpc.studypartner2.Profile.User;
import postpc.studypartner2.Profile.UserViewModel;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";


    private final int RESULT_NOT_REGISTERED = 777;
    public final static int LOGIN_REQUEST = 888;
    public final static String KEY_AUTH = "key_auth";
    private final int RC_SIGN_IN = 123;

    // Firebase Authentication
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Context mContext = MainActivity.this;
    private Intent authIntent;
    private FirebaseUser curAuthUser;

    private static String current_user_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Auth set up
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Log.d(TAG, "onCreate: didn't get user from auth ");
            logIn();
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
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Log.d(TAG, "onStart: user "+user.getUid()+" is logged in");
            current_user_uid = user.getUid();

            // insert user todo: temp. add same as db
            User second_u = new User(current_user_uid, user.getDisplayName(), user.getEmail(), "");


            UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);
            viewModel.addUser(second_u);
        } else {
            Log.d(TAG, "onStart: no user is logged in");
        }
    }

    private void logIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

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
