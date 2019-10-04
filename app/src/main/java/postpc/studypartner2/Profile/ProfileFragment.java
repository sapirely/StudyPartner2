package postpc.studypartner2.Profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import postpc.studypartner2.Utils.Log;
import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;

// todo: edit profile

public class ProfileFragment extends Fragment implements CourseRecyclerUtils.CourseClickCallBack {

    private static final String TAG = "ProfileFragment";

    private UserViewModel viewModel;
    private RecyclerView mRecyclerView;

    private TextView profileName;
    private TextView profileDesc;
    private ImageView profilePic;
    private EditText editProfileName;
    private EditText editProfileDesc;
    private ImageButton addCourseBtn;

    private CourseRecyclerUtils.CoursesAdapter adapter = new CourseRecyclerUtils.CoursesAdapter();
    public ArrayList<Course> courses = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Set up views
        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.courses_recycler_view);
        profileName = view.findViewById(R.id.profile_name);
        profileDesc = view.findViewById(R.id.profile_desc);
        profilePic = view.findViewById(R.id.profile_image);
        editProfileName = view.findViewById(R.id.edit_profile_name);
        editProfileDesc = view.findViewById(R.id.edit_profile_desc);
        addCourseBtn = view.findViewById(R.id.btn_add_course);

        loadUser(view);

        return view;
    }

    @Override
    public void onCourseLongClick(Course course) {

    }

    private void loadUser(final View view){
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.loadUser(MainActivity.getCurrentUserID()).observe(getViewLifecycleOwner(), new Observer<User>(){

            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: observed user change");
                try {
                    Log.d(TAG, "onChanged: setting up ui ");
                    updateUI(view, user);
                } catch (Exception e){
                    // todo handle exception
                    Log.e(TAG, "onChanged: Error observing user. ", e);
                }
            }
        });
    }


    private void setUpRecyclerView() {
        final int NUM_OF_COLS = 2;
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(NUM_OF_COLS,
                StaggeredGridLayoutManager.HORIZONTAL));
    }

    private void updateUI(View view, User user) throws Exception {
        if (user == null){
            throw new Exception("Error getting user, can't update UI.");
        }
        Log.d(TAG, "updateUI: updating for user "+user.getUid());
        setUpProfileImage(view, user.getImage_url());
        profileName.setText(user.getName());
        profileDesc.setText(user.getDescription());
        setUpRecyclerView();
        adapter.setCourses(user.getCoursesList_courseType());

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
