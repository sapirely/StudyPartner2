package postpc.studypartner2.Search;

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

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import postpc.studypartner2.R;

public class ResultRecyclerUtils {

    static class ResultCallBack
            extends DiffUtil.ItemCallback<Result> {

        @Override
        public boolean areItemsTheSame(@NonNull Result r1, @NonNull Result r2) {
            return r1.getUid().equals(r2.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Result r1, @NonNull Result r2) {
            return r1.getUid().equals(r2.getUid());
        }
    }

    public interface ResultClickCallBack{
        void onResultLongClick(Result Result);
    }

    static class ResultsAdapter extends ListAdapter<Result, ResultRecyclerUtils.ResultHolder> {
        public ResultsAdapter() { super(new ResultRecyclerUtils.ResultCallBack()); }

        public ResultRecyclerUtils.ResultClickCallBack callBack;

        @NonNull @Override
        public ResultRecyclerUtils.ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ResultRecyclerUtils.ResultHolder(inflater.inflate(R.layout.item_partner, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final ResultRecyclerUtils.ResultHolder holder, int position) {
            Result Result = getItem(position);
            holder.nameTextView.setText("J");
            Date currentTime = Calendar.getInstance().getTime(); //todo delete
            holder.distanceTextView.setText(currentTime.toString()); // todo change
            holder.nameTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // create delete Result dialog
                    return true;
                }
            });
        }

    }


    static class ResultHolder
            extends RecyclerView.ViewHolder {

        public final TextView nameTextView;
        public final TextView distanceTextView;
        public final ImageView profileImageView;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tv_partner_name);
            distanceTextView = itemView.findViewById(R.id.tv_distance);
            profileImageView = itemView.findViewById(R.id.iv_result_img);
        }
    }
}
