package postpc.studypartner2.calendar;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.profile.UserViewModel;
import postpc.studypartner2.R;

/**
 * A simple {@link Fragment} subclass.
 */
//public class CalendarFragment extends Fragment  implements TimeSlotRecyclerUtils.TimeSlotClickCallBack {
public class CalendarFragment extends Fragment {
    private RecyclerView mRecyclerView1;
    private ProgressBar progressBar;

    private UserViewModel viewModel;
    private TimeSlotRecyclerUtils.TimeSlotAdapter adapter;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calendar, container, false);

        adapter = new TimeSlotRecyclerUtils.TimeSlotAdapter(new TimeSlotRecyclerUtils.onTimeSlotClickListener() {
            @Override public void onTimeSlotClick(TimeSlot item) {
                Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
            }
        });

        mRecyclerView1 = view.findViewById(R.id.time_table_recycler_view1);
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mRecyclerView1.setAdapter(adapter);

        mRecyclerView1.setLayoutManager(new GridLayoutManager(
                view.getContext(), 3));

        adapter.setTimeSlots(generateTimeSlots(6));

        return view;
    }

    private List<TimeSlot> generateTimeSlots(int startingHour){
        List<TimeSlot> slots = new ArrayList<>();
        for (int i=startingHour; i<23; i++){
            slots.add(new TimeSlot(i,i+1));
        }
        slots.add(new TimeSlot(23,0));
        return slots;
    }

//    @Override
//    public void onTimeSlotClick(TimeSlot timeSlot) {
//    }

}
