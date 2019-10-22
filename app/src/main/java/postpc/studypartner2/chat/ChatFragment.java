package postpc.studypartner2.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
import postpc.studypartner2.notifications.APIService;
import postpc.studypartner2.notifications.Client;
import postpc.studypartner2.notifications.Data;
import postpc.studypartner2.notifications.Response;
import postpc.studypartner2.notifications.Sender;
import postpc.studypartner2.notifications.Token;
import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;
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
    private ImageView addMeetingBtn;
    private ImageView backArrow;
    private ImageView chatAvatar;
    private TextView otherUserName;

    private User otherUser;
    private User user;

    // notifications
    final private String FCM_API = "https://fcm.googleapis.com/";
    private APIService apiService;
    private boolean notify = false;

    private MessageRecyclerUtils.MessagesAdapter adapter = new MessageRecyclerUtils.MessagesAdapter();
    public List<Message> currentMessages = new ArrayList<>();

    private UserViewModel viewModel;


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
        this.addMeetingBtn = view.findViewById(R.id.chat_meeting);
        this.backArrow = view.findViewById(R.id.chat_back_arrow);

        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        // get current user
//        SharedPreferences sp = getActivity().getSharedPreferences(SP_USER, MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sp.getString("Current_USER", "");
////        final User user = gson.fromJson(json, User.class);
//        user = gson.fromJson(json, User.class);
        user = getCurrentUser();

        // read other user from bundle
//        Bundle bundle = this.getArguments();
//        otherUser = new User();
//        if (bundle != null) {
//            String otherUserUID = bundle.getString("otherChatUserUID", "");
//            Log.d(TAG, "onCreateView: got uid "+otherUserUID+" from bundle");
//            otherUser = bundle.getParcelable("otherChatUser");
//            Log.d(TAG, "onCreateView: got parcelable user "+otherUser.getName());
//        } else {
//            Log.d(TAG, "onCreateView: Got to chat without user info");
//        }
        otherUser = getOtherUser();

        // check if otherUser is a partner
//        viewModel.getPartnersUIDS(user.getUid()).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
//            @Override
//            public void onChanged(List<String> uids) {
//                // otherUser is partner of user
//                setUpAddFriend(uids.contains(otherUser.getUid()));
//            }
//        });
        setUpPartnerFeatures(user.getUid(), otherUser.getUid());

        // set up top bar
        this.otherUserName = view.findViewById(R.id.chat_other_user_name);
        this.chatAvatar = view.findViewById(R.id.chat_avatar);
        if (otherUser.getUid() != null) {
            this.otherUserName.setText(otherUser.getName());
            Glide.with(this)
                    .load(otherUser.getImage_url())
                    .placeholder(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(chatAvatar);
        }

        setUpRecyclerView(view);

        // create API service - notifications
        apiService = Client.getRetrofit(FCM_API).create(APIService.class);

        setUpSendBtn();

        // load messages
        loadMessages();

        adapter.submitList(currentMessages);
        this.editText.setText("");

        setUpAddFriendBtn();
        setUpBackArrow();
        setAddMeetingBtn();
//
        // log message list size
        Log.d(TAG, "onCreate: current_size_of_msg_list: "+currentMessages.size());

        return view;
    }

    private void setUpPartnerFeatures(final String userUID, final String otherUserUID) {
        viewModel.getPartnersUIDS(userUID).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> uids) {
                // otherUser is partner of user
                Boolean isPartner = uids.contains(otherUserUID);
                setUpAddFriend(isPartner);
                setUpMeeting(isPartner);
            }
        });
    }

    private void setUpMeeting(boolean isPartner) {
        if (isPartner){
            addMeetingBtn.setVisibility(View.VISIBLE);
        } else {
            addMeetingBtn.setVisibility(View.GONE);
        }
    }

    private void setAddMeetingBtn(){
        addMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpEvent(otherUser.getName());
            }
        });
    }

    private void setUpEvent(String partnerName){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, "StudyWith "+partnerName);
//        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//                startDateMillis);
//        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
//                endDateMillis);
//        intent.putExtra(CalendarContract.Events.DESCRIPTION,strDescription));
        startActivity(intent);
    }

    private User getOtherUser() {
        Bundle bundle = this.getArguments();
        User user = new User();
        if (bundle != null) {
            String otherUserUID = bundle.getString("otherChatUserUID", "");
            Log.d(TAG, "onCreateView: got uid "+otherUserUID+" from bundle");
            user = bundle.getParcelable("otherChatUser");
            Log.d(TAG, "onCreateView: got parcelable user "+user.getName());
        } else {
            Log.d(TAG, "onCreateView: Got to chat without user info");
        }
        return user;
    }

    private User getCurrentUser(){
        SharedPreferences sp = getActivity().getSharedPreferences(SP_USER, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("Current_USER", "");
        return gson.fromJson(json, User.class);
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView.setAdapter(adapter);
        adapter.setCurrentUID(MainActivity.getCurrentUserID());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter.callBack = this;
    }

    private void loadMessages() {

        viewModel.getMessages(getCurrentUserUID(), otherUser.getUid()).observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                currentMessages = messages;
                adapter.submitList(currentMessages);
            }
        });
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
                        .navigate(R.id.action_chatFragment_to_inboxHolderFragment);
            }
        });
    }

    private void setUpAddFriendBtn(){
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.chat_add_friend:
                        viewModel.sendPartnerRequest(user.getUid(), otherUser.getUid());
                        sendMessageNotification(otherUser.getUid(), user.getName(), "", true);
                        Toast.makeText
                                (view.getContext(), "Sent partner request", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setUpAddFriend(Boolean isPartner){
        if (isPartner){
            addFriendBtn.setVisibility(View.GONE);
        } else {
            addFriendBtn.setVisibility(View.VISIBLE);
        }
    }

    private Message sendMessage(final String msgContent) {
        // add the message to the list of messages
        ArrayList<Message> messagesCopy = new ArrayList<>(currentMessages);
        currentMessages = messagesCopy;

        // add to list
        Message new_message = new Message(getCurrentUserUID(), msgContent);
        currentMessages.add(new_message);

        // add to db

        viewModel.saveMessage(getCurrentUserUID(), otherUser, new_message);

        // update recycler view
        adapter.submitList(currentMessages);
        editText.setText("");

//        SharedPreferences sp = getActivity().getSharedPreferences(SP_USER, MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = sp.getString("Current_USER", "");
//        final User user = gson.fromJson(json, User.class);

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(getCurrentUserUID());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (notify){
                    String otherUserUID = otherUser.getUid();

                    sendMessageNotification(otherUserUID, user.getName(), msgContent, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return new_message;
    }

    private void sendMessageNotification(final String otherUserUID, final String nameOfUser, final String msgContent, final boolean isRequest) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(otherUserUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    String notificationBody = generateNotificationBody(nameOfUser, msgContent, isRequest);

                    Data data = new Data(otherUserUID, notificationBody, null, otherUserUID, R.drawable.ic_chat, isRequest);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Log.d(TAG, "onResponse: "+response.message());
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            private String generateNotificationBody(String nameOfUser, String msgContent, boolean isRequest) {
                if (isRequest){
                    return nameOfUser + " sent you a partner request.";
                } else {
                    return nameOfUser+": "+msgContent;
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
