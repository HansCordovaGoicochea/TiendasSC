package com.scientechperu.pideloya.Adaptadores;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.scientechperu.pideloya.Clases.Carro;
import com.scientechperu.pideloya.Clases.Productos;
import com.scientechperu.pideloya.Clases.UrlRaiz;
import com.scientechperu.pideloya.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link BaseAdapter} personalizado para el gridview
 */
public class RecyclerAdapterProductos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Productos> items;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean isFooterEnabled = true;

    public RecyclerAdapterProductos(Context mContext,List<Productos> items) {
        this.context = mContext;
        this.items = items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View v1 = inflater.inflate(R.layout.grid_item_productos, null);
                viewHolder = new RecyclerViewHolderProductos(v1, items);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.progress_item, parent, false);
                viewHolder = new ProgressViewHolder(v2);
                break;
        }


//
//        final RecyclerView.ViewHolder finalViewHolder = viewHolder;
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Tag Click", "position = " + finalViewHolder.getAdapterPosition());
//            }
//        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

//        Toast toast= Toast.makeText(context, ""+Carro.count(Carro.class), Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
//        toast.show();

        switch (getItemViewType(position)) {
            case ITEM:
                final Productos item = items.get(position);
                if (item.getId_image() != 0) {
                    // load image from the internet using Glide
                    String url_imagen = UrlRaiz.domain_api + "images/products/" + item.getId_product() + "/" + item.getId_image() + "" + UrlRaiz.ws_key;

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
                                    ((RecyclerViewHolderProductos) holder).progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((RecyclerViewHolderProductos) holder).progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(((RecyclerViewHolderProductos) holder).image);
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
                                    ((RecyclerViewHolderProductos) holder).progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((RecyclerViewHolderProductos) holder).progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(((RecyclerViewHolderProductos) holder).image);
                }

                ((RecyclerViewHolderProductos) holder).name.setText(item.getName());
                ((RecyclerViewHolderProductos) holder).precio_con_igv.setText("S/" + item.getPrecio_con_igv());
                break;
            case LOADING:
//                Do nothing
                break;
        }




    }


    @Override
    public int getItemCount() {

        return  (isFooterEnabled) ? items.size() + 1 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (isFooterEnabled && position >= items.size() ) ? LOADING : ITEM;
    }

    /**
     * Enable or disable footer (Default is true)
     *
     * @param isEnabled boolean to turn on or off footer.
     */
    public void enableFooter(boolean isEnabled){
        this.isFooterEnabled = isEnabled;
    }


    public void setFilter(ArrayList<Productos> nuevosProductos){
        items = new ArrayList<>();
        items.addAll(nuevosProductos);
        notifyDataSetChanged();
    }
}