package postpc.studypartner2.Search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.Profile.User;
import postpc.studypartner2.Profile.UserViewModel;
import postpc.studypartner2.R;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private EditText courseNum;
    private ImageButton searchBtn;

    private UserViewModel viewModel;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        courseNum = view.findViewById(R.id.edit_text_search);
        searchBtn = view.findViewById(R.id.button_search);
        return view;
        // init view model
//        final UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);
//        //load user
//        viewModel.getUsersByCourse(courseNum).observe(this, new Observer<User>(){
//
//            @Override
//            public void onChanged(User loadedUser) {
//                postpc.studypartner2.Utils.Log.d(TAG, "onChanged: observed user change");
//                try {
//                    if (loadedUser.getUid() == "-1"){
//                        Log.d(TAG, "onChanged: user doesn't exist in db");
//                        // go to edit profile screen
//                        firstLogIn(viewModel, authCurrentUser);
//                    } else {
//                        Log.d(TAG, "onChanged: got user, continue in home fragment");
//                        // send user data to home
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelable("user", loadedUser);
//                    }
//                } catch (Exception e){
//                    // todo handle exception
//                    postpc.studypartner2.Utils.Log.e(TAG, "onChanged: Error observing user. ", e);
//                }
//            }
//        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                switch(view.getId()) {
                    case R.id.button_search:
//                        viewModel.getUsersByCourse(courseNum.getText().toString()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
//                            @Override
//                            public void onChanged(List<User> users) {
//                                Log.d(TAG, "onChanged: updated query ");
//                            }
//                        });
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_searchFragment_to_searchResultsFragment);
                        break;
                }
            }
        });

    }
}
