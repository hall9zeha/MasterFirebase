package com.barryzea.firechat.MainModule.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.databinding.ItemUserBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> listUsers;
    private Context mContext;
    private EventClickListener mListener;

    public UserAdapter(List<User> listUsers, EventClickListener mListener) {
        this.listUsers = listUsers;
        this.mListener = mListener;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext= parent.getContext();
        return new ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull  UserAdapter.ViewHolder holder, int position) {
            holder.mUser=listUsers.get(position);
            holder.setOnClickListener(holder.mUser,mListener);

        holder.tvName.setText(holder.mUser.getUsernameValid());

        int msgUnread=holder.mUser.getMessagesUnread();
        if(msgUnread >0){
            String countUnr= msgUnread>99?
                    mContext.getString(R.string.main_item_max_messagesUnread)
                    :String.valueOf(msgUnread);
            holder.tvUnreadMsg.setText(countUnr);
            holder.tvUnreadMsg.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvUnreadMsg.setVisibility(View.GONE);
        }
        RequestOptions options= new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);

        Glide.with(mContext)
                .load(holder.mUser.getPhotoUrl())
                .apply(options)
                .into(holder.imgPhoto);


    }

    public void add(User user){
        if(!listUsers.contains(user)){
            listUsers.add(user);
            notifyItemInserted(listUsers.size() -1);
        }
        else{
            update(user);
        }
    }

    public void update(User user) {
        if(listUsers.contains(user)){
            int index= listUsers.indexOf(user);
            listUsers.set(index, user);
            notifyItemChanged(index);
        }
    }
    public void remove(User user){
        if(listUsers.contains(user)){
            int index= listUsers.indexOf(user);
            listUsers.remove(user);
            notifyItemRemoved(index);
        }
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgPhoto;
        public TextView tvName, tvUnreadMsg;
        public User mUser;
        private View mView;
        public ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            imgPhoto=binding.ctUser.imageViewPhoto;
            tvName= binding.ctUser.tvName;
            tvUnreadMsg= binding.ctUser.tvCountUnread;
            mView=binding.getRoot();

        }
        public void setOnClickListener(final User user, final EventClickListener listener){
            mView.setOnClickListener(click->{
                listener.onItemClick(user);
            });
            mView.setOnLongClickListener(view -> {
                listener.onItemLongClick(user);
                return true;
            });
        }
    }
}
