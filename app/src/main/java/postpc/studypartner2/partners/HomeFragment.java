package postpc.studypartner2.partners;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.Search.results.ResultRecyclerUtils;
import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;
import postpc.studypartner2.R;

import static android.view.View.GONE;


public class HomeFragment extends Fragment {

    private UserViewModel viewModel;

    private static final String TAG = "HomeFragment";
    private FloatingActionButton floatingActionButton;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    private TextView emptyPartnerListMessage;
//    private PartnerRecyclerUtils.PartnersAdapter adapter = new PartnerRecyclerUtils.PartnersAdapter();
    private ResultRecyclerUtils.ResultsAdapter adapter;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.progressBarPartners);
        emptyPartnerListMessage = view.findViewById(R.id.home_empty_partners_text);
        emptyPartnerListMessage.setVisibility(GONE);

        redirectUser();

        // Set up UI
        adapter = new ResultRecyclerUtils.ResultsAdapter(getContext());
        setUpRecyclerView(view);
        setUpFAB(view);

        if (MainActivity.getCurrentUserID() != null) {
            // logged in
            loadPartners();
        }

        return view;
    }

    private void redirectUser() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            boolean isNewUser = bundle.getBoolean("isNewUser", false);
            if (isNewUser) {
                // new user
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_homeFragment_to_profileFragment);
            }
        }
    }

    private void setUpFAB(View view){
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_homeFragment_to_searchFragment);

            }
        });
    }

    private void loadPartners(){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.getPartners(MainActivity.getCurrentUserID())
                .observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        Log.d(TAG, "onChanged: updated query ");
//                        adapter.setPartners(users);
                        adapter.setResults(users);
                        updateUI();
                    }
                });
    }

    private void updateUI() {

        progressBar.setVisibility(GONE);
        if (adapter.getItemCount() < 1 ){
            emptyPartnerListMessage.setVisibility(View.VISIBLE);
        } else {
            emptyPartnerListMessage.setVisibility(GONE);
        }
    }

    private void setUpRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.partnerRecyclerView);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }


}
