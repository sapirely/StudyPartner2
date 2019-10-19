package postpc.studypartner2.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
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
        void onRequestLongClick(User user);
    }

    //    static class RequestsAdapter extends FirestoreRecyclerAdapter<User,RequestHolder> {
    public static class RequestsAdapter extends ListAdapter<User, RequestRecyclerUtils.RequestHolder> {
        private List<User> requests = new ArrayList<>();
        private Context context;

        public RequestsAdapter(Context context) {

            super(new RequestCallBack());
            this.context = context;
        }

        @NonNull
        @Override
        public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new RequestHolder(inflater.inflate(R.layout.item_request, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
            final User currentRequest = requests.get(position);
            holder.setData(currentRequest);
//            holder.msgIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("otherChatUserUID", currentRequest.getUid());
//                    bundle.putParcelable("otherChatUser", currentRequest);
//
//                    // todo: unspaghetti
//                    try {
//                        Navigation.findNavController((AppCompatActivity) view.getContext(), R.id.nav_host_fragment)
//                                .navigate(R.id.action_homeFragment_to_chatFragment, bundle);
//                    } catch (Exception e){
//                        Log.d(TAG, "onClick: failed to navigate from home to chat ");
//                        try {
//                            Navigation.findNavController((AppCompatActivity) view.getContext(), R.id.nav_host_fragment)
//                                    .navigate(R.id.action_searchFragment_to_chatFragment, bundle);
//                        } catch (Exception e2){
//                            Log.d(TAG, "onClick: failed to navigate from search to chat");
//                        }
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        public void setRequests(List<User> requests) {
            this.requests = requests;
            notifyDataSetChanged();
        }

        public void setCurrentUser(User user){
            currentUser = user;
        }
    }

    static class RequestHolder
            extends RecyclerView.ViewHolder {

        private View view;
        public final TextView nameTextView;
        public final TextView distanceTextView;
        public final ImageView profileImageView;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            nameTextView = itemView.findViewById(R.id.request_partner_name);
            distanceTextView = itemView.findViewById(R.id.request_distance);
            profileImageView = itemView.findViewById(R.id.request_img);
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
    }

}