package postpc.studypartner2.chat;

import android.content.Context;
import android.graphics.Typeface;
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
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import postpc.studypartner2.MainActivity;
import postpc.studypartner2.R;
import postpc.studypartner2.profile.User;
import postpc.studypartner2.utils.HelperFunctions;

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
        void onConversationClick(Conversation conversation, View view);
    }

    static class ConversationsAdapter extends ListAdapter<Conversation, ConversationHolder> {
        private List<Conversation> conversations = new ArrayList<>();
        static ClickListener clickListener;
//        private HashMap<String, User[]> convoToUsersDict = new HashMap<>();
        Context context;

        public ConversationsAdapter(Context context) {
            super(new ConversationCallBack());
            this.context = context;
        }

        public ConversationClickCallBack callBack;

        @NonNull @Override
        public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ConversationHolder(inflater.inflate(R.layout.item_conversation, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final ConversationHolder holder, final int position) {
            Conversation conversation = conversations.get(position);
            holder.setData(conversation);
            holder.partnerAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null){
                        callBack.onConversationClick(getConversation(position), view);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return conversations.size();
        }

        public void setConversations(List<Conversation> conversations) {
            this.conversations = conversations;
            notifyDataSetChanged();
        }

        public Conversation getConversation(int position){
            return this.conversations.get(position);
        }

        public void setOnItemClickListener(ClickListener clickListener) {
            ConversationsAdapter.clickListener = clickListener;
        }

//        public HashMap<String, HashMap<String, User>> getConvoToUsersDict() {
//            return convoToUsersDict;
//        }
//
//        public void setConvoToUsersDict(HashMap<String, User[]> convoToUsersDict) {
//            this.convoToUsersDict = convoToUsersDict;
//        }

        public interface ClickListener {
            void onItemClick(int position, View v);
        }

    }

    static class ConversationHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View view;
        public final TextView partnerName;
        public final TextView lastMsg;
        public final TextView timeOfLastMsg;
        public final ImageView partnerAvatar;

        public ConversationHolder(@NonNull View itemView)  {
            super(itemView);
            this.view = itemView;
            itemView.setOnClickListener(this);
            partnerName = itemView.findViewById(R.id.conv_partner_name);
            lastMsg = itemView.findViewById(R.id.conv_last_msg);
            timeOfLastMsg = itemView.findViewById(R.id.conv_time_of_last_msg);
            partnerAvatar = itemView.findViewById(R.id.conv_avatar);
        }

        public void setData(Conversation conversation){
//            User partner = conversation.getOtherUser();
            String otherUserUID = HelperFunctions.determineOtherUserUIDFromConversation(conversation);
            User partner = conversation.getUsers().get(otherUserUID);
            User thisUser = conversation.getUsers().get(MainActivity.getCurrentUserID());
            partnerName.setText(partner.getName());
            setPartnerAvatar(partner.getImage_url());
            if (conversation.getLastMessage() != null) {
                lastMsg.setText(conversation.getLastMessage().getMessageText());

                // set conversation in bold
                if (conversation.getUnread() != null ) {
                    if (conversation.getUnread() && !conversation.getLastMsg().getSenderUID().equals(thisUser.getUid())) {
//                        lastMsg.setTypeface(lastMsg.getTypeface(), Typeface.BOLD);
                        setUnreadView(true);
                    } else {
//                        lastMsg.setTypeface(lastMsg.getTypeface(), Typeface.NORMAL);
                        setUnreadView(false);
                    }
                }
            }
            timeOfLastMsg.setText(conversation.getLastMessage().getPrettySentTime());
        }

        private void setUnreadView(boolean isUnread){
            if (isUnread) {
                lastMsg.setTypeface(lastMsg.getTypeface(), Typeface.BOLD);
                partnerName.setTypeface(lastMsg.getTypeface(), Typeface.BOLD);
                timeOfLastMsg.setTypeface(lastMsg.getTypeface(), Typeface.BOLD);
            } else {
                lastMsg.setTypeface(lastMsg.getTypeface(), Typeface.NORMAL);
                partnerName.setTypeface(lastMsg.getTypeface(), Typeface.NORMAL);
                timeOfLastMsg.setTypeface(lastMsg.getTypeface(), Typeface.NORMAL);
            }
        }

        private void setPartnerAvatar(String url){
            Glide.with(view)
                    .load(url)
                    .placeholder(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(partnerAvatar);
        }

        @Override
        public void onClick(View view) {
            ConversationsAdapter.clickListener.onItemClick(getAdapterPosition(), view);
        }
    }


}


