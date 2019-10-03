package postpc.studypartner2.Search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.Profile.User;
import postpc.studypartner2.Profile.UserViewModel;
import postpc.studypartner2.R;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);
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
}
