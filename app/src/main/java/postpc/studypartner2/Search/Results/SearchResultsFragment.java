package postpc.studypartner2.Search.Results;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import postpc.studypartner2.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements ResultRecyclerUtils.ResultClickCallBack {

    private static final String TAG = "SearchResultsFragment";

    private final String EMPTY_MSG_TOAST = "Empty Message";


    private TextView nameTextView;
    private TextView distanceTextView;
    private TextView clickMeTextView;
    private ImageView profileImageView;
    private RecyclerView mRecyclerView;

    private ResultRecyclerUtils.ResultsAdapter adapter = new ResultRecyclerUtils.ResultsAdapter();
    public ArrayList<Result> results = new ArrayList<>();

    public SearchResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        // set views
        this.nameTextView = view.findViewById(R.id.tv_partner_name);
        this.distanceTextView = view.findViewById(R.id.tv_distance);
        this.profileImageView = view.findViewById(R.id.iv_result_img);
        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.searchRecyclerView);
        this.clickMeTextView = view.findViewById(R.id.tv_click_me);

        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter.callBack = this;

        clickMeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch(view.getId()) {
                    case R.id.tv_click_me:
                        // send and add to database
                        Result m = sendResult("A");
                        break;
                }
            }
        });

        adapter.submitList(results);

        // log message list size
        Log.d(TAG, "onCreate: current size of results list: "+results.size());

        return view;
    }

    private Result sendResult(String msgContent) {
        // add the message to the list of messages
        ArrayList<Result> resultssCopy = new ArrayList<>(results);
        results = resultssCopy;
        Result new_result = new Result(msgContent);
        results.add(new_result);
        adapter.submitList(results);

        return new_result;
    }

    @Override
    public void onResultLongClick(Result Result) {

    }
}
