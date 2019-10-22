package postpc.studypartner2.Search.results;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.profile.ProfileFragment;
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

    private TextView emptyListTextViewMsg;

    private TextView[] studyTimesTextViews = new TextView[3];
    private TextView[] environmentsTextViews = new TextView[2];

    private UserViewModel viewModel;
    private ResultRecyclerUtils.ResultsAdapter adapter;


    private List<String> user_study_times = new ArrayList<>();
    private List<String> user_environments = new ArrayList<>();


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

        emptyListTextViewMsg = view.findViewById(R.id.search_empty_results_text);

        studyTimesTextViews[0] = view.findViewById(R.id.profile_time_0);
        studyTimesTextViews[1] = view.findViewById(R.id.profile_time_1);
        studyTimesTextViews[2] = view.findViewById(R.id.profile_time_2);

        environmentsTextViews[0] = view.findViewById(R.id.profile_env_0);
        environmentsTextViews[1] = view.findViewById(R.id.profile_env_1);

        // Set up search filters
        setUpSelectables(ProfileFragment.SelectableType.TIME, studyTimesTextViews, user_study_times);
        setUpSelectables(ProfileFragment.SelectableType.ENV, environmentsTextViews, user_environments);

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
                        String t = courseNum.getText().toString();
                        if (t.isEmpty()){
                            Toast.makeText(getContext(), "Please enter course name", Toast.LENGTH_LONG).show();
                        } else {
                            searchFiltersLayout.setVisibility(View.GONE);
                            loadResults(t, user_study_times, user_environments);
                        }
//                        updateUI();
                        break;
                }
            }
        });

    }

    private void loadResults(String courseName, List<String> studyTimes, List<String> environments){
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
//        a.add("morning");
//        b.add("quiet");
//        viewModel.getUsersByCourseOnly(courseNum.getText().toString())
        viewModel.getUsersQuery(courseName, studyTimes, environments)
                .observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        Log.d(TAG, "onChanged: updated query ");
                        adapter.setResults(users);
                        setCurrentUserForLocation();
                        updateUI();
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

        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (adapter.getItemCount() < 1){
            emptyListTextViewMsg.setVisibility(View.VISIBLE);
        }
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


    private boolean isTextViewSelectedAlready(final List<String> list, TextView tv){
        android.util.Log.d(TAG, "isTextViewSelectedAlready: "+list.contains(tv.getText().toString().toLowerCase()));
        return list.contains(tv.getText().toString().toLowerCase());
    }


    private void updateSelectableTextView(TextView textView, boolean selected){
        if (selected){
            textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.accent_filled_rounded_rectangle));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.accent_stroke_rounded_rectangle));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    private void unselectObject(ProfileFragment.SelectableType type, TextView view, String view_text){
        // UI
        updateSelectableTextView(view, false);

        List<String> list;
        String key;
        if (type == ProfileFragment.SelectableType.ENV){
            user_environments.remove(view_text);
        } else if (type == ProfileFragment.SelectableType.TIME) {
            user_study_times.remove(view_text);
        } else {
            throw new IllegalArgumentException("Illegal type of selectable list");
        }
    }

    private void selectObject(ProfileFragment.SelectableType type, TextView view, String view_text){
        // UI
        updateSelectableTextView(view, true);

        List<String> list;
        String key;
        if (type == ProfileFragment.SelectableType.ENV){
            user_environments.add(view_text);
        } else if (type == ProfileFragment.SelectableType.TIME) {
            user_study_times.add(view_text);
        } else {
            throw new IllegalArgumentException("Illegal type of selectable list");
        }
    }


    private void setUpSelectables(final ProfileFragment.SelectableType type, final TextView[] textsViews, final List<String> user_list){
        for (TextView e : textsViews) {
            e.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.util.Log.d(TAG, "onClick: clicked "+((TextView)view).getText());
                    final TextView tv = (TextView) view;
                    final String tv_text = tv.getText().toString().toLowerCase();

                    // selected again -> unselect
                    if (isTextViewSelectedAlready(user_list, tv)){
                        unselectObject(type, tv, tv_text);
                    } else {
                        // newly selected
                        selectObject(type, tv, tv_text);
                    }

                }
            });
        }

    }


}
