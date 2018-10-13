package com.scientechperu.pideloya.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scientechperu.pideloya.ActividadPrincipal;
import com.scientechperu.pideloya.Clases.DetalleOrder;
import com.scientechperu.pideloya.Clases.Vehiculos;
import com.scientechperu.pideloya.Fragmentos.FragmentEstacionamiento;
import com.scientechperu.pideloya.QrActivity;
import com.scientechperu.pideloya.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VehiculosAdapter extends RecyclerView.Adapter<VehiculosAdapter.ViewHolder>{

    private final Context context;
    private final List<Vehiculos> vehiculos;

    public VehiculosAdapter(Context context, List<Vehiculos> vehiculos) {
        this.context = context;
        this.vehiculos = vehiculos;
    }

    @NonNull
    @Override
    public VehiculosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehiculo, parent, false);




        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final VehiculosAdapter.ViewHolder holder, final int position) {
        final Vehiculos item = vehiculos.get(position);
//            Log.e("asd", item.getPlaca());
//            Log.e("asd", item.getModelo());
//            Log.e("asd", item.getMarca());
        holder.placa.setText(item.getPlaca());
        holder.modelo.setText(item.getModelo());
        holder.marca.setText(item.getMarca());
        holder.color.setText(item.getColor());
        holder.tipo_vehiculo.setText(item.getTipo_vehiculo());
        holder.tipo_combustible.setText(item.getTipo_combustible());
        holder.fecha_adquisicion.setText(item.getFecha_adquisicion());
        holder.estado.setText(item.isEstado()?"Dentro":"Fuera");


        holder.row_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
                alertDialogBuilder.setTitle("¿Seguro de Eliminar?");
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Eliminar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        Log.e("","New Quantity Value : "+ aNumberPicker.getValue());
//                                        Toast.makeText(holder.itemView.getContext(), ""+aNumberPicker.getValue(), Toast.LENGTH_SHORT).show();
                                        Vehiculos vehiculos = Vehiculos.findById(Vehiculos.class, item.getId());
                                        vehiculos.delete();
                                        removeAt(position);
                                        new SweetAlertDialog(holder.itemView.getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Eliminado!")
                                                .setConfirmText("OK")
                                                .show();

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
        vehiculos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, vehiculos.size());
    }

    @Override
    public int getItemCount() {
        return vehiculos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public TextView placa, modelo, marca, tipo_vehiculo, fecha_adquisicion, color, tipo_combustible, estado;
        public ImageView row_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            placa = itemView.findViewById(R.id.placa_vehiculo);
            modelo = itemView.findViewById(R.id.modelo_vehiculo);
            marca = itemView.findViewById(R.id.marca_vehiculo);
            tipo_vehiculo = itemView.findViewById(R.id.tipo_vehiculo);
            fecha_adquisicion = itemView.findViewById(R.id.fecha_adquisicion);
            color = itemView.findViewById(R.id.color);
            tipo_combustible = itemView.findViewById(R.id.combustible);
            estado = itemView.findViewById(R.id.estado);


            row_delete = itemView.findViewById(R.id.img_row_delete);

        }

        @Override
        public void onClick(View view) {
            Intent intent =new Intent(itemView.getContext(), QrActivity.class);
            intent.putExtra("idvehiculo", vehiculos.get(getAdapterPosition()).getId().toString());
            context.startActivity(intent);
        }
    }

}
