package postpc.studypartner2.Partners;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.Profile.User;
import postpc.studypartner2.Profile.UserViewModel;
import postpc.studypartner2.R;


public class HomeFragment extends Fragment {

    private UserViewModel viewModel;

    private static final String TAG = "HomeFragment";
    private FloatingActionButton floatingActionButton;
    private RecyclerView mRecyclerView;
    private PartnerRecyclerUtils.PartnersAdapter adapter = new PartnerRecyclerUtils.PartnersAdapter();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up UI
        setUpRecyclerView(view);
        setUpFAB(view);

        if (MainActivity.getCurrentUserID() != null) {
            // logged in
            loadPartners();
        }

        return view;
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
        // todo CHANGE TO LOAD ACTUAL PARTNERS
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
//        viewModel.getUsersByCourse("67521")
//        viewModel.getPartners(MainActivity.getCurrentUserID());
//        adapter.setPartners(partners);
        viewModel.getPartners(MainActivity.getCurrentUserID())
                .observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        Log.d(TAG, "onChanged: updated query ");
                        adapter.setPartners(users);
                    }
                });
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
