package postpc.studypartner2.Profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import postpc.studypartner2.Utils.Log;
import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private UserViewModel viewModel;
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
        Log.d(TAG, "onCreate: ViewModel initiated");
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Set up views
        profileName = view.findViewById(R.id.profile_name);
        profileDesc = view.findViewById(R.id.profile_desc);
        profilePic = view.findViewById(R.id.profile_image);

        // Set up UI
        Log.d(TAG, "onCreateView: setting up ui ");
        String image_url = ""; // todo change to actual url
        setUpProfileImage(view, image_url);

        // todo change to get user by id
//        java.util.Date currentTime = Calendar.getInstance().getTime();
//        User second_u = new User("2", "second User", currentTime.toString(), "");
//        viewModel.insertUser(second_u);
        viewModel.getUser(MainActivity.getCurrentUserID()).observe(this, new Observer<User>(){

            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: observed user change");
                try {
                    updateUI(view, user);
                } catch (Exception e){
                    // todo handle exception
                    Log.e(TAG, "onChanged: Error observing user. ", e);
                }
            }
        });

        return view;
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


    private void setUpProfileImage(View currentView, String image_uri){
        ImageView imageView = (ImageView) currentView.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(image_uri)
                .placeholder(R.drawable.girl)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

}
