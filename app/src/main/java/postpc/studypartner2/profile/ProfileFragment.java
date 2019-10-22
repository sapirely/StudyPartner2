package postpc.studypartner2.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import postpc.studypartner2.chat.MyLocation;
import postpc.studypartner2.utils.Log;
import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

// todo: edit profile

public class ProfileFragment extends Fragment implements CourseRecyclerUtils.CourseClickCallBack {

    public enum SelectableType {ENV, TIME};
    private static final String TAG = "ProfileFragment";

    private final int PROFILE_IMG_REQUEST_CODE = 9239;

    private UserViewModel viewModel;
    private RecyclerView mRecyclerView;
    private User currentUser;

    private ProgressBar progressBar;

    private TextView profileName;
    private TextView profileDesc;
    private ImageView profilePic;
    private EditText editProfileName;
    private EditText editProfileDesc;
    private ImageButton addCourseBtn;
    private TextView[] studyTimesTextViews = new TextView[3];
    private TextView[] environmentsTextViews = new TextView[2];

    private TextView locationCityTextView;
    private LinearLayout locationSnippet;

    private CourseRecyclerUtils.CoursesAdapter adapter = new CourseRecyclerUtils.CoursesAdapter();
    public ArrayList<Course> courses = new ArrayList<>();

    private List<String> user_environments = new ArrayList<>();
    private List<String> user_study_times = new ArrayList<>();

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
        progressBar = view.findViewById(R.id.progressBarProfile);

        locationCityTextView = view.findViewById(R.id.location_city_text);
        locationSnippet = view.findViewById(R.id.profile_location_snippet);

        studyTimesTextViews[0] = view.findViewById(R.id.profile_time_0);
        studyTimesTextViews[1] = view.findViewById(R.id.profile_time_1);
        studyTimesTextViews[2] = view.findViewById(R.id.profile_time_2);

        environmentsTextViews[0] = view.findViewById(R.id.profile_env_0);
        environmentsTextViews[1] = view.findViewById(R.id.profile_env_1);

        loadUser(view);
        setAddCourseBtn();
//        setUpEnvironments();

