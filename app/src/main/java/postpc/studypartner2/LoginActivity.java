package postpc.studypartner2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import postpc.studypartner2.utils.Log;

import static postpc.studypartner2.utils.HelperFunctions.SRC_GOOGLE;
import static postpc.studypartner2.utils.HelperFunctions.SRC_KEY;
import static postpc.studypartner2.utils.HelperFunctions.SRC_LOGIN_EXISTING;
import static postpc.studypartner2.utils.HelperFunctions.SRC_LOGIN_NEW;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1843;

    private FirebaseAuth mAuth;

    private EditText loginEmailEditText;
    private EditText passwordEditText;
    private Button loginBtn;
    private ImageButton googleBtn;
    private Button registerBtn;

    private ProgressBar progressBar;

    private enum LogInState {IN_PROGRESS, FAILED}

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set up views
        loginEmailEditText = findViewById(R.id.login_email_edit_text);
        passwordEditText = findViewById(R.id.login_email_password);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.login_register_btn);
        googleBtn = findViewById(R.id.login_google_btn);
        progressBar = findViewById(R.id.loginProgressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        bundle = null;
        if (currentUser != null){
            Intent notificationIntent = getIntent();
            if (notificationIntent != null){
                bundle = notificationIntent.getExtras();
            }
            goToMainActivity(SRC_LOGIN_EXISTING, bundle);
        }

        // set up UI
        progressBar.setVisibility(View.INVISIBLE);
        setUpSignInButtons();
        setUpRegisterButton();
    }

    private void goToMainActivity(String source, Bundle bundle){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(SRC_KEY, source);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            Log.d(TAG, "goToMainActivity: authenticated, moving to MainActivity");
            startActivity(intent);
        } else {
            Log.d(TAG, "goToMainActivity: auth is null, authentication went wrong");
            updateUI(LogInState.FAILED);
        }
    }

    private void showEmptyWarning(String field){
        Toast.makeText(this, "Please enter "+field, Toast.LENGTH_LONG).show();
    }

    private boolean requireNonEmptyFields(String email, String password){
        if (email.isEmpty()) {
            showEmptyWarning("email");
            return false;
        }
        if (password.isEmpty()) {
            showEmptyWarning("password");
            return false;
        }
        return true;
    }

    private void setUpSignInButtons(){
        // If sign in button clicked
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (requireNonEmptyFields(email, password)) {
                    Log.d(TAG, "onClick: attempting to sign in with email and password...");
//                    progressBar.setVisibility(View.VISIBLE);
                    updateUI(LogInState.IN_PROGRESS);
                    signInWithEmail(email, password);
                }
            }
        });

        // If google button clicked
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to sign in with google...");
                progressBar.setVisibility(View.VISIBLE);
                signInWithGoogle();
            }
        });
    }

    private void setUpRegisterButton(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (requireNonEmptyFields(email, password)) {
                    Log.d(TAG, "onClick: attempting to register with email and password...");
//                    progressBar.setVisibility(View.VISIBLE);
                    updateUI(LogInState.IN_PROGRESS);
                    registerWithEmail(email, password);
                }
            }
        });
    }

    private void registerWithEmail(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMainActivity(SRC_LOGIN_NEW, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure: "+ task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(LogInState.FAILED);
                        }
                    }
                });
    }

    private void signInWithEmail(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMainActivity(SRC_LOGIN_EXISTING, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure: "+task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(LogInState.FAILED);
                        }
                    }
                });
    }

    private void goToGoogleSignIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithGoogle(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                // docs say to replace this ^^^^^ but other sources say json replaces it
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        goToGoogleSignIn(mGoogleSignInClient);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                // Sign in succeeded, proceed with account
                GoogleSignInAccount acct = task.getResult();
                Log.d(TAG, "onActivityResult: signed in with google");
                if (acct != null) {
                    firebaseAuthWithGoogle(acct);
                } else {
                    Log.d(TAG, "onActivityResult: acct is null, sign in with google failed");
                    updateUI(LogInState.FAILED);
                }
            } else {
                Log.d(TAG, "onActivityResult: sign in with google failed");
                Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                updateUI(LogInState.FAILED);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMainActivity(SRC_GOOGLE, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure "+task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_LONG).show();
                            updateUI(LogInState.FAILED);
                        }
                    }
                });
    }

    private void updateUI(LogInState state){
        switch (state){
            case IN_PROGRESS:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case FAILED:
                progressBar.setVisibility(View.GONE);
                break;
        }
    }


}
