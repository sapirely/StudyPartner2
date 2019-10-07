package postpc.studypartner2.Chat;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import postpc.studypartner2.R;


public class ChatFragment extends Fragment implements MessageRecyclerUtils.MessageClickCallBack {

    private static final String TAG = "ChatFragment";

    private final String EMPTY_MSG_TOAST = "Empty Message";


    private TextView textView;
    private EditText editText;
    private ImageButton sendBtn;
    private RecyclerView mRecyclerView;

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

        // log message list size
        Log.d(TAG, "onCreate: current_size_of_msg_list: "+messages.size());

        return view;
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
