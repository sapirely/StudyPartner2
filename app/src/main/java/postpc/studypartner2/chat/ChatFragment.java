package postpc.studypartner2.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
import postpc.studypartner2.utils.MySingleton;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class ChatFragment extends Fragment implements MessageRecyclerUtils.MessageClickCallBack {

    private static final String TAG = "ChatFragment";

    private final String EMPTY_MSG_TOAST = "Empty Message";


    private TextView textView;
    private EditText editText;
    private ImageButton sendBtn;
    private RecyclerView mRecyclerView;
    private ImageView addFriendBtn;

    /* Notification Related */
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA8nKFCCY:APA91bEgDiZ_VPZJB_QdzhChKXurHwnuoQl_64wmtlpzuSZS1etE1L6WrWy4-oTcxBRF-VAn-V_hUIHmLfVqIDeM1iuSeZ5NXjRcbjSVPu7Osh-VLXBlRfOPFbCfDeLZd8N8Eoy6zq7K";
    final private String contentType = "application/json";
    final String NTAG = "NOTIFICATION TAG";

    private String NOTIFICATION_TITLE;
    private String NOTIFICATION_MESSAGE;
    private String TOPIC;
    //////////////////////////

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

        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        view.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter.callBack = this;

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch(view.getId()) {
                    case R.id.send_btn:
                        // check that content is valid
                        String msgContent = editText.getText().toString();
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

        adapter.submitList(messages);
        this.editText.setText("");

        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.chat_add_friend:
                        String receiverID = "bZwbqJqrtuW3va5Mfw9bPwJI1XH2";//todo - change to getting the receiver uid
                        TOPIC = receiverID; //topic must match with what the receiver subscribed to
                        NOTIFICATION_TITLE = "Friend";
                        NOTIFICATION_MESSAGE = "friend request from brooke";

                        JSONObject notification = new JSONObject();
                        JSONObject notifcationBody = new JSONObject();
                        try {
                            notifcationBody.put("title", NOTIFICATION_TITLE);
                            notifcationBody.put("message", NOTIFICATION_MESSAGE);

                            notification.put("to", TOPIC);
                            notification.put("data", notifcationBody);
                        } catch (JSONException e) {
                            Log.e(TAG, "onCreate: " + e.getMessage() );
                        }
                        Log.d(TAG, "onClick: created notification");
                        sendNotification(view, notification);
                        break;
                }
            }
        });
//
        // log message list size
        Log.d(TAG, "onCreate: current_size_of_msg_list: "+messages.size());

        return view;
    }

    private void sendNotification(final View view, JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
//                        edtTitle.setText("");
//                        edtMessage.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view.getContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(this.getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private Message sendMessage(String msgContent) {
        // add the message to the list of messages
        ArrayList<Message> messagesCopy = new ArrayList<>(messages);
        messages = messagesCopy;
        Message new_message = new Message(msgContent);
        messages.add(new_message);
        adapter.submitList(messages);
        editText.setText("");

        return new_message;
    }

    @Override
    public void onMessageLongClick(Message message) {

    }
}
