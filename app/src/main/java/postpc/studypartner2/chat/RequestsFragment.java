package postpc.studypartner2.chat;


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
import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private static final String TAG = "RequestsFragment";

    private RecyclerView mRecyclerView;
    private RequestRecyclerUtils.RequestsAdapter adapter;
    private UserViewModel viewModel;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requests, container, false);

        // Set up UI
        adapter = new RequestRecyclerUtils.RequestsAdapter(getContext());
        setUpRecyclerView(view);

        if (MainActivity.getCurrentUserID() != null) {
            // logged in
            loadRequests();
        }

        return view;
    }


    private void loadRequests(){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.getRequests(MainActivity.getCurrentUserID())
                .observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        Log.d(TAG, "onChanged: updated query ");
//                        adapter.setPartners(users);
                        adapter.setRequests(users);
                    }
                });
    }

    private View.OnClickListener setOncl(final String requestUID){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.request_partner_approve_btn:
                        viewModel.addPartner(MainActivity.getCurrentUserID(), requestUID);
                        break;
                }
            }
        };
        return null;
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.requestsRecyclerView);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }
}
