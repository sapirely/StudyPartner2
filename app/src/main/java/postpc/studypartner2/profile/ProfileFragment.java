package postpc.studypartner2.profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import postpc.studypartner2.utils.Log;
import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;

import static android.app.Activity.RESULT_OK;

// todo: edit profile

public class ProfileFragment extends Fragment implements CourseRecyclerUtils.CourseClickCallBack {

    private static final String TAG = "ProfileFragment";

    private final int PROFILE_IMG_REQUEST_CODE = 9239;

    private UserViewModel viewModel;
    private RecyclerView mRecyclerView;
    private User currentUser;

    private TextView profileName;
    private TextView profileDesc;
    private ImageView profilePic;
    private EditText editProfileName;
    private EditText editProfileDesc;
    private ImageButton addCourseBtn;
    private ImageView imageView;
    private TextView[] studyTimes = new TextView[3];
    private TextView[] environments = new TextView[2];

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
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.courses_recycler_view);
        profileName = view.findViewById(R.id.profile_name);
        profileDesc = view.findViewById(R.id.profile_desc);
        profilePic = view.findViewById(R.id.profile_image);
        editProfileName = view.findViewById(R.id.edit_profile_name);
        editProfileDesc = view.findViewById(R.id.edit_profile_desc);
        addCourseBtn = view.findViewById(R.id.btn_add_course);
        imageView = (ImageView) view.findViewById(R.id.profile_image);
        studyTimes[0] = view.findViewById(R.id.profile_time_0);
        studyTimes[1] = view.findViewById(R.id.profile_time_1);
        studyTimes[2] = view.findViewById(R.id.profile_time_2);

        environments[0] = view.findViewById(R.id.profile_env_0);
        environments[1] = view.findViewById(R.id.profile_env_1);

        loadUser(view);

        return view;
    }

    @Override
    public void onCourseLongClick(Course course) {
        if (adapter.getItemCount() > 1){
            createDeleteCourseDialog(course);
        } else {
            Toast.makeText(this.getContext(), "At least one course is required", Toast.LENGTH_LONG).show();
        }

//        viewModel.removeRequest(MainActivity.getCurrentUserID(), user.getUid());
//        adapter.removeAt(position);
//        Toast.makeText(this.getContext(), "Removed "+user.getName(), Toast.LENGTH_LONG).show();
//        android.util.Log.d(TAG, "onRequestClick: removing partner request of "+user.getUid());
//        break;

    }

    private void createDeleteCourseDialog(final Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to remove this course?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove course
                        android.util.Log.d(TAG, "onClick: user clicked on remove course");
                        removeCourse(MainActivity.getCurrentUserID(), course);
                        adapter.removeCourse(course);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        android.util.Log.d(TAG, "onClick: remove course dialog was cancelled");
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeCourse(final String uid, final Course course){
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.removeCourse(uid, course);
    }

    private void loadUser(final View view) {
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.loadUser(MainActivity.getCurrentUserID()).observe(getViewLifecycleOwner(), new Observer<User>() {

            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: observed user change");
                try {
                    Log.d(TAG, "onChanged: setting up ui ");
                    updateUI(view, user);
                } catch (Exception e) {
                    // todo handle exception
                    Log.e(TAG, "onChanged: Error observing user. ", e);
                }
            }
        });
    }


    private void setUpRecyclerView(int coursesListLength) {
        final double NUM_OF_COLS = 4.0; // num of courses in a line
        int numOfLines = 1; // initial number of lines

        if (coursesListLength > 0) {
            // calc how many lines are needed
            numOfLines = (int) Math.ceil(coursesListLength / NUM_OF_COLS);
        }
        adapter.callBack = this;
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(numOfLines,
                StaggeredGridLayoutManager.HORIZONTAL));
    }

    private void updateUI(View view, User user) throws Exception {
        if (user == null) {
            throw new Exception("Error getting user, can't update UI.");
        }
        Log.d(TAG, "updateUI: updating for user " + user.getUid());
        setUpProfileImage(view, user.getImage_url());
        profileName.setText(user.getName());
        profileDesc.setText(user.getDescription());
        setUpRecyclerView(user.getCourses().size());
        adapter.setCourses(user.getCoursesList_courseType());

        //todo more stuff
    }

    private void setUpProfileImage(View currentView, String image_uri) {

        loadImage(image_uri);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickProfileImage();
            }
        });
    }

    private void loadImage(String image_uri) {
        Glide.with(this)
                .load(image_uri)
                .placeholder(R.drawable.default_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    private void pickProfileImage() {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, PROFILE_IMG_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Loads selected image from gallery
        if (requestCode == PROFILE_IMG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String filePath = data.getData().getPath(); // only one of them is neede todo
                    android.util.Log.d(TAG, "onActivityResult: filepath is " + filePath);
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                    viewModel.uploadProfileImageToStorage(MainActivity.getCurrentUserID(), uri).observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String storageUri) {
                            loadImage(storageUri);
                        }
                    });
                }
            }
        }
    }


//    // utils - maybe remove
//    public class CourseDeletingDialog extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage("Are you sure you want to remove this course?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // remove course
//                            android.util.Log.d(TAG, "onClick: user clicked on remove course");
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                            android.util.Log.d(TAG, "onClick: remove course dialog was cancelled");
//                        }
//                    });
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//    }
}
