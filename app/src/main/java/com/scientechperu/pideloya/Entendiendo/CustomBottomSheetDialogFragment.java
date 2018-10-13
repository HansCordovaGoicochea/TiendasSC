package com.scientechperu.pideloya.Entendiendo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.scientechperu.pideloya.Clases.Carro;
import com.scientechperu.pideloya.Clases.Productos;
import com.scientechperu.pideloya.R;
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

    public TextView badge;
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

            badge = getActivity().findViewById(R.id.cart_badge);


            // Set typeface
            cant.setTypeface(Typeface.create(getString(R.string.roboto_light), Typeface.NORMAL));

            producto.setText(item.getName());
            precio.setText("S/ "+item.getPrecio_con_igv());


//            Toast.makeText(getContext(), "num "+badge.getText(), Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        pasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                //                    Toast toast= Toast.makeText(getContext(), "¡Tienes "+cant.getValue()+" "+item.getName(), Toast.LENGTH_SHORT);

//                Toast.makeText(getContext(), "num "+badge.getText(), Toast.LENGTH_SHORT).show();
                //actualizar el icono del carrito
                // To load the data at a later time
                SharedPreferences prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
                Integer idtienda_current = prefs.getInt("id_shop", 0);
                Integer idcaja_current = prefs.getInt("id_caja", 0);


                List<Carro> carros = Carro.find(Carro.class, "idproducto = ?", item.getId_product().toString());
                if (carros.size() > 0) {


                    Carro carro = carros.get(0);

                    Integer cantidad_old = carro.getCantidad();
                    Integer cantidad_new = cantidad_old + cant.getValue();

                    carro.setCantidad(cantidad_new);
                    Double importe = (cantidad_new * Double.parseDouble(item.getPrecio_con_igv()));
                    carro.setImporte(importe);
                    carro.save();

//                    Toast.makeText(getContext(), "¡Tienes "+cantidad_new+" "+item.getName()+"!", Toast.LENGTH_SHORT).show();
                    Toast toast= Toast.makeText(getContext(), "¡Tienes "+cantidad_new+" "+item.getName()+"!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 60);
                    toast.show();

                }else{
                    Double importe = (cant.getValue() * Double.parseDouble(item.getPrecio_con_igv()));

                    Carro carro = new Carro();
                    carro.setId_cart(UUID.randomUUID().toString());
                    carro.setId_producto(item.getId_product().toString());
                    carro.setCantidad(cant.getValue());
                    carro.setNombre_producto(item.getName());
                    carro.setPrecio_producto(Double.valueOf(item.getPrecio_con_igv()));
                    carro.setId_image(item.getId_image());
                    carro.setImporte(importe);
                    carro.setId_shop(Integer.valueOf(item.getId_shop_default()));
                    carro.setId_caja(idcaja_current);
                    carro.save();

//                    Toast.makeText(getContext(), "¡Tienes "+cant.getValue()+" "+item.getName(), Toast.LENGTH_SHORT).show();
                    Toast toast= Toast.makeText(getContext(), "¡Tienes "+cant.getValue()+" "+item.getName(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
                    toast.show();
                }



                String[] vals = {
                        String.valueOf(idtienda_current),
                        String.valueOf(idcaja_current)
                };
                int mCartItemCount = (int) Carro.count(Carro.class, "idshop = ? and idcaja = ?", vals);
                if (mCartItemCount < 0){
                    mCartItemCount = 0;
                }
                badge.setText(String.valueOf(mCartItemCount));

                dismiss();
            }
        });
    }
}