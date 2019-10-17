package postpc.studypartner2.chat;

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

import java.util.ArrayList;
import java.util.List;

import postpc.studypartner2.R;
import postpc.studypartner2.profile.User;

public class ConversationRecyclerUtils {
    static class ConversationCallBack
            extends DiffUtil.ItemCallback<Conversation> {

        @Override
        public boolean areItemsTheSame(@NonNull Conversation m1, @NonNull Conversation m2) {
            return m1.getUid1().equals(m2.getUid1()) && m1.getUid2().equals(m2.getUid2()) ;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Conversation m1, @NonNull Conversation m2) {
            return m1.getMessages().equals(m2.getMessages());
        }
    }

    public interface ConversationClickCallBack{
        void onConversationLongClick(Conversation conversation);
    }

    static class ConversationsAdapter extends ListAdapter<Conversation, ConversationHolder> {
        private List<Conversation> conversations = new ArrayList<>();

        public ConversationsAdapter() {
            super(new ConversationCallBack());
        }

        public ConversationClickCallBack callBack;

        @NonNull @Override
        public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ConversationHolder(inflater.inflate(R.layout.item_conversation, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final ConversationHolder holder, int position) {
            Conversation conversation = conversations.get(position);
            holder.setData(conversation);
        }

        @Override
        public int getItemCount() {
            return conversations.size();
        }

        public void setConversations(List<Conversation> conversations) {
            this.conversations = conversations;
            notifyDataSetChanged();
        }

    }

    static class ConversationHolder
            extends RecyclerView.ViewHolder {

        private View view;
        public final TextView partnerName;
        public final TextView lastMsg;
        public final TextView timeOfLastMsg;
        public final ImageView partnerAvatar;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            partnerName = itemView.findViewById(R.id.conv_partner_name);
            lastMsg = itemView.findViewById(R.id.conv_last_msg);
            timeOfLastMsg = itemView.findViewById(R.id.conv_time_of_last_msg);
            partnerAvatar = itemView.findViewById(R.id.conv_avatar);
        }

        public void setData(Conversation conversation){
            User partner = conversation.getOtherUser();
            partnerName.setText(partner.getName());
            setPartnerAvatar(partner.getImage_url());
            if (conversation.getLastMessage() != null) {
                lastMsg.setText(conversation.getLastMessage().getMessageText());
            }
            timeOfLastMsg.setText(conversation.getLastMessage().getPrettySentTime());
        }

        private void setPartnerAvatar(String url){
            Glide.with(view)
                    .load(url)
                    .placeholder(R.drawable.girl)//todo change
                    .apply(RequestOptions.circleCropTransform())
                    .into(partnerAvatar);
        }


    }
}


