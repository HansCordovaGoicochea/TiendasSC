package com.scientechperu.tiendassc.Adaptadores;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.Entendiendo.CustomBottomSheetDialogFragment;
import com.scientechperu.tiendassc.R;

import java.util.List;

public class RecyclerViewHolderProductos extends RecyclerView.ViewHolder implements View.OnClickListener{


    public ImageView image;
    public final ProgressBar progressBar;
    public TextView name;
    public TextView precio_con_igv;
    public CardView cardView;


    public BottomSheetDialog bottomSheetDialog;

    public CustomBottomSheetDialogFragment customBottomSheetDialogFragment;


    private List<Productos> items;
    Productos producto;

    public RecyclerViewHolderProductos(View itemView, List<Productos> items) {
        super(itemView);
        itemView.setOnClickListener(this);

        image = (ImageView) itemView.findViewById(R.id.imagen);
        progressBar = (ProgressBar) itemView.findViewById(R.id.homeprogress);
        name = (TextView) itemView.findViewById(R.id.nombre);
        precio_con_igv = (TextView) itemView.findViewById(R.id.precio);
        cardView = (CardView) itemView.findViewById(R.id.cardProductos);

        bottomSheetDialog = new BottomSheetDialog(itemView.getContext());

        this.items = items;

        customBottomSheetDialogFragment = new CustomBottomSheetDialogFragment();

    }

    @Override
    public void onClick(final View view) {
        producto = items.get(getAdapterPosition());

//        String nombre_producto = items.get(getAdapterPosition()).getName();
//        String recicler_precio = items.get(getAdapterPosition()).getPrecio_con_igv();


        customBottomSheetDialogFragment = new CustomBottomSheetDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("producto", producto);
        customBottomSheetDialogFragment.setArguments(bundle);
        customBottomSheetDialogFragment.show(((FragmentActivity)itemView.getContext()).getSupportFragmentManager(), customBottomSheetDialogFragment.getTag());

    }
}