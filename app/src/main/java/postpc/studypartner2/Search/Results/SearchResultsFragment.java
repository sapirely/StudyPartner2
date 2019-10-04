package postpc.studypartner2.Search.Results;


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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.Profile.User;
import postpc.studypartner2.Profile.UserViewModel;
import postpc.studypartner2.R;
import postpc.studypartner2.Search.Results.ResultRecyclerUtils.ResultsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements ResultRecyclerUtils.ResultClickCallBack {

    private static final String TAG = "SearchResultsFragment";

    private final String EMPTY_MSG_TOAST = "Empty Message";

    private RecyclerView mRecyclerView;
    UserViewModel viewModel;

//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference colRef = db.collection("users");
    private ResultsAdapter adapter = new ResultsAdapter();

    public ArrayList<User> results = new ArrayList<>();

    public SearchResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        // set views
//        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.searchRecyclerView);
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        setUpRecyclerView(view);
        // log message list size
        Log.d(TAG, "onCreate: current size of results list: "+results.size());

        return view;
    }

    private void setUpRecyclerView(View view){

        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.searchRecyclerView);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));

        EditText courseNum = view.findViewById(R.id.edit_text_search);

//        viewModel.getUsersByCourse(courseNum.getText().toString()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
//        viewModel.getLastUsersQuery().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
        viewModel.getLastQuery().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
//                adapter.setResults(users);
                adapter.submitList(users);
            }

        });

//        Query query = colRef.orderBy("name", Query.Direction.ASCENDING);



//        FirestoreRecyclerOptions<Result> options = new FirestoreRecyclerOptions.Builder<Result>().setSnapshotArray(viewModel.getLastQuery())
//        Log.d(TAG, "setUpRecyclerView: setting up query");
//        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
//                .setQuery(query, User.class)
//                .build();

//        adapter = new ResultRecyclerUtils.ResultsAdapter(options);

//        Log.d(TAG, "setUpRecyclerView: setting up UI");
//        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.searchRecyclerView);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(
//                view.getContext(),
//                LinearLayoutManager.VERTICAL,
//                false));
//        mRecyclerView.setAdapter(adapter);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.startListening();
//    }



    //    private Result sendResult(String msgContent) {
//        // add the message to the list of messages
//        ArrayList<Result> resultssCopy = new ArrayList<>(results);
//        results = resultssCopy;
//        Result new_result = new Result(msgContent);
//        results.add(new_result);
//        adapter.submitList(results);
//
//        return new_result;
//    }

    @Override
    public void onResultLongClick(User Result) {

    }
}
