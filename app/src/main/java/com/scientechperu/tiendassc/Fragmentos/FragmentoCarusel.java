package com.scientechperu.tiendassc.Fragmentos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scientechperu.tiendassc.Adaptadores.CaruselAdapter;
import com.scientechperu.tiendassc.Adaptadores.CuponesAdapter;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.R;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

public class FragmentoCarusel extends Fragment implements DiscreteScrollView.OnItemChangedListener {

    private List<Tienda> data = Tienda.listAll(Tienda.class);
    private Tienda tienda;

    private TextView txtCliente;
    private TextView currentItemName;
    private TextView currentItemPrice;

    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;

    RecyclerView expanderRecyclerView;

    private SharedPreferences perfilUsuarioshared;
    private String dnishared;
    private String nombreshared;
    private String apellidoshared;

    public FragmentoCarusel() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // You can hide the state of the menu item here if you call getActivity().supportInvalidateOptionsMenu(); somewhere in your code
        MenuItem menuItem = menu.findItem(R.id.action_shop);
        MenuItem buscar = menu.findItem(R.id.action_buscar);
        menuItem.setVisible(false);
        buscar.setVisible(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carousel, container, false);

        currentItemName = (TextView) view.findViewById(R.id.item_name);
        currentItemPrice = (TextView) view.findViewById(R.id.item_price);

        txtCliente = (TextView) view.findViewById(R.id.txt_cliente);

        perfilUsuarioshared = getActivity().getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);

        txtCliente.setText("");

        if (perfilUsuarioshared.contains("_dni")){
            dnishared = perfilUsuarioshared.getString("_dni", null);
            nombreshared = perfilUsuarioshared.getString("_nombre", null);
            apellidoshared = perfilUsuarioshared.getString("_apellidos", null);


            String nombre = nombreshared.substring(0,1).toUpperCase();
            String apellidos = apellidoshared.substring(0,1).toUpperCase() + apellidoshared.substring(1);

            txtCliente.setText(nombre+", "+apellidos+" - "+dnishared);

        }



        itemPicker = (DiscreteScrollView) view.findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new CaruselAdapter(data));
        itemPicker.setAdapter(infiniteAdapter);
//        itemPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime());
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

//        onItemChanged(data.get(0));


        expanderRecyclerView = view.findViewById(R.id.recyclerView_cupones);
        initiateExpander();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (data.size() > 0)
            onItemChanged(data.get(0));
    }

    private void onItemChanged(Tienda item) {
        currentItemName.setText(item.getNombre());
        currentItemPrice.setText(item.getDireccion());
    }


    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        int positionInDataSet = infiniteAdapter.getRealPosition(position);
        onItemChanged(data.get(positionInDataSet));
    }

    private void initiateExpander() {

//        ArrayList<String> parentList = new ArrayList<>();
//
//        ArrayList<ArrayList> childListHolder = new ArrayList<>();


//        parentList.add("Fruits & Vegetables");
//        parentList.add("Beverages & Health");
//        parentList.add("Home & Kitchen");
//
//        ArrayList<String> childNameList = new ArrayList<>();
//        childNameList.add("Apple");
//        childNameList.add("Mango");
//        childNameList.add("Banana");
//
//        childListHolder.add(childNameList);
//
//        childNameList = new ArrayList<>();
//        childNameList.add("Red bull");
//        childNameList.add("Maa");
//        childNameList.add("Horlicks");
//
//        childListHolder.add(childNameList);
//
//        childNameList = new ArrayList<>();
//        childNameList.add("Knife");
//        childNameList.add("Vessels");
//        childNameList.add("Spoons");
//
//        childListHolder.add(childNameList);
//
//        CuponesAdapter cuponesAdapter =
//                new CuponesAdapter(getContext(), data, childListHolder);

        CuponesAdapter cuponesAdapter = new CuponesAdapter(getContext(), data);

        expanderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expanderRecyclerView.setAdapter(cuponesAdapter);
    }
}
