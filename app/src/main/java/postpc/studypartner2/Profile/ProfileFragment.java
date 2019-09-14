package postpc.studypartner2.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;

import postpc.studypartner2.R;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ProfileViewModel viewModel;
    private TextView profileName;
    private TextView profileDesc;
    private ImageView profilePic;
    // todo add course + other stuff

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // init ViewModel
//        viewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel.class);
        Log.d(TAG, "onCreate: ViewModel initiated");

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // todo change room to work with livedata
        // todo: should it be in this method?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Set up views
        profileName = view.findViewById(R.id.profile_name);
        profileDesc = view.findViewById(R.id.profile_desc);
        profilePic = view.findViewById(R.id.profile_image);

        // Set up UI
        Log.d(TAG, "onCreateView: setting up ui ");
        String image_url = ""; // todo change to actual url
        setUpProfileImage(view, image_url);

        return view;
    }

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
//        Log.d(TAG, "onViewCreated: updating ui");
//        User u = viewModel.getUser(MainActivity.getCurrentUserID());

        java.util.Date currentTime = Calendar.getInstance().getTime();
        final User user = new User("1", "TEST USER", currentTime.toString(), "https://scontent.ftlv5-1.fna.fbcdn.net/v/t31.0-8/23632513_10212274441148777_3337400136456812381_o.jpg?_nc_cat=106&_nc_oc=AQnLh4xulD7mh2TaOG1KZ5sy0qFnTsRtMmKNXHzudKYuN6_mRydu9PI4-fB4SC7XUcY&_nc_ht=scontent.ftlv5-1.fna&oh=036d934e2a5af0b558428b01efb1b9f1&oe=5DF89952");
        Log.d(TAG, "onViewCreated: inserting user ");
        viewModel.insertUser(user);
        viewModel.getAllUsers().observe(this, new Observer<List<User>>(){

            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG, "onChanged: observed user change");
                try {
                    updateUI(view, users.get(0));
//                    TESTING_show_users(view, users);
                } catch (Exception e){
                    // todo handle exception
                    Log.e(TAG, "onChanged: Error observing user. ", e);
                }
            }
        });
//        try {
//            Log.d(TAG, "onViewCreated: updating UI ");
//            updateUI(view, u);
//        } catch (Exception e){
//            Log.e(TAG, "onViewCreated: user didn't make it to UI. ", e);
//        }


//        viewModel.getAllUsers().observe(requireActivity(), new Observer<List<User>>() {
//            @Override
//            public void onChanged(@Nullable List<User> u) {
//                Log.d(TAG, "onChanged: observed user change");
//                try {
////                    updateUI(view, u);
//                } catch (Exception e){
//                    // todo handle exception
//                    Log.e(TAG, "onChanged: Error observing user. ", e);
//                }
//            }
//        });


    }

    private void updateUI(View view, User user) throws Exception {
        if (user == null){
            throw new Exception("Error getting user, can't update UI.");
        }
        Log.d(TAG, "updateUI: updating for user "+user.getUid());
        setUpProfileImage(view, user.getImage_url());
        profileName.setText(user.getName());
        profileDesc.setText(user.getDescription());
        //todo more stuff
    }
//
//    private void TESTING_show_users(View view, List<User> users){
//        Log.d(TAG, "TESTING_show_users: got "+users.toString());
//        profileDesc.setText(users.get(0).getDescription());
//    }

    private void setUpProfileImage(View currentView, String image_uri){
        ImageView imageView = (ImageView) currentView.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(image_uri)
                .placeholder(R.drawable.girl)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

}
