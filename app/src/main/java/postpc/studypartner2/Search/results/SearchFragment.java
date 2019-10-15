package postpc.studypartner2.Search.results;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.profile.User;
import postpc.studypartner2.profile.UserViewModel;
import postpc.studypartner2.R;


public class SearchFragment extends Fragment implements ResultRecyclerUtils.ResultClickCallBack{

    private static final String TAG = "SearchFragment";
    private EditText courseNum;
    private ImageButton searchBtn;
    private LinearLayout searchFiltersLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    private UserViewModel viewModel;
    private ResultRecyclerUtils.ResultsAdapter adapter;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        adapter = new ResultRecyclerUtils.ResultsAdapter(getContext());
        courseNum = view.findViewById(R.id.edit_text_search_course);
        searchBtn = view.findViewById(R.id.button_search);
        searchFiltersLayout = view.findViewById(R.id.search_filters_layout);
        mRecyclerView = view.findViewById(R.id.searchRecyclerView);
        progressBar = view.findViewById(R.id.progressBarSearch);

        // Set up results
        setUpRecyclerView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch(view.getId()) {
                    case R.id.button_search:
                        progressBar.setVisibility(View.VISIBLE);
                        loadResults();
                        updateUI();
                        break;
                }
            }
        });

    }

    private void loadResults(){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.getUsersByCourse(courseNum.getText().toString())
                .observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        Log.d(TAG, "onChanged: updated query ");
                        adapter.setResults(users);
                        setCurrentUserForLocation();
                    }
                });

    }

    private void setCurrentUserForLocation() {

        viewModel.loadUser(MainActivity.getCurrentUserID()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                adapter.setCurrentUser(user);
            }
        });
    }

    private void updateUI(){
        // Hide search filters and show results
        searchFiltersLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setUpRecyclerView(View view) {

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }

    @Override
    public void onResultLongClick(User user) {

    }
}
