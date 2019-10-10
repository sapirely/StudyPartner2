package postpc.studypartner2.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import postpc.studypartner2.R;

public class MessageRecyclerUtils {
    static class MessageCallBack
            extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull Message m1, @NonNull Message m2) {
            return m1.getId().equals(m2.getId());
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
        public MessagesAdapter() { super(new MessageCallBack()); }

        public MessageClickCallBack callBack;

        @NonNull @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
//            View itemView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
//            final MessageHolder holder = new MessageHolder(itemView);
//            return holder;

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MessageHolder(inflater.inflate(R.layout.item_message, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final MessageHolder holder, int position) {
            Message message = getItem(position);
            holder.text.setText(message.getMessageText());
            holder.text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // create delete message dialog
                    return true;
                }
            });
        }

    }

    static class MessageHolder
            extends RecyclerView.ViewHolder {

        public final TextView text;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.msgText);
        }
    }
}

