package com.scientechperu.tiendassc.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scientechperu.tiendassc.Clases.DetalleOrder;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.R;

import java.util.ArrayList;
import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList<DetalleOrder> detalle;

    public PedidoAdapter(Context context, ArrayList<DetalleOrder> detalle) {
        this.context = context;
        this.detalle = detalle;
    }

    @NonNull
    @Override
    public PedidoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoAdapter.ViewHolder holder, int position) {
        final DetalleOrder item = detalle.get(position);


        holder.nombre_producto_car.setText(item.getProduct_name());
        holder.cantidad_producto_car.setText("Cant: "+Double.parseDouble(item.getProduct_quantity()));
        holder.precio_unit_car.setText("P.U.: S/"+Double.parseDouble(item.getUnit_price_tax_incl()));
        double importe = Double.valueOf(item.getProduct_quantity()) * Double.valueOf(item.getUnit_price_tax_incl());
        holder.precio_producto_car.setText("S/"+importe);

    }

    @Override
    public int getItemCount() {
        return detalle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imagen_car;
        public TextView nombre_producto_car;
        public TextView cantidad_producto_car;
        public TextView precio_producto_car;
        public TextView precio_unit_car;
        public final ProgressBar progressBarCarrito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen_car = (ImageView) itemView.findViewById(R.id.imagen_producto_carrito);
            nombre_producto_car = (TextView) itemView.findViewById(R.id.nombre_producto_carrito);
            cantidad_producto_car = (TextView) itemView.findViewById(R.id.cantidad_pedida_carrito);
            precio_unit_car = (TextView) itemView.findViewById(R.id.precio_unit_carrito);
            precio_producto_car = (TextView) itemView.findViewById(R.id.precio_producto_carrito);
            progressBarCarrito = (ProgressBar) itemView.findViewById(R.id.homeprogress_carrito);


        }
    }
}
