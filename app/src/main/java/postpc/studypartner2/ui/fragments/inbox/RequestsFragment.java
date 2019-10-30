package postpc.studypartner2.ui.fragments.inbox;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
import postpc.studypartner2.model.User;
import postpc.studypartner2.repository.UserViewModel;
import postpc.studypartner2.ui.RequestRecyclerUtils;
import postpc.studypartner2.utils.Log;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment implements RequestRecyclerUtils.RequestClickCallBack {

    private static final String TAG = "RequestsFragment";

    private RecyclerView mRecyclerView;
    private RequestRecyclerUtils.RequestsAdapter adapter;
    private UserViewModel viewModel;
    private ProgressBar progressBar;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requests, container, false);
        progressBar = view.findViewById(R.id.progressBarRequests);

        // Set up UI
        adapter = new RequestRecyclerUtils.RequestsAdapter(getContext());
        setUpRecyclerView(view);

        adapter.callBack = this;

        if (MainActivity.getCurrentUserID() != null) {
            // logged in
            viewModel = new ViewModelProvider(this).get(UserViewModel.class);
            setCurrentUserForLocation();
            loadRequests();
        }

        return view;
    }


    private void loadRequests(){

        viewModel.getRequests(MainActivity.getCurrentUserID())
                .observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        Log.d(TAG, "onChanged: updated query ");
                        adapter.setRequests(users);
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        progressBar.setVisibility(GONE);
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.requestsRecyclerView);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }

    private void setCurrentUserForLocation() {

        viewModel.loadUser(MainActivity.getCurrentUserID()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                adapter.setCurrentUser(user);
            }
        });
    }

    @Override
    public void onRequestClick(View view, int position) {
        User user = adapter.getItemByPosition(position);
        switch (view.getId()){
            case R.id.request_partner_approve_btn:
                viewModel.addPartner(MainActivity.getCurrentUserID(), user.getUid());
                adapter.removeAt(position);
                Toast.makeText(this.getContext(), "Added "+user.getName(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onRequestClick: approving partner request of "+user.getUid());
                break;
            case R.id.request_partner_cancel_btn:
                viewModel.removeRequest(MainActivity.getCurrentUserID(), user.getUid());
                adapter.removeAt(position);
                Toast.makeText(this.getContext(), "Removed "+user.getName(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onRequestClick: removing partner request of "+user.getUid());
                break;
        }
    }
}
