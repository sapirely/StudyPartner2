package postpc.studypartner2.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import postpc.studypartner2.model.User;
import postpc.studypartner2.R;
import postpc.studypartner2.utils.HelperFunctions;
import postpc.studypartner2.utils.Log;

public class ResultRecyclerUtils {
    private static final String TAG = "ResultRecyclerUtils";

    public static User currentUser;

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
        void onResultClick(User user, View view);
    }

    public static class ResultsAdapter extends ListAdapter<User, ResultRecyclerUtils.ResultHolder> {
        private List<User> results = new ArrayList<>();
        private Context context;
        public ResultClickCallBack callBack;

        public ResultsAdapter(Context context) {

            super(new ResultCallBack());
            this.context = context;
        }

        @NonNull
        @Override
        public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ResultHolder(inflater.inflate(R.layout.item_partner, parent, false));

        }

        @Override
        public void onBindViewHolder(final @NonNull ResultHolder holder, final int position) {
            final User currentResult = results.get(position);
                holder.setData(currentResult);
                holder.msgIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("otherChatUserUID", currentResult.getUid());
                        bundle.putParcelable("otherChatUser", currentResult);

                        try {
                            Navigation.findNavController((AppCompatActivity) view.getContext(), R.id.nav_host_fragment)
                                    .navigate(R.id.action_homeFragment_to_chatFragment, bundle);
                        } catch (Exception e){
                            Log.d(TAG, "onClick: failed to navigate from home to chat ");
                            try {
                                Navigation.findNavController((AppCompatActivity) view.getContext(), R.id.nav_host_fragment)
                                        .navigate(R.id.action_searchFragment_to_chatFragment, bundle);
                            } catch (Exception e2){
                                Log.d(TAG, "onClick: failed to navigate from search to chat");
                            }
                        }
                    }
                });
            holder.profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null){
                        callBack.onResultClick(results.get(position), view);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        public void setResults(List<User> results) {
            this.results = results;
            notifyDataSetChanged();
        }

        public void setCurrentUser(User user){
            currentUser = user;
        }
    }

    static class ResultHolder
            extends RecyclerView.ViewHolder {

        private View view;
        public final TextView nameTextView;
        public final TextView distanceTextView;
        public final ImageView profileImageView;
        public final ImageButton msgIcon;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            nameTextView = itemView.findViewById(R.id.tv_partner_name);
            distanceTextView = itemView.findViewById(R.id.tv_distance);
            profileImageView = itemView.findViewById(R.id.iv_result_img);
            msgIcon= itemView.findViewById(R.id.item_partner_chat_icon);
        }


        public void setData(User result) {
            nameTextView.setText(result.getName());
            distanceTextView.setText(HelperFunctions.getStringDistanceBetweenTwoUsers(currentUser, result));
            Glide.with(view)
                    .load(result.getImage_url())
                    .placeholder(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
        }
    }

}