//package postpc.studypartner2.partners;
//
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
//import java.util.ArrayList;
//import java.util.List;
//
//import postpc.studypartner2.profile.User;
//import postpc.studypartner2.R;
//import postpc.studypartner2.utils.HelperFunctions;
//
//public class PartnerRecyclerUtils {
//
//    static class PartnerCallBack
//            extends DiffUtil.ItemCallback<User> {
//
//        @Override
//        public boolean areItemsTheSame(@NonNull User r1, @NonNull User r2) {
//            return r1.getUid().equals(r2.getUid());
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull User r1, @NonNull User r2) {
//            return r1.getUid().equals(r2.getUid());
//        }
//    }
//
//    public interface PartnerClickCallBack{
//        void onPartnerLongClick(User user);
//    }
//
//    //    static class PartnersAdapter extends FirestoreRecyclerAdapter<User,PartnerHolder> {
//    static class PartnersAdapter extends ListAdapter<User, PartnerRecyclerUtils.PartnerHolder> {
//        private List<User> partners = new ArrayList<>();
//
//        protected PartnersAdapter() {
//            super(new PartnerCallBack());
//        }
//
//        @NonNull
//        @Override
//        public PartnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            return new PartnerHolder(inflater.inflate(R.layout.item_partner, parent, false));
//
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull PartnerHolder holder, int position) {
//            User currentPartner = partners.get(position);
//            holder.setData(currentPartner);
//        }
//
//        @Override
//        public int getItemCount() {
//            return partners.size();
//        }
//
//        public void setPartners(List<User> results) {
//            this.partners = results;
//            notifyDataSetChanged();
//        }
//    }
//
//    static class PartnerHolder
//            extends RecyclerView.ViewHolder {
//
//        private View view;
//        public final TextView nameTextView;
//        public final TextView distanceTextView;
//        public final ImageView profileImageView;
//
//        public PartnerHolder(@NonNull View itemView) {
//            super(itemView);
//
//            this.view = itemView;
//            nameTextView = itemView.findViewById(R.id.tv_partner_name);
//            profileImageView = itemView.findViewById(R.id.iv_result_img);
//            distanceTextView = itemView.findViewById(R.id.tv_distance);
//        }
//
//        public void setData(User result) {
//            nameTextView.setText(result.getName());
//            distanceTextView.setText(HelperFunctions.getStringDistanceBetweenTwoUsers(currentUser, result));
//            Glide.with(view)
//                    .load(result.getImage_url())
//                    .placeholder(R.drawable.default_avatar)//todo change
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(profileImageView);
//        }
//    }
//}
