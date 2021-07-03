package com.barryzea.firechat.ChatModule.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.pojo.Message;
import com.barryzea.firechat.databinding.ItemChatBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> messages;
    private OnItemClickListener listener;
    private ItemChatBinding binding;

    private int lastPhoto =0;

    public ChatAdapter(List<Message> messages, OnItemClickListener listener) {
        this.messages = messages;
        this.listener = listener;

    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        binding= ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        mContext= parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull  ChatAdapter.ViewHolder holder, int position) {
         Message msg= messages.get(position);
        final int maxMarginHorizontal=mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_horizontal);
        final int maxMarginTop=mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_max_top);
        final int minMargin=mContext.getResources().getDimensionPixelSize(R.dimen.chat_margin_min);


        int gravity= Gravity.END;
        Drawable background= ContextCompat.getDrawable(mContext, R.drawable.background_chat_me);
        int marginStart=maxMarginHorizontal;
        int marginTop=minMargin;
        int marginEnd=minMargin;
        if(!msg.isSentByMe()){
            gravity= Gravity.START;
            background=ContextCompat.getDrawable(mContext, R.drawable.background_chat_friend);
            marginEnd=maxMarginHorizontal;
            marginStart=minMargin;
        }
        if(position >0 && msg.isSentByMe() != messages.get(position -1 ).isSentByMe()){
            marginTop = maxMarginTop;
        }
        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)holder.tvMessage.getLayoutParams();
        params.gravity=gravity;
        params.setMargins(marginStart, marginTop, marginEnd, minMargin);
        if(msg.getPhotoUrl() != null){
            holder.tvMessage.setVisibility(View.GONE);
            holder.imgPhoto.setVisibility(View.VISIBLE);
            if(position>lastPhoto){
                lastPhoto = position;
            }
            final  int size=mContext.getResources().getDimensionPixelSize(R.dimen.chat_size_image);
            params.width=size;
            params.height=size;

            RequestOptions options= new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_timer_sand_160)
                    .error(R.drawable.ic_emoticon_sad)
                    .centerCrop();
            Glide.with(mContext)
                    .asBitmap()
                    .load(msg.getPhotoUrl())
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable  GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                           int dimension=size - mContext.getResources().getDimensionPixelSize(R.dimen.chat_padding_image);
                           Bitmap bitmap= ThumbnailUtils.extractThumbnail(resource, dimension, dimension);
                           holder.imgPhoto.setImageBitmap(bitmap);
                           if(!msg.isLoaded()){
                               msg.setLoaded(true);
                               if(position==lastPhoto){
                                   listener.onImageLoad();
                               }

                           }
                            return true;
                        }
                    })
                    .into(holder.imgPhoto);
                    holder.imgPhoto.setBackground(background);
                    holder.setClickListener(msg, listener);
        }
        else{
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.imgPhoto.setVisibility(View.GONE);

            params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width=LinearLayout.LayoutParams.WRAP_CONTENT;

            holder.tvMessage.setBackground(background);
            holder.tvMessage.setText(msg.getMsg());
        }
        holder.imgPhoto.setLayoutParams(params);
        holder.tvMessage.setLayoutParams(params);


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void add(Message msg) {
        if(!messages.contains(msg)){
            messages.add(msg);
            notifyItemInserted(messages.size() -1 );
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;
        public ImageView imgPhoto;
        public ViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            tvMessage= binding.tvMessageItemChat;
            imgPhoto= binding.imgPhotoItemChat;
        }
        private void setClickListener(final Message message, OnItemClickListener listener){
            imgPhoto.setOnClickListener(click->{
                listener.onClickImage(message);
            });
        }
    }
}
