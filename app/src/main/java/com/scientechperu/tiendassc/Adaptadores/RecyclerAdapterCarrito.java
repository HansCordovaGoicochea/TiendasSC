package com.scientechperu.tiendassc.Adaptadores;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import java.util.ArrayList;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


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

        ((RecyclerViewHolderCarrito) holder).row_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout linearLayout = new RelativeLayout(holder.itemView.getContext());
                final NumberPicker aNumberPicker = new NumberPicker(holder.itemView.getContext());
                aNumberPicker.setMaxValue(item.getCantidad());
                aNumberPicker.setMinValue(1);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                linearLayout.setLayoutParams(params);
                linearLayout.addView(aNumberPicker,numPicerParams);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(((RecyclerViewHolderCarrito) holder).itemView.getContext());
                alertDialogBuilder.setTitle("¿Cuantos eliminar?");
                alertDialogBuilder.setView(linearLayout);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Eliminar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        Log.e("","New Quantity Value : "+ aNumberPicker.getValue());
//                                        Toast.makeText(holder.itemView.getContext(), ""+aNumberPicker.getValue(), Toast.LENGTH_SHORT).show();
                                        Carro carro = Carro.findById(Carro.class, item.getId());
                                        if (aNumberPicker.getValue() == item.getCantidad()){
                                            carro.delete();
                                            removeAt(position);
                                        }
                                        else{
                                            Integer cantidad_actualizada =  item.getCantidad() - aNumberPicker.getValue();
                                            Double importe = (cantidad_actualizada * item.getImporte());
                                            carro.setImporte(importe);
                                            carro.setCantidad(cantidad_actualizada);
                                            carro.save(); // updates the previous entry with new values.
                                            List<Carro> carros = Carro.listAll(Carro.class);
                                            update(carros);

                                        }

                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });




    }

    public void removeAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    public void update(List<Carro> datas){
        items.clear();
        items.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}