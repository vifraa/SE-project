package com.chalmers.gyarados.split;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_LEAVE= 3;
    private static final int VIEW_TYPE_JOIN= 4;

    private Context mContext;
    private List<Message> mMessageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if(viewType==VIEW_TYPE_JOIN){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_join,parent,false);
            return new JoinMessageHolder(view);
        }else if(viewType==VIEW_TYPE_LEAVE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_leave,parent,false);
            return new LeaveMessageHolder(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_LEAVE:
                ((LeaveMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_JOIN:
                ((JoinMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);
        MessageType type = message.getType();

        if(type.equals(MessageType.JOIN)){
            return VIEW_TYPE_JOIN;
        }
        else if(type.equals(MessageType.LEAVE)){
            return VIEW_TYPE_LEAVE;
        }else{
            if (message.getSender().getUserId().equals(CurrentSession.getCurrentUser().getUserId())) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }

    }

    public void addItem(Message message) {
        mMessageList.add(message);
        notifyItemInserted(mMessageList.size()-1);
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{

        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(Utils.formatDateTime(message.getTimestamp()));
            nameText.setText(message.getSender().getName());
            String photoUrl = message.getSender().getPhotoUrl();
            if(photoUrl!=null){
                ImageConverter.loadRoundedImage(mContext,photoUrl,profileImage);
            }else{
                profileImage.setImageDrawable(
                        ResourcesCompat.getDrawable(mContext.getResources(),R.drawable.profile_pic_default,null));
            }
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getContent());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(Utils.formatDateTime(message.getTimestamp()));
        }
    }

    private class JoinMessageHolder extends RecyclerView.ViewHolder{

        TextView messageText, timeText, nameText;


        JoinMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);

        }

        void bind(Message message) {
            //messageText.setText(message.getContent());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(Utils.formatDateTime(message.getTimestamp()));
            nameText.setText(message.getSender().getName());


        }
    }

    private class LeaveMessageHolder extends RecyclerView.ViewHolder{

        TextView messageText, timeText, nameText;

        LeaveMessageHolder(View itemView) {
            super(itemView);
            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
            nameText =  itemView.findViewById(R.id.text_message_name);

        }

        void bind(Message message) {
            //messageText.setText(message.getContent());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(Utils.formatDateTime(message.getTimestamp()));
            nameText.setText(message.getSender().getName());

        }
    }


}
