package com.scientechperu.tiendassc.Entendiendo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.R;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.List;
import java.util.UUID;


public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public static final String KEY_STRING = "KEY_STRING";
    private Productos item ;

    public NumberPicker cant;
    public TextView producto;
    public TextView precio;
    public Button pasar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);


        if (getArguments() != null) {

            item = (Productos) getArguments().getSerializable("producto");

//            Toast.makeText(getContext(), " Dentro "+ item.getName() , Toast.LENGTH_SHORT).show();
            //setear el nombre del producto y el precio

            producto = v.findViewById(R.id.txtProducto);
            precio = v.findViewById(R.id.txtPrecio);
            cant = v.findViewById(R.id.cantidad);
            pasar = v.findViewById(R.id.btnPasar);

            // Set typeface
            cant.setTypeface(Typeface.create(getString(R.string.roboto_light), Typeface.NORMAL));
//            cant.setTypeface(getString(R.string.roboto_light), Typeface.NORMAL);
//            cant.setTypeface(getString(R.string.roboto_light));
//            cant.setTypeface(R.string.roboto_light, Typeface.NORMAL);
//            cant.setTypeface(R.string.roboto_light);

            producto.setText(item.getName());
            precio.setText("S/ "+item.getPrecio_con_igv());


        }

//        Button btn1 = (Button)v.findViewById(R.id.btn1);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(),YourActivity.class));
//            }
//        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                List<Carro> carros = Carro.find(Carro.class, "idproducto = ?", item.getId_product().toString());
                if (carros.size() > 0) {


                    Carro carro = carros.get(0);
                    Integer cantidad_old = carro.getCantidad();
                    Integer cantidad_new = cantidad_old + cant.getValue();
                    carro.setCantidad(cantidad_new);
                    carro.save();

                    Toast.makeText(getContext(), "¡Tienes "+cantidad_new+" "+item.getName()+"!", Toast.LENGTH_SHORT).show();

                }else{
                    Carro carro = new Carro();
                    carro.setId_cart(UUID.randomUUID().toString());
                    carro.setId_producto(item.getId_product().toString());
                    carro.setCantidad(cant.getValue());
                    carro.setNombre_producto(item.getName());
                    carro.setPrecio_producto(Double.valueOf(item.getPrecio_con_igv()));
                    carro.setId_image(item.getId_image());
                    carro.save();

                    Toast.makeText(getContext(), "¡Tienes "+cant.getValue()+" "+item.getName(), Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });
    }
}