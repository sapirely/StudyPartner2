package postpc.studypartner2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LogInActivity";



    EditText email,password;
    Button registerButton,loginButton;
//    AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        registerButton = (Button) findViewById(R.id.registerBtn);
        loginButton = (Button) findViewById(R.id.SignInBtn);


    }



//        final LifecycleOwner lifecycleOwner = this;

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: Clicked sign in");
//                viewModel.logIn(email.getText().toString(), password.getText().toString())
//                        .observe(lifecycleOwner, new Observer<Auth>() {
//                    @Override
//                    public void onChanged(Auth auth) {
//
//                    }
//                });
//
//            }
//        });

//    }

//    private void returnResultToMainActivity(){
//        Intent intentBack = new Intent();
//        setResult(RESULT_OK, intentBack);
//        finish();
//    }

}
