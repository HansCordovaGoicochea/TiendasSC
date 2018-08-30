package com.scientechperu.tiendassc.Adaptadores;


import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.R;

import java.util.List;

/**
 * {@link BaseAdapter} personalizado para el gridview
 */
public class RecyclerAdapterCarrito extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Carro> items;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean isFooterEnabled = true;

    public RecyclerAdapterCarrito(List<Carro> items) {
        this.items = items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_carrito, null);
        viewHolder = new RecyclerViewHolderCarrito(v1, items);


        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        final Carro item = items.get(position);
        if (item.getId_image() != 0) {
            // load image from the internet using Glide
            String url_imagen = UrlRaiz.domain_api + "images/products/" + item.getId_producto() + "/" + item.getId_image() + "" + UrlRaiz.ws_key;

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // because file name is always same
                    .placeholder(R.drawable.noimage);
            Glide.with(holder.itemView.getContext())
                    .load(url_imagen)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((RecyclerViewHolderCarrito) holder).progressBarCarrito.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((RecyclerViewHolderCarrito) holder).progressBarCarrito.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(((RecyclerViewHolderCarrito) holder).imagen_car);
        }
        else {
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // because file name is always same
                    .placeholder(R.drawable.noimage);

            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.noimage)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((RecyclerViewHolderCarrito) holder).progressBarCarrito.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((RecyclerViewHolderCarrito) holder).progressBarCarrito.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(((RecyclerViewHolderCarrito) holder).imagen_car);
        }

        ((RecyclerViewHolderCarrito) holder).nombre_producto_car.setText(item.getNombre_producto());
        ((RecyclerViewHolderCarrito) holder).cantidad_producto_car.setText("Cant: "+item.getCantidad());
        ((RecyclerViewHolderCarrito) holder).precio_producto_car.setText("S/"+item.getImporte());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}