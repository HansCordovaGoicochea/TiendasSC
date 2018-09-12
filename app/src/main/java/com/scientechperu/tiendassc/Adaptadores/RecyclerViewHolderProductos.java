package com.scientechperu.tiendassc.Adaptadores;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.scientechperu.tiendassc.Entendiendo.CustomBottomSheetDialogFragment;
import com.scientechperu.tiendassc.R;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.List;
import java.util.UUID;

public class RecyclerViewHolderProductos extends RecyclerView.ViewHolder implements View.OnClickListener{


    public ImageView image;
    public final ProgressBar progressBar;
    public TextView name;
    public TextView precio_con_igv;
    public CardView cardView;
    public ImageView descripcion;
    Dialog myDialog;

    public BottomSheetDialog bottomSheetDialog;

    public CustomBottomSheetDialogFragment customBottomSheetDialogFragment;


    private List<Productos> items;
    Productos producto;

    int mCartItemCount = 0;

    public RecyclerViewHolderProductos(View itemView, List<Productos> items) {
        super(itemView);
        itemView.setOnClickListener(this);

        image = (ImageView) itemView.findViewById(R.id.imagen);
        progressBar = (ProgressBar) itemView.findViewById(R.id.homeprogress);
        name = (TextView) itemView.findViewById(R.id.nombre);
        precio_con_igv = (TextView) itemView.findViewById(R.id.precio);
        cardView = (CardView) itemView.findViewById(R.id.cardProductos);

        bottomSheetDialog = new BottomSheetDialog(itemView.getContext());

        descripcion = (ImageView) itemView.findViewById(R.id.img_row_view);
        descripcion.setOnClickListener(this);
        myDialog = new Dialog(itemView.getContext());

        this.items = items;

        customBottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
//
//        Toast.makeText(itemView.getContext(), "dsfdsfdsfdsf", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(final View view) {
        producto = items.get(getAdapterPosition());

        if (view.getId() == descripcion.getId()) {
//            Toast.makeText(itemView.getContext(), "Image Clicked!", Toast.LENGTH_LONG).show();
            ShowPopup(view);
        }else{
            //        String nombre_producto = items.get(getAdapterPosition()).getName();
//        String recicler_precio = items.get(getAdapterPosition()).getPrecio_con_igv();


            customBottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("producto", producto);
            customBottomSheetDialogFragment.setArguments(bundle);
            customBottomSheetDialogFragment.show(((FragmentActivity)itemView.getContext()).getSupportFragmentManager(), customBottomSheetDialogFragment.getTag());

        }


    }
    @SuppressLint("SetTextI18n")
    public void ShowPopup(View v) {
//        Toast.makeText(itemView.getContext(), producto.getDescription_short(), Toast.LENGTH_LONG).show();
        TextView txtclose;
        TextView txtNombre_popup;
        TextView txtPrecio_popup;
        TextView descripcion_dialog;
        Button btnFollow;
        ImageView image_popup;
        final NumberPicker cant;
        final TextView badge;
        final ProgressBar progressBar_popup;
        myDialog.setContentView(R.layout.descripcion_producto);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        image_popup = (ImageView) myDialog.findViewById(R.id.imagen_popup);
        progressBar_popup = (ProgressBar) myDialog.findViewById(R.id.popupprogress);

        txtNombre_popup =(TextView) myDialog.findViewById(R.id.nombre_popup);
        txtNombre_popup.setText(producto.getName());
        txtPrecio_popup =(TextView) myDialog.findViewById(R.id.precio_popup);
        txtPrecio_popup.setText("S/ "+producto.getPrecio_con_igv());
        descripcion_dialog =(TextView) myDialog.findViewById(R.id.descripcionpopup);

        cant = myDialog.findViewById(R.id.cantidad);
        badge = itemView.getRootView().findViewById(R.id.cart_badge);

        if (!producto.getDescription_short().equals("")){
            descripcion_dialog.setText(producto.getDescription_short());
        }else{
            descripcion_dialog.setText("No hay Descripción");
        }

        if (producto.getId_image() > 0){
            String url_imagen_poup = UrlRaiz.domain_api + "images/products/" + producto.getId_product() + "/" + producto.getId_image() + "" + UrlRaiz.ws_key;
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // because file name is always same
                    .placeholder(R.drawable.noimage);

            Log.e("IMAGEN URL", url_imagen_poup);
            Glide.with(itemView.getContext())
                    .load(url_imagen_poup)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar_popup.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar_popup.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(image_popup);
        }else{
            progressBar_popup.setVisibility(View.GONE);
        }


        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Carro> carros = Carro.find(Carro.class, "idproducto = ?", producto.getId_product().toString());
                if (carros.size() > 0) {


                    Carro carro = carros.get(0);

                    Integer cantidad_old = carro.getCantidad();
                    Integer cantidad_new = cantidad_old + cant.getValue();

                    carro.setCantidad(cantidad_new);
                    Double importe = (cantidad_new * Double.parseDouble(producto.getPrecio_con_igv()));
                    carro.setImporte(importe);
                    carro.save();

//                    Toast.makeText(getContext(), "¡Tienes "+cantidad_new+" "+item.getName()+"!", Toast.LENGTH_SHORT).show();
                    Toast toast= Toast.makeText(itemView.getContext(), "¡Tienes "+cantidad_new+" "+producto.getName()+"!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 60);
                    toast.show();

                }else{
                    Double importe = (cant.getValue() * Double.parseDouble(producto.getPrecio_con_igv()));

                    Carro carro = new Carro();
                    carro.setId_cart(UUID.randomUUID().toString());
                    carro.setId_producto(producto.getId_product().toString());
                    carro.setCantidad(cant.getValue());
                    carro.setNombre_producto(producto.getName());
                    carro.setPrecio_producto(Double.valueOf(producto.getPrecio_con_igv()));
                    carro.setId_image(producto.getId_image());
                    carro.setImporte(importe);
                    carro.setId_shop(Integer.valueOf(producto.getId_shop_default()));
                    carro.save();

//                    Toast.makeText(getContext(), "¡Tienes "+cant.getValue()+" "+item.getName(), Toast.LENGTH_SHORT).show();
                    Toast toast= Toast.makeText(itemView.getContext(), "¡Tienes "+cant.getValue()+" "+producto.getName(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                    toast.show();
                }

//                    Toast toast= Toast.makeText(getContext(), "¡Tienes "+cant.getValue()+" "+item.getName(), Toast.LENGTH_SHORT);

//                Toast.makeText(getContext(), "num "+badge.getText(), Toast.LENGTH_SHORT).show();
                //actualizar el icono del carrito
                // To load the data at a later time
                SharedPreferences prefs = itemView.getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
                Integer idtienda_current = prefs.getInt("id_shop", 0);


                String[] vals = {
                        String.valueOf(idtienda_current)
                };
                int mCartItemCount = (int) Carro.count(Carro.class, "idshop = ?", vals);
                if (mCartItemCount < 0){
                    mCartItemCount = 0;
                }
                badge.setText(String.valueOf(mCartItemCount));

                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}