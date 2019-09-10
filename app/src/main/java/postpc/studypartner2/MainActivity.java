package postpc.studypartner2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import postpc.studypartner2.Chat.Message;
import postpc.studypartner2.Chat.MessageRecyclerUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

//    private final String EMPTY_MSG_TOAST = "Empty Message";
    private final String EDIT_TEXT_KEY = "edit_text_state";
    private final String KEY_RECYCLER_STATE = "recycler_state";

//    private TextView textView;
//    private EditText editText;
//    private ImageButton sendBtn;
//    private RecyclerView mRecyclerView;
//
//    private MessageRecyclerUtils.MessagesAdapter adapter = new MessageRecyclerUtils.MessagesAdapter();
//    public ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nvaigation_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


//        // set views
//        this.textView = findViewById(R.id.msgText);
//        this.sendBtn = findViewById(R.id.send_btn);
//        this.editText = findViewById(R.id.messageEditText);
//        this.mRecyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);
////
////         set RecyclerView
////        RecyclerView.LayoutManager mymanag = new LinearLayoutManager(
////                this, LinearLayoutManager.VERTICAL, false);
////        mRecyclerView.setLayoutManager(mymanag);
////        mRecyclerView.setLayoutManager(new LinearLayoutManager(
////                this, LinearLayoutManager.VERTICAL, false));
//
//        mRecyclerView.setAdapter(adapter);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(
//                this, LinearLayoutManager.VERTICAL, false));
//        adapter.callBack = this;
//
//        sendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view){
//                switch(view.getId()) {
//                    case R.id.send_btn:
//                        // check that content is valid
//                        String msgContent = editText.getText().toString();
//                        if (msgContent.isEmpty()){
//                            Toast.makeText(getApplicationContext(), EMPTY_MSG_TOAST, Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        // send and add to database
//                        Message m = sendMessage(msgContent);
//                        break;
//                }
//            }
//        });

//        adapter.submitList(messages);
//        this.editText.setText("");
//
//        // log message list size
//        Log.d(TAG, "onCreate: current_size_of_msg_list: "+messages.size());
    }


//    private Message sendMessage(String msgContent) {
//        // add the message to the list of messages
//        ArrayList<Message> messagesCopy = new ArrayList<>(messages);
//        messages = messagesCopy;
//        Message new_message = new Message(msgContent);
//        messages.add(new_message);
//        adapter.submitList(messages);
//        editText.setText("");
//
//        return new_message;
//    }
//
//    @Override
//    public void onMessageLongClick(Message message) {
//
//    }
}
