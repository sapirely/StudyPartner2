package postpc.studypartner2.chat;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
import postpc.studypartner2.notifications.APIService;
import postpc.studypartner2.notifications.Client;
import postpc.studypartner2.notifications.Data;
import postpc.studypartner2.notifications.Response;
import postpc.studypartner2.notifications.Sender;
import postpc.studypartner2.notifications.Token;
import postpc.studypartner2.profile.User;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;
import static postpc.studypartner2.utils.HelperFunctions.SP_UID;
import static postpc.studypartner2.utils.HelperFunctions.SP_USER;

public class ChatFragment extends Fragment implements MessageRecyclerUtils.MessageClickCallBack {

    private static final String TAG = "ChatFragment";

    private final String EMPTY_MSG_TOAST = "Empty Message";


    private TextView textView;
    private EditText editText;
    private ImageButton sendBtn;
    private RecyclerView mRecyclerView;
    private ImageView addFriendBtn;
    private ImageView backArrow;

    // notifications
    final private String FCM_API = "https://fcm.googleapis.com/";
    private APIService apiService;
    private boolean notify = false;

    private MessageRecyclerUtils.MessagesAdapter adapter = new MessageRecyclerUtils.MessagesAdapter();
    public ArrayList<Message> messages = new ArrayList<>();


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // set views
        this.textView = view.findViewById(R.id.msgText);
        this.sendBtn = view.findViewById(R.id.send_btn);
        this.editText = view.findViewById(R.id.messageEditText);
        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.messageRecyclerView);
        this.addFriendBtn = view.findViewById(R.id.chat_add_friend);
        this.backArrow = view.findViewById(R.id.chat_back_arrow);

        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        view.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter.callBack = this;

        // create API service
        apiService = Client.getRetrofit(FCM_API).create(APIService.class);


        setUpSendBtn();

        adapter.submitList(messages);
        this.editText.setText("");

        setUpAddFriendBtn();

        setUpBackArrow();
//
        // log message list size
        Log.d(TAG, "onCreate: current_size_of_msg_list: "+messages.size());

        return view;
    }



    private void setUpSendBtn(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch(view.getId()) {
                    case R.id.send_btn:
                        // check that content is valid
                        String msgContent = editText.getText().toString();

                        // notification
                        notify = true;

                        if (msgContent.isEmpty()){
                            Toast.makeText(view.getContext(), EMPTY_MSG_TOAST, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // send and add to database
                        Message m = sendMessage(msgContent);
                        break;
                }
            }
        });
    }

    private void setUpBackArrow() {
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_chatFragment_to_conversationsFragment2);
            }
        });
    }

    private void setUpAddFriendBtn(){
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.chat_add_friend:
                        Toast.makeText
                                (view.getContext(), "Sent prtner request", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }

    private Message sendMessage(final String msgContent) {
        // add the message to the list of messages
        ArrayList<Message> messagesCopy = new ArrayList<>(messages);
        messages = messagesCopy;
        Message new_message = new Message(msgContent);
        messages.add(new_message);
        adapter.submitList(messages);
        editText.setText("");

        SharedPreferences sp = getActivity().getSharedPreferences(SP_USER, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("Current_USER", "");
        final User user = gson.fromJson(json, User.class);

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(getCurrentUserUID());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (notify){
                    String otherUserUID = "bZwbqJqrtuW3va5Mfw9bPwJI1XH2";

                    sendNotification(otherUserUID, user.getName(), msgContent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return new_message;
    }

    private void sendNotification(final String otherUserUID, final String nameOfUser, final String msgContent) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(otherUserUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(otherUserUID, nameOfUser+": "+msgContent, "New Message", otherUserUID, R.drawable.ic_chat);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onResponse: "+response.message());
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCurrentUserUID(){
        // todo remove thus from here. viewmodel can handle it
        // get current user from shared preferences
        Log.d(TAG, "getCurrentUserUID: getting uid from SP");
        SharedPreferences sp = getContext().getSharedPreferences(SP_UID, MODE_PRIVATE);
        return sp.getString("Current_USERID", "None");
    }

    @Override
    public void onMessageLongClick(Message message) {

    }
}