        return view;
    }

    @Override
    public void onCourseLongClick(Course course) {
        if (adapter.getItemCount() > 1){
            createDeleteCourseDialog(course);
        } else {
            Toast.makeText(this.getContext(), "At least one course is required", Toast.LENGTH_LONG).show();
        }

    }

    private void unselectObject(SelectableType type, TextView view, String view_text){
        // UI
        updateSelectableTextView(view, false);

        List<String> list;
        String key;
        if (type == SelectableType.ENV){
            list = user_environments;
            key = "environment";
        } else if (type == SelectableType.TIME) {
            list = user_study_times;
            key = "study_time";
        } else {
            throw new IllegalArgumentException("Illegal type of selectable list");
        }
        list.remove(view_text);
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.updateUser(MainActivity.getCurrentUserID(), key, list);
    }

    private void selectObject(SelectableType type, TextView view, String view_text){
        // UI
        updateSelectableTextView(view, true);

        List<String> list;
        String key;
        if (type == SelectableType.ENV){
            list = user_environments;
            key = "environment";
        } else if (type == SelectableType.TIME) {
            list = user_study_times;
            key = "study_time";
        } else {
            throw new IllegalArgumentException("Illegal type of selectable list");
        }
        list.add(view_text);
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.updateUser(MainActivity.getCurrentUserID(), key, list);
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

    private boolean isTextViewSelectedAlready(final List<String> list, TextView tv){
        android.util.Log.d(TAG, "isTextViewSelectedAlready: "+list.contains(tv.getText().toString().toLowerCase()));
        return list.contains(tv.getText().toString().toLowerCase());
    }

    private void setUpSelectables(final SelectableType type, final TextView[] textsViews, final List<String> user_list){
        for (TextView e : textsViews) {
            e.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.util.Log.d(TAG, "onClick: clicked "+((TextView)view).getText());
                    final TextView tv = (TextView) view;
                    final String tv_text = tv.getText().toString().toLowerCase();

                    // selected again -> unselect
                    if (isTextViewSelectedAlready(user_list, tv)){
                        if (user_list.size() > 1) {
//                            unselectEnvironment(tv, env_text);
                            unselectObject(type, tv, tv_text);
                        } else {
                            Toast.makeText(getContext(), "At least one is required", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // newly selected
                        selectObject(type, tv, tv_text);
                    }

                }
            });
        }

    }

    private void setAddCourseBtn(){
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCourseDialog();
            }
        });
    }

    private void AddCourseDialog() {
        final EditText taskEditText = new EditText(this.getContext());
        AlertDialog dialog = new AlertDialog.Builder(this.getContext())
                .setMessage("Enter course name")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String courseName = String.valueOf(taskEditText.getText());
                        android.util.Log.d(TAG, "onClick: adding course "+courseName);
                        addCourse(MainActivity.getCurrentUserID(), new Course(courseName));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void createDeleteCourseDialog(final Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to remove this course?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // remove course
                        android.util.Log.d(TAG, "onClick: user clicked on remove course");
                        removeCourse(MainActivity.getCurrentUserID(), course);

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
        adapter.removeCourse(course);
        setUpRecyclerView(adapter.getItemCount()); // update lines
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.removeCourse(uid, course);
    }

    private void addCourse(final String uid, final Course course){
        adapter.addCourse(course);
        setUpRecyclerView(adapter.getItemCount());// update lines
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.addCourse(uid, course);
    }

    private void loadUser(final View view) {
        viewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        viewModel.loadUser(MainActivity.getCurrentUserID()).observe(getViewLifecycleOwner(), new Observer<User>() {

            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: observed user change");
                try {
                    Log.d(TAG, "onChanged: setting up ui ");
                    setUpSelectableLists(user);


                    updateUI(view, user);
                } catch (Exception e) {
                    Log.e(TAG, "onChanged: Error observing user. ", e);
                }
            }
        });
    }

    private void setUpSelectableLists(User user){
        user_environments=user.getEnvironment();
        user_study_times=user.getStudy_time();

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

    private void updateUI(View view, User user) {
        if (user == null) {
            throw new IllegalArgumentException("Error getting user, can't update UI.");
        }
        Log.d(TAG, "updateUI: updating for user " + user.getUid());
        setUpProfileImage(user.getImage_url());
        profileName.setText(user.getName());
        profileDesc.setText(user.getDescription());
        setUpRecyclerView(user.getCourses().size());
        adapter.setCourses(user.getCoursesList_courseType());

        setUpLocation(locationCityTextView, user.getLocation());

        // init selected/unselected ui state of the environments
        for (TextView env:environmentsTextViews){
            updateSelectableTextView(env, isTextViewSelectedAlready(user_environments, env));
        }
        setUpSelectables(SelectableType.ENV, environmentsTextViews, user_environments);

        for (TextView tv: studyTimesTextViews){
            updateSelectableTextView(tv, isTextViewSelectedAlready(user_study_times, tv));
        }
        setUpSelectables(SelectableType.TIME, studyTimesTextViews, user_study_times);

        progressBar.setVisibility(GONE);
    }

    private void setUpLocation(TextView locationCityTextView, MyLocation location) {
        Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String cityName = addresses.get(0).getAddressLine(0);
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
            String countryName = addresses.get(0).getAddressLine(2);
            locationCityTextView.setText(cityName+", "+stateName);
            locationSnippet.setVisibility(View.VISIBLE);
        } catch (IOException e){
            locationSnippet.setVisibility(GONE);
            android.util.Log.d(TAG, "setUpLocation: couldn't get city: ");
            e.printStackTrace();
        }
    }

    private void setUpProfileImage(String image_uri) {

        loadImage(image_uri);
        profilePic.setOnClickListener(new View.OnClickListener() {
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
                .into(profilePic);
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
