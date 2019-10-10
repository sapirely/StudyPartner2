package postpc.studypartner2.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.R;

public class TimeSlotRecyclerUtils {

    public interface onTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot item);
    }

        static class TimeSlotCallBack
                extends DiffUtil.ItemCallback<TimeSlot> {

            @Override
            public boolean areItemsTheSame(@NonNull TimeSlot r1, @NonNull TimeSlot r2) {
                return r1.getStartingTime().equals(r2.getStartingTime()) && r1.getEndingTime().equals(r2.getEndingTime());
            }

            @Override
            public boolean areContentsTheSame(@NonNull TimeSlot r1, @NonNull TimeSlot r2) {
                return r1.getStartingTime().equals(r2.getStartingTime()) && r1.getEndingTime().equals(r2.getEndingTime());
            }
        }

        public interface TimeSlotClickCallBack{
//            void onTimeSlotLongClick(TimeSlot user);
            void onTimeSlotClick(TimeSlot timeSlot);
        }


        static class TimeSlotAdapter extends ListAdapter<TimeSlot, TimeSlotHolder> {
            private List<TimeSlot> timeSlots = new ArrayList<>();
            private final onTimeSlotClickListener listener;

            protected TimeSlotAdapter(onTimeSlotClickListener listener) {
                super(new TimeSlotCallBack());
                this.listener = listener;
            }

            @NonNull
            @Override
            public TimeSlotHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new TimeSlotHolder(inflater.inflate(R.layout.item_time_slot, parent, false));

            }

            @Override
            public void onBindViewHolder(@NonNull final TimeSlotHolder holder, int position) {
                holder.bind(timeSlots.get(position), listener);
//                TimeSlot currentResult = timeSlots.get(position);
//                holder.setData(currentResult);
//                holder.times.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        holder.setBackgroundColor(Color.parseColor("grey"));
//                    }
//                });
            }

            @Override
            public int getItemCount() {
                return timeSlots.size();
            }

            public void setTimeSlots(List<TimeSlot> timeSlots) {
                this.timeSlots = timeSlots;
            }
        }

        static class TimeSlotHolder
                extends RecyclerView.ViewHolder {

            private View view;
            public final TextView times;
            public final MaterialCardView timesCard;

            public TimeSlotHolder(@NonNull View itemView) {
                super(itemView);

                this.view = itemView;
                times = itemView.findViewById(R.id.timeSlotTimesText);
                timesCard = itemView.findViewById(R.id.timeSlotTimesCard);
            }

            public void bind(final TimeSlot timeSlot, final onTimeSlotClickListener listener){
                setData(timeSlot);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        setBackgroundColor(timeSlot.isSelected());
                        if (!timeSlot.isSelected()){
                            // selected now
                            timeSlot.setSelected(true);
                            // todo: add to list
                        } else {
                            // unselected
                            timeSlot.setSelected(false);
                            // todo: remove from list
                        }

                        listener.onTimeSlotClick(timeSlot);
                    }
                });
            }

            public void setData(TimeSlot timeSlot) {
                times.setText(timeSlot.getAll());
        }

            public void setBackgroundColor(boolean isSelected){
                if (!isSelected) {
                    timesCard.setBackgroundColor(Color.GRAY);
                } else {
                    timesCard.setBackgroundColor(Color.WHITE);
                }
            }

    }
}
