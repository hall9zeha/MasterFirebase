package com.barryzea.inventariofirebase.MainModule.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.databinding.ItemProductBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private final ListenerEventsProducts listener;
    private Context context;

    public ProductAdapter(List<Product> products, ListenerEventsProducts listener) {

        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context=parent.getContext();
            return new ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(context), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull  ProductAdapter.ViewHolder holder, int position) {
            Product product=products.get(position);

        //concatenar parámetros dentro de un string creado en recursos como un format
        holder.tvDataProduct.setText(context.getString(R.string.data_text_view, product.getName(), String.valueOf(product.getQuantity())));
        holder.tvScore.setText(String.format(Locale.ROOT, "%.2f", product.getScore()));
        RequestOptions options= new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        Glide.with(context)
                .load(product.getPhotoUrl())
                .apply(options)
                .into(holder.imgPhotoProduct);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProductClick(product);

            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onProductLongClick(product);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return  products.size();
    }
    public void addItem(Product prod){
        if(!products.contains(prod)){
            products.add(prod);
            notifyItemInserted(products.size()-1);
        }
        else{

            updateItem(prod);
        }
    }

    public void updateItem(Product prod) {
        if (products.contains(prod)) {
            int index = products.indexOf(prod);
            products.set(index, prod);
            notifyItemChanged(index);

        }
    }
    public void removeItem(Product prod){
        if (products.contains(prod)){
            final int index = products.indexOf(prod);
            products.remove(index);
            notifyItemRemoved(index);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private ImageView imgPhotoProduct;
        private TextView tvDataProduct , tvScore;
        private CardView card;
        public ViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            mView = binding.getRoot();
            imgPhotoProduct = binding.imageViewPhoto;
            tvDataProduct = binding.textViewData;
            tvScore = binding.tvScore;
            card= binding.cardViewMain;


        }



    }
}
