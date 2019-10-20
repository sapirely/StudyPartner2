package postpc.studypartner2.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import postpc.studypartner2.R;

public class MessageRecyclerUtils {

    private static final String TAG = "MessageRecyclerUtils";
    static class MessageCallBack
            extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull Message m1, @NonNull Message m2) {
            return m1.getmID().equals(m2.getmID()) && areContentsTheSame(m1, m2);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message m1, @NonNull Message m2) {
            return m1.getMessageText().equals(m2.getMessageText());
        }
    }

    public interface MessageClickCallBack{
        void onMessageLongClick(Message message);
    }

    static class MessagesAdapter extends ListAdapter<Message, MessageHolder>{
        private String currentUID;

        public MessagesAdapter() { super(new MessageCallBack()); }

        public MessageClickCallBack callBack;

        @NonNull @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MessageHolder(inflater.inflate(R.layout.item_message, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final MessageHolder holder, int position) {
            Message message = getItem(position);
            holder.text.setText(message.getMessageText());
            holder.setMessageStyle(isMessageByPartner(message.getSenderUID()));
        }

        private boolean isMessageByPartner(String senderUID){
            Log.d(TAG, "isMessageByPartner: sender uid: "+senderUID+" current uid: "+currentUID);
            return !currentUID.equals(senderUID);
        }

        public void setCurrentUID(String currentUID) {
            this.currentUID = currentUID;
        }
    }

    static class MessageHolder
            extends RecyclerView.ViewHolder {

        public final View itemView;
        public final TextView text;
        private final MaterialCardView cardView;
        private final LinearLayout cardWrapper;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            text = itemView.findViewById(R.id.msgText);
            cardView = itemView.findViewById(R.id.messageCardHolder);
            cardWrapper = itemView.findViewById(R.id.card_wrapper_layout);
        }

        public void setMessageStyle(Boolean isPartner){
            if (isPartner){
                Log.d(TAG, "setMessageStyle: partner style");
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.light_grey));
//                cardView.setForegroundGravity(View.FOCUS_LEFT);
                cardWrapper.setGravity(Gravity.LEFT);
            } else { // current user
                Log.d(TAG, "setMessageStyle: current user style");

                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
                cardWrapper.setGravity(Gravity.RIGHT);
            }
        }
    }
}

