package postpc.studypartner2.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.profile.User;
import postpc.studypartner2.R;
import postpc.studypartner2.utils.HelperFunctions;

public class RequestRecyclerUtils {
    private static final String TAG = "RequestRecyclerUtils";

    public static User currentUser;

    static class RequestCallBack
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

    public interface RequestClickCallBack{
        void onRequestClick(View v, int position);
    }

    //    static class RequestsAdapter extends FirestoreRecyclerAdapter<User,RequestHolder> {
    public static class RequestsAdapter extends ListAdapter<User, RequestRecyclerUtils.RequestHolder> {
        private List<User> requests = new ArrayList<>();

        public RequestClickCallBack callBack;

        public RequestsAdapter(Context context) {

            super(new RequestCallBack());
        }


        @NonNull
        @Override
        public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new RequestHolder(inflater.inflate(R.layout.item_request, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RequestHolder holder, final int position) {
            final User currentRequest = requests.get(position);
            holder.setData(currentRequest);
            final RequestHolder fHolder = holder;
            final int fPosition = position;
            fHolder.approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null){
                        callBack.onRequestClick(view, fPosition);
                    }
                }
            });
            fHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null){
                        callBack.onRequestClick(view, fPosition);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }


        public void setRequests(List<User> requests) {
            this.requests = requests;
            notifyDataSetChanged();
        }

        public User getItemByPosition(int position) {
            return this.requests.get(position);
        }

        public void removeAt(int position) {
            this.requests.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    static class RequestHolder
            extends RecyclerView.ViewHolder {

        private View view;
        public final TextView nameTextView;
        public final TextView distanceTextView;
        public final ImageView profileImageView;
        public final ImageButton approveButton;
        public final ImageButton cancelButton;

        public RequestHolder (@NonNull View itemView) {
            super(itemView);

            this.view = itemView;

            nameTextView = itemView.findViewById(R.id.request_partner_name);
            distanceTextView = itemView.findViewById(R.id.request_distance);
            profileImageView = itemView.findViewById(R.id.request_img);
            approveButton = itemView.findViewById(R.id.request_partner_approve_btn);
            cancelButton = itemView.findViewById(R.id.request_partner_cancel_btn);
        }

        public void setData(User request) {
            nameTextView.setText(request.getName());
            distanceTextView.setText(HelperFunctions.getStringDistanceBetweenTwoUsers(currentUser, request));
            Glide.with(view)
                    .load(request.getImage_url())
                    .placeholder(R.drawable.default_avatar)//todo change
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
        }

//        @Override
//        public void onClick(View view) {
//            RequestsAdapter.clickListener.onItemClick(getAdapterPosition(), view);
//        }
    }

}