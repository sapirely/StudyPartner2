package postpc.studypartner2.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;

import postpc.studypartner2.R;


public class CourseRecyclerUtils {

    static class CourseCallBack
            extends DiffUtil.ItemCallback<Course> {

        @Override
        public boolean areItemsTheSame(@NonNull Course r1, @NonNull Course r2) {
            return r1 == r2;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course r1, @NonNull Course r2) {
            return (r1.getId().equals(r2.getId()) || r1.getName().equals(r2.getName()));
        }
    }

    public interface CourseClickCallBack{
        void onCourseLongClick(Course course);
    }

    static class CoursesAdapter extends ListAdapter<Course, CourseRecyclerUtils.CourseHolder> {
        public CoursesAdapter() { super(new CourseRecyclerUtils.CourseCallBack()); }

        public CourseRecyclerUtils.CourseClickCallBack callBack;

        @NonNull @Override
        public CourseRecyclerUtils.CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new CourseRecyclerUtils.CourseHolder(inflater.inflate(R.layout.item_partner, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final CourseRecyclerUtils.CourseHolder holder, int position) {
            Course Course = getItem(position);
            holder.courseId.setText("67777");
//            Date currentTime = Calendar.getInstance().getTime(); //todo delete
//            holder.distanceTextView.setText(currentTime.toString()); // todo change
            holder.courseId.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // create delete Course dialog
                    return true;
                }
            });
        }

    }


    static class CourseHolder
            extends RecyclerView.ViewHolder {

        public final TextView courseId;
//        public final TextView courseName;

        public CourseHolder(@NonNull View itemView) {
            super(itemView);

            courseId = itemView.findViewById(R.id.courseNumber);
        }
    }
    
}