package postpc.studypartner2.ui.fragments.inbox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
//import postpc.studypartner2.partners.PartnerRecyclerUtils;
//import postpc.studypartner2.model.User;
import postpc.studypartner2.model.Conversation;
import postpc.studypartner2.model.User;
import postpc.studypartner2.repository.UserViewModel;
import postpc.studypartner2.ui.ConversationRecyclerUtils;
import postpc.studypartner2.utils.HelperFunctions;
import postpc.studypartner2.utils.Log;

import static android.view.View.GONE;
import static postpc.studypartner2.utils.HelperFunctions.determineOtherUserUIDFromConversation;


public class ConversationsFragment extends Fragment implements ConversationRecyclerUtils.ConversationClickCallBack {


    private static final String TAG = "ConversationsFragment";

    public UserViewModel viewModel;

    private View currentView;

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private ConversationRecyclerUtils.ConversationsAdapter adapter = new ConversationRecyclerUtils.ConversationsAdapter(getContext());


    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        currentView = view;
        progressBar = view.findViewById(R.id.progressBarConversations);
        // Set up UI
        setUpRecyclerView(view);

        loadConversations();

        adapter.setOnItemClickListener(new ConversationRecyclerUtils.ConversationsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle = new Bundle();
                Conversation conversation = adapter.getConversation(position);
                User otherUser = getOtherUserFromConversation(conversation);

                if (conversation.getUnread() != null ) {
                    if (conversation.getUnread() && !conversation.getLastMsg().getSenderUID().equals(MainActivity.getCurrentUserID())) {
                        conversation.setUnread(false);
                        viewModel.updateUnreadConversation(conversation.getUid1(), conversation.getUid2(), false);
                    }
                }
                //////////


                bundle.putString("otherChatUserUID", otherUser.getUid());
                bundle.putParcelable("otherChatUser", otherUser);
                Log.d(TAG, "onItemClick position: " + position);
                Navigation.findNavController((AppCompatActivity) getContext(), R.id.nav_host_fragment)
//                        .navigate(R.id.action_conversationsFragment2_to_chatFragment, bundle);
                .navigate(R.id.action_inboxHolderFragment_to_chatFragment, bundle);
            }
        });

        return view;
    }

    public User getOtherUserFromConversation(Conversation conversation){
        String uid = determineOtherUserUIDFromConversation(conversation);
        return conversation.getUsers().get(uid);
    }



    private void loadConversations(){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        try {
            viewModel.getConversations(MainActivity.getCurrentUserID())
                    .observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
                        @Override
                        public void onChanged(List<Conversation> c) {
                            Log.d(TAG, "onChanged: updated query ");
                            adapter.setConversations(c);
                            updateUI();
                        }
                    });
        } catch (Exception e){
            Log.d(TAG, "loadConversations: no conversations yet");
        }
    }

    private void updateUI() {
        progressBar.setVisibility(GONE);
    }
//
//
//    private static void getUserFromConversation(List<Conversation> conversations, DocumentReference doc){
//        HashMap<String, User[]> convoToUsersDict = new HashMap<>();
//
//        for (Conversation c:conversations){
//
//        }
//        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                User user = task.getResult().toObject(User.class);
//            }
//        });
//    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.conversationsRecyclerView);

        mRecyclerView.setAdapter(adapter);
        adapter.callBack = this;


        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }

    @Override
    public void onConversationClick(Conversation conversation, View view) {
        if (view.getId() == R.id.conv_avatar){

            User user = getOtherUserFromConversation(conversation);
            HelperFunctions.showPopup(getContext(), getView(), user);
        }

    }

//    public void showPopup(User user) {
//        // set up pop up
//        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_profile, null);
//        TextView profileName = popupView.findViewById(R.id.profile_name);
//        TextView profileDesc = popupView.findViewById(R.id.profile_desc);
//        ImageView profilePic = popupView.findViewById(R.id.profile_image);
//        TextView loc = popupView.findViewById(R.id.profile_popup_location);
//
//        loadImage(user.getImage_url(), profilePic);
//        profileName.setText(user.getName());
//        profileDesc.setText(user.getDescription());
//        if (!user.getPrettyLocation(getContext()).isEmpty()) {
//            loc.setText(user.getPrettyLocation(getContext()));
//        }
//
//        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        boolean focusable = true; // lets taps outside the popup also dismiss it
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//        // show the popup window
//        popupWindow.showAtLocation(currentView, Gravity.CENTER, 0, 0);
//
////        // dismiss the popup window when touched
////        popupView.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                popupWindow.dismiss();
////                return true;
////            }
////        });
//    }

//    private void loadImage(String image_uri, ImageView imageView) {
//        Glide.with(this)
//                .load(image_uri)
//                .placeholder(R.drawable.default_avatar)
//                .apply(RequestOptions.circleCropTransform())
//                .into(imageView);
//    }
}
