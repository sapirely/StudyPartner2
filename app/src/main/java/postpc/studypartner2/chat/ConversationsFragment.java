package postpc.studypartner2.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
//import postpc.studypartner2.partners.PartnerRecyclerUtils;
//import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;


public class ConversationsFragment extends Fragment {


    private static final String TAG = "ConversationsFragment";

    public UserViewModel viewModel;

    private RecyclerView mRecyclerView;
    private ConversationRecyclerUtils.ConversationsAdapter adapter = new ConversationRecyclerUtils.ConversationsAdapter();


    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        // Set up UI
        setUpRecyclerView(view);

        loadConversations();

        return view;
    }

    private void loadConversations(){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.getConversations(MainActivity.getCurrentUserID())
                .observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
                    @Override
                    public void onChanged(List<Conversation> c) {
                        Log.d(TAG, "onChanged: updated query ");
                        adapter.setConversations(c);
                    }
                });
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.conversationsRecyclerView);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }
}
