package com.scientechperu.pideloya.Adaptadores;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scientechperu.pideloya.Clases.Carro;
import com.scientechperu.pideloya.Clases.Productos;
import com.scientechperu.pideloya.Entendiendo.CustomBottomSheetDialogFragment;
import com.scientechperu.pideloya.R;

import java.util.List;

public class RecyclerViewHolderCarrito extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView imagen_car;
    public TextView nombre_producto_car;
    public TextView cantidad_producto_car;
    public TextView precio_producto_car;
    public TextView precio_unit_car;
    public final ProgressBar progressBarCarrito;
    public ImageView row_delete;

    private List<Carro> items;
    Carro carro;

    public RecyclerViewHolderCarrito(View itemView, List<Carro> items) {
        super(itemView);
        itemView.setOnClickListener(this);

        imagen_car = (ImageView) itemView.findViewById(R.id.imagen_producto_carrito);
        nombre_producto_car = (TextView) itemView.findViewById(R.id.nombre_producto_carrito);
        cantidad_producto_car = (TextView) itemView.findViewById(R.id.cantidad_pedida_carrito);
        precio_unit_car = (TextView) itemView.findViewById(R.id.precio_unit_carrito);
        precio_producto_car = (TextView) itemView.findViewById(R.id.precio_producto_carrito);
        progressBarCarrito = (ProgressBar) itemView.findViewById(R.id.homeprogress_carrito);
        row_delete = itemView.findViewById(R.id.img_row_delete);


        this.items = items;

//        Toast.makeText(itemView.getContext(), "dsfdsfdsfdsf", Toast.LENGTH_SHORT).show();
    }

//    @Override
    public void onClick(final View view) {


//        producto = items.get(getAdapterPosition());
//
////        String nombre_producto = items.get(getAdapterPosition()).getName();
////        String recicler_precio = items.get(getAdapterPosition()).getPrecio_con_igv();
//
//
//        customBottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("producto", producto);
//        customBottomSheetDialogFragment.setArguments(bundle);
//        customBottomSheetDialogFragment.show(((FragmentActivity)itemView.getContext()).getSupportFragmentManager(), customBottomSheetDialogFragment.getTag());
//
    }
}