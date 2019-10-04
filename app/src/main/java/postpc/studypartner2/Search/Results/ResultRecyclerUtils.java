package postpc.studypartner2.Search.Results;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import postpc.studypartner2.Profile.User;
import postpc.studypartner2.R;

public class ResultRecyclerUtils {

    static class ResultCallBack
            extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull User r1, @NonNull User r2) {
            return r1.getUid().equals(r2.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User r1, @NonNull User r2) {
            return r1.getUid().equals(r2.getUid());
        }
    }

    public interface ResultClickCallBack{
        void onResultLongClick(User user);
    }

//    static class ResultsAdapter extends FirestoreRecyclerAdapter<User,ResultHolder> {
    static class ResultsAdapter extends ListAdapter<User, ResultRecyclerUtils.ResultHolder> {
        private List<User> results = new ArrayList<>();

        protected ResultsAdapter() {
            super(new ResultCallBack());
        }

    //        /**
    //         * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
    //         * FirestoreRecyclerOptions} for configuration options.
    //         *
    //         * @param options
    //         */
    //        public ResultsAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
    //            super(options);
    //        }


        @NonNull
        @Override
        public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ResultHolder(inflater.inflate(R.layout.item_partner, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
            User currentResult = results.get(position);
//            holder.nameTextView.setText("changed");
                holder.setData(currentResult);
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        //    @Override
    //        protected void onBindViewHolder(@NonNull ResultHolder holder, int position, @NonNull User model) {
    //            // tell the adapter what we want to put in each card
    //            User currentResult = results.get(position);
    //            holder.setData(currentResult);
    //
    //        }

        public void setResults(List<User> results) {
            this.results = results;
            notifyDataSetChanged();
        }
    }

    static class ResultHolder
            extends RecyclerView.ViewHolder {

        private View view;
        public final TextView nameTextView;
        public final TextView distanceTextView;
        public final ImageView profileImageView;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            nameTextView = itemView.findViewById(R.id.tv_partner_name);
            distanceTextView = itemView.findViewById(R.id.tv_distance);
            profileImageView = itemView.findViewById(R.id.iv_result_img);
        }

        public void setData(User result) {
            nameTextView.setText(result.getName());
            distanceTextView.setText("0");
            Glide.with(view)
                    .load(result.getImage_url())
                    .placeholder(R.drawable.girl)//todo change
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
        }
    }

}




//package postpc.studypartner2.Search.Results;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.ListAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//
//import java.util.Calendar;
//import java.util.Date;
//
//import postpc.studypartner2.R;
//
//public class ResultRecyclerUtils {
//
//    static class ResultCallBack
//            extends DiffUtil.ItemCallback<Result> {
//
//        @Override
//        public boolean areItemsTheSame(@NonNull Result r1, @NonNull Result r2) {
//            return r1.getUid().equals(r2.getUid());
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull Result r1, @NonNull Result r2) {
//            return r1.getUid().equals(r2.getUid());
//        }
//    }
//
//    public interface ResultClickCallBack{
//        void onResultLongClick(Result Result);
//    }
//
//    static class ResultsAdapter extends ListAdapter<Result, ResultRecyclerUtils.ResultHolder> {
//        public ResultsAdapter() { super(new ResultRecyclerUtils.ResultCallBack()); }
//
//        public ResultRecyclerUtils.ResultClickCallBack callBack;
//
//        @NonNull @Override
//        public ResultRecyclerUtils.ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
//            Context context = parent.getContext();
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            return new ResultRecyclerUtils.ResultHolder(inflater.inflate(R.layout.item_partner, parent, false));
//
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull final ResultRecyclerUtils.ResultHolder holder, int position) {
//            Result Result = getItem(position);
//            holder.setData();
////            holder.nameTextView.setText("J");
////            Date currentTime = Calendar.getInstance().getTime(); //todo delete
////            holder.distanceTextView.setText(currentTime.toString()); // todo change
////            holder.nameTextView.setOnLongClickListener(new View.OnLongClickListener() {
////                @Override
////                public boolean onLongClick(View v) {
////                    // create delete Result dialog
////                    return true;
////                }
////            });
//        }
//
//    }
//
//
//    static class ResultHolder
//            extends RecyclerView.ViewHolder {
//
//        private View view;
//        public final TextView nameTextView;
//        public final TextView distanceTextView;
//        public final ImageView profileImageView;
//
//        public ResultHolder(@NonNull View itemView) {
//            super(itemView);
//
//            this.view = itemView;
//            nameTextView = itemView.findViewById(R.id.tv_partner_name);
//            distanceTextView = itemView.findViewById(R.id.tv_distance);
//            profileImageView = itemView.findViewById(R.id.iv_result_img);
//        }
//
//        public void setData(String name, String distance, String image_url){
//            nameTextView.setText(name);
////            Glide.with(view)
////                    .load(image_url)
////                    .placeholder(R.drawable.girl)//todo change
////                    .apply(RequestOptions.circleCropTransform())
////                    .into(profileImageView);
//        }
//    }
//}
