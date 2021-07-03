package com.barryzea.firechat.MainModule.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.databinding.ItemRequestBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter  extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<User> listUsers;
    private Context context;
    private EventClickListener mListener;

    public RequestAdapter(List<User> listUsers, EventClickListener mListener) {
        this.listUsers = listUsers;
        this.mListener = mListener;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new ViewHolder(ItemRequestBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        User user=listUsers.get(position);
        holder.user=listUsers.get(position);
        holder.setOnClickListener(user, mListener);
        holder.tvName.setText(user.getUserName());
        holder.tvEmail.setText(user.getEmail());

        RequestOptions options= new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);

        Glide.with(context)
                .load(user.getPhotoUrl())
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
        public User user;
        public CircleImageView imgPhoto;
        public ImageButton btnAccept, btnDeny;
        public TextView tvName, tvEmail;


        public ViewHolder(ItemRequestBinding binding) {
            super(binding.getRoot());
            imgPhoto= binding.imgPhoto;
            btnAccept= binding.imageBtnAccept;
            btnDeny= binding.imageBtnDeny;
            tvName= binding.tvName;
            tvEmail= binding.tvEmail;



        }
        private void setOnClickListener(final User userFriend,EventClickListener listener ){
            btnAccept.setOnClickListener(click ->{


                listener.onAcceptRequest(userFriend);
            });
            btnDeny.setOnClickListener(click->{
                listener.onDeniedRequest(userFriend);
            });
        }
    }
}
