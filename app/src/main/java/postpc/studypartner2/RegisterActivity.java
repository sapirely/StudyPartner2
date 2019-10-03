package postpc.studypartner2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.annotation.Nullable;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    EditText editTextDescription;
    EditText editTextCourseNum;
    Button buttonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextDescription = findViewById(R.id.editTextRegisterDescription);
        editTextCourseNum = findViewById(R.id.editTextRegisterCourseNum);
        buttonDone = findViewById(R.id.buttonRegisterDone);

        Log.d(TAG, "onCreate: ");
        Intent intentCreatedMe = getIntent();
        // get anything from the intent

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch(view.getId()) {
                    case R.id.buttonRegisterDone:
                        returnResultToMain();
                        break;
                }
            }
        });

    }

    private void returnResultToMain(){
        Intent intentBack = new Intent();
        intentBack.putExtra("Description", editTextDescription.getText().toString());
        intentBack.putExtra("CourseNum", editTextCourseNum.getText().toString());
        setResult(RESULT_OK, intentBack);
        finish();
    }
}
