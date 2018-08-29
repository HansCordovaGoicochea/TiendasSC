package com.scientechperu.tiendassc.Fragmentos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scientechperu.tiendassc.Adaptadores.GridAdapter;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.R;

import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Fragmento para el contenido principal
 */
public class PlaceholderFragment extends Fragment {
    /**
     * Este argumento del fragmento representa el título de cada
     * sección
     */
    public static final String ARG_SECTION_TITLE = "section_number";


    /**
     * Crea una instancia prefabricada de {@link PlaceholderFragment}
     *
     * @param sectionTitle Título usado en el contenido
     * @return Instancia dle fragmento
     */
    public static PlaceholderFragment newInstance(String sectionTitle) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, sectionTitle);
        fragment.setArguments(args);
        return fragment;
    }


    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.section_fragment, container, false);


        // Ubicar argumento en el text view de section_fragment.xml
        String title = getArguments().getString(ARG_SECTION_TITLE);
//        TextView titulo = (TextView) view.findViewById(R.id.title);
//        titulo.setText(title);

        // Obtención del grid view
        GridViewWithHeaderAndFooter grid =
                (GridViewWithHeaderAndFooter) view.findViewById(R.id.gridview);

        // Inicializar el grid view
//        setUpGridView(grid);

        List<Tienda> arrayTienda = Tienda.find(Tienda.class, "categoria = ?", title);

        grid.setAdapter(new GridAdapter(getActivity(), arrayTienda));


        return view;
    }

}