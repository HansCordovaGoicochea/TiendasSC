package com.scientechperu.pideloya.Fragmentos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scientechperu.pideloya.Adaptadores.VehiculosAdapter;
import com.scientechperu.pideloya.Clases.DateInputMask;
import com.scientechperu.pideloya.Clases.Usuario;
import com.scientechperu.pideloya.Clases.Vehiculos;
import com.scientechperu.pideloya.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentEstacionamiento.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FragmentEstacionamiento extends Fragment {

    public static final String ARG_SECTION_TITLE = "Mis Vehículos";
    private static final String TAG = FragmentEstacionamiento.class.getSimpleName();
    public static final String ARG_SECTION_URL = "";

    RecyclerView recyclerView;
    VehiculosAdapter rcAdapter;
    private List<Vehiculos> list_vehiculos = Vehiculos.listAll(Vehiculos.class);
    AlertDialog alertDialog;

    EditText placa, marca, modelo, tipo_vehiculo, fecha_adquisicion, color, tipo_combustible;
    Button agregar_vehiculo;

    SharedPreferences prefs;
    Integer idtienda_current;
    Integer idcaja_current;
    Usuario usuario;

    public FragmentEstacionamiento() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(ARG_SECTION_TITLE);
//        Log.e(TAG, "resumen PANTALLA");

        // To load the data at a later time
        prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        idtienda_current = prefs.getInt("id_shop", 0);
        idcaja_current = prefs.getInt("id_caja", 0);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

// You can hide the state of the menu item here if you call getActivity().supportInvalidateOptionsMenu(); somewhere in your code
        MenuItem carro = menu.findItem(R.id.action_shop);
        carro.setVisible(false);


    }

    public static FragmentEstacionamiento newInstance(String sectionTitle) {
        FragmentEstacionamiento fragment = new FragmentEstacionamiento();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, sectionTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_estacionamiento, container, false);

        //initialize recyclerview
        recyclerView = view.findViewById(R.id.vehiculos_recycler_view);
        //fixed size of recyclerview layout
        recyclerView.setHasFixedSize(true);
        //initialize linear layout manager horizontally
        LinearLayoutManager linearHorizontal = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        //attach linear layout to recyclerview
        recyclerView.setLayoutManager(linearHorizontal);
        //initialize adapter
        rcAdapter = new VehiculosAdapter(getContext(), list_vehiculos);
        //attach adapter to recyclerview
        recyclerView.setAdapter(rcAdapter);


        FloatingActionButton fab =  view.findViewById(R.id.agregar_vehiculo);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(getContext(), "Cdsfsfdsfds", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setCancelable(false);
                View view_dialog = getLayoutInflater().inflate(R.layout.dialog_vehiculo, null);
                alertDialogBuilder.setView(view_dialog);
                alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                TextView txtclose = view_dialog.findViewById(R.id.txtclose);

                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                placa = view_dialog.findViewById(R.id.input_placa);
//                placa.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                marca = view_dialog.findViewById(R.id.input_marca);
                modelo = view_dialog.findViewById(R.id.input_modelo);
                tipo_vehiculo = view_dialog.findViewById(R.id.input_tipo);
                fecha_adquisicion = view_dialog.findViewById(R.id.input_fecha_adquisicion);
                color = view_dialog.findViewById(R.id.input_color);
                tipo_combustible = view_dialog.findViewById(R.id.input_tipo_combustible);

                //marcada de fecha mes/anio
                new DateInputMask(fecha_adquisicion);

                agregar_vehiculo = view_dialog.findViewById(R.id.agregar_vehiculo);
                agregar_vehiculo.setEnabled(true);

                agregar_vehiculo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "Signup");
                        agregar_vehiculo.setEnabled(false);

                        if (!validate()) {
                            onSignupFailed();
                            return;
                        }
                        String[] vals = {
                                String.valueOf(idtienda_current)
                        };

                        List<Usuario> usuarios = Usuario.find(Usuario.class, "idshop = ?", vals);

                        if (usuarios.size() > 0) {
                            usuario = usuarios.get(0);
                            String id_customer = usuario.getIdcustomer();
                            String _placa = placa.getText().toString();
                            String _modelo = modelo.getText().toString();
                            String _marca = marca.getText().toString();
                            String _tipo_vehiculo = tipo_vehiculo.getText().toString();
                            String _fecha_adquisicion = fecha_adquisicion.getText().toString();
                            String _color = color.getText().toString();
                            String _tipo_combustible = tipo_combustible.getText().toString();
                            Boolean estado = false;

                            Vehiculos vehiculo = new Vehiculos();
                            vehiculo.setIdcustomer(id_customer);
                            vehiculo.setPlaca(_placa);
                            vehiculo.setModelo(_modelo);
                            vehiculo.setMarca(_marca);
                            vehiculo.setTipo_vehiculo(_tipo_vehiculo);
                            vehiculo.setFecha_adquisicion(_fecha_adquisicion);
                            vehiculo.setColor(_color);
                            vehiculo.setTipo_combustible(_tipo_combustible);
                            vehiculo.setId_shop(idtienda_current.toString());
                            vehiculo.setEstado(estado);
                            vehiculo.save();

                            list_vehiculos.clear();
                            List<Vehiculos> list_vehiculos_nuevo = Vehiculos.listAll(Vehiculos.class);
                            list_vehiculos.addAll(list_vehiculos_nuevo);
                            rcAdapter.notifyDataSetChanged();

                            alertDialog.dismiss();

                            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Exito!")
                                    .setContentText("Vehiculo Creado")
                                    .show();

                        } else {
                            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Registrese!")
                                    .setContentText("Registrese en el formulario gracias")
                                    .show();

                            //aca mostrar el registro del usuario

                            return;
                        }
                    }
                });
            }
        });



        return view;
    }


    public void onSignupFailed() {
        Toast.makeText(getActivity().getBaseContext(), "Llene los datos correctamente", Toast.LENGTH_LONG).show();
        agregar_vehiculo.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String _placa = placa.getText().toString();
        String _modelo = modelo.getText().toString();
        String _marca = marca.getText().toString();

        if (_placa.isEmpty() || _placa.length() < 4) {
            placa.setError("Al menos 4 caracteres");
            valid = false;
        } else {
            placa.setError(null);
        }

        if (_modelo.isEmpty() || _modelo.length() < 2) {
            modelo.setError("Al menos 2 caracteres");
            valid = false;
        } else {
            modelo.setError(null);
        }

        if (_marca.isEmpty() || _marca.length() < 2) {
            marca.setError("Al menos 2 caracteres");
            valid = false;
        } else {
            marca.setError(null);
        }


        return valid;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
