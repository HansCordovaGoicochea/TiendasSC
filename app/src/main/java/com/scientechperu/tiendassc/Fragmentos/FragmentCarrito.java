package com.scientechperu.tiendassc.Fragmentos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scientechperu.tiendassc.R;


public class FragmentCarrito extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_SECTION_TITLE = "section_number";


    public FragmentCarrito() {
        // Required empty public constructor
    }

    /**
     * Crea una instancia prefabricada de {@link FragmentCarrito}
     *
     * @param sectionTitle Título usado en el contenido
     * @return Instancia dle fragmento
     */
    public static FragmentCarrito newInstance(String sectionTitle) {
        FragmentCarrito fragment = new FragmentCarrito();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, sectionTitle);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_activividad_carrito, container, false);

        return view;
    }

}
