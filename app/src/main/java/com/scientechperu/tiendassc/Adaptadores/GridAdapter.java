package com.scientechperu.tiendassc.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Fragmentos.FragmentProductos;
import com.scientechperu.tiendassc.R;

import java.util.List;

/**
 * {@link BaseAdapter} personalizado para el gridview
 */
public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Tienda> items;
    ImageView image;
    RequestOptions options ;


    public GridAdapter(Context c, List<Tienda> items) {
        mContext = c;
        this.items = items;
    }


    @Override
    public int getCount() {
        // Decremento en 1, para no contar el header view
        return items.size();
    }

    @Override
    public Tienda getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
        }

        final Tienda item = getItem(position);

        // Seteando Imagen
        image = (ImageView) view.findViewById(R.id.imagen);

        // load image from the internet using Glide
        String url_imagen = UrlRaiz.domain + "/api/images/stores/"+item.getId_store()+""+UrlRaiz.ws_key;


        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.homeprogress);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // because file name is always same
                .placeholder(R.drawable.noimage);

        Glide.with(view.getContext())
                .load(url_imagen)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);

//
//        // Seteando Nombre
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getNombre());
//
//        // Seteando Direccion
        TextView direccion = (TextView) view.findViewById(R.id.direccion);
        direccion.setText(item.getDireccion());
//
        // Seteando Precio
        TextView telefono = (TextView) view.findViewById(R.id.telefono);
        telefono.setText(item.getTelefono());

//        // Seteando Rating
//        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);
//        ratingBar.setRating(item.getRating());

        final CardView cardView = (CardView) view.findViewById(R.id.cardTienda);



        cardView.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                cardView.setCardBackgroundColor(R.color.color_light);

                // Enviar URL como arguemento del fragmento

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragmentProductos = new FragmentProductos();
                Bundle args = new Bundle();
                args.putString(FragmentProductos.ARG_SECTION_URL, UrlRaiz.domain+"/"+item.getVirtual_uri()+"api/products"+UrlRaiz.ws_key+"&output_format=JSON&filter[active]=1&filter[id_caja]="+item.getId_caja()+"&display=full&price[precio_con_igv][use_tax]=1");
                fragmentProductos = FragmentProductos.newInstance("Productos");
                fragmentProductos.setArguments(args);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, fragmentProductos).addToBackStack(null).commit();
                activity.setTitle(FragmentProductos.ARG_SECTION_TITLE);
            }
        });

        return view;
    }

}