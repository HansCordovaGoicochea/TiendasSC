package com.scientechperu.tiendassc.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.scientechperu.tiendassc.Adaptadores.PedidoAdapter;
import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Carroprincipal;
import com.scientechperu.tiendassc.Clases.DetalleOrder;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Clases.Usuario;
import com.scientechperu.tiendassc.R;
import com.scientechperu.tiendassc.SingletonVolley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FragmentPedido extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_SECTION_TITLE = "Ultimo Pedido";
    private static final String TAG = FragmentPedido.class.getSimpleName();
    TextView textCartItemCount;
    SharedPreferences prefs;
    Tienda tienda;
    public TextView cantidad_productos;
    public TextView total;
    public TextView subtotal;
    public TextView igv;

    RecyclerView recyclerView;
    PedidoAdapter rcAdapter;
    private ArrayList<DetalleOrder> detalleOrders = new ArrayList<>();
    private ProgressDialog pd;

    public FragmentPedido() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionTitle Parameter 1.
     * @return A new instance of fragment fragment_pedido.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPedido newInstance(String sectionTitle) {
        FragmentPedido fragment = new FragmentPedido();
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
        View view =  inflater.inflate(R.layout.fragment_pedido, container, false);

        //initialize recyclerview
        recyclerView = view.findViewById(R.id.reciclerview_productos);
        //fixed size of recyclerview layout
        recyclerView.setHasFixedSize(true);
        //initialize linear layout manager horizontally
        LinearLayoutManager linearHorizontal = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        //attach linear layout to recyclerview
        recyclerView.setLayoutManager(linearHorizontal);
        //initialize adapter
        rcAdapter = new PedidoAdapter(getContext(), detalleOrders);
        //attach adapter to recyclerview
        recyclerView.setAdapter(rcAdapter);


        // To load the data at a later time
        prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        final Integer idtienda_current = prefs.getInt("id_shop", 0);
        final Integer idcaja_current = prefs.getInt("id_caja", 0);

        final String[] argwheretienda = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current),
        };

        List<Tienda> tiendas = Tienda.find(Tienda.class, "idshop = ? and idcaja = ?", argwheretienda);
        tienda = tiendas.get(0);

        cantidad_productos = view.findViewById(R.id.txtCantidadProd);
        total = view.findViewById(R.id.txtTotal);
        subtotal = view.findViewById(R.id.txtSubtotal);
        igv = view.findViewById(R.id.txtIgv);

        cantidad_productos.setText("( 0 ) Productos pedidos");
        total.setText("S/ 0.00");
        subtotal.setText("S/ 0.00");
        igv.setText("S/ 0.00");

        //      HACER VERIFICACION SI LA ORDEN YA ESTA PAGADA CON EL CODIGO UNICO
        List<Carroprincipal> carroprincipals = Carroprincipal.find(Carroprincipal.class, "idshop = ?", ""+idtienda_current);
        if (carroprincipals.size() > 0) {
            Carroprincipal carroprincipal = carroprincipals.get(0);

            final String[] argusu = {
                    String.valueOf(idtienda_current)
            };
            List<Usuario> usuarios = Usuario.find(Usuario.class, "idshop = ?", argusu);
//            http://testsc.corporacioncatto.com/pollos-parrillas-elhawaiano/api/orders/?output_format=JSON&filter[id_customer]=320&display=full&sort=[id_DESC]&limit=1
//            jsonOrder(UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/orders/" + UrlRaiz.ws_key + "&output_format=JSON&filter[codigo_unico_app]="+carroprincipal.getCodigounico()+"&display=full");
            pd = ProgressDialog.show(getContext(),"Cargando...","Porfavor espere...");
            jsonOrder(UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/orders/" + UrlRaiz.ws_key + "&output_format=JSON&filter[id_customer]="+usuarios.get(0).getIdcustomer()+"&filter[id_caja]="+tienda.getId_caja()+"&display=full&sort=[id_DESC]&limit=1");
        }

        Button ir_productos = view.findViewById(R.id.btnPasar);

        ir_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getFragmentManager().popBackStackImmediate();
                FragmentProductos fr=new FragmentProductos();
                Bundle args = new Bundle();
                args.putString(FragmentProductos.ARG_SECTION_TITLE, "Productos");
                args.putString(FragmentProductos.ARG_SECTION_URL, UrlRaiz.domain+"/"+tienda.getVirtual_uri()+"api/products"+UrlRaiz.ws_key+"&output_format=JSON&filter[active]=1&filter[id_caja]="+tienda.getId_caja()+"&display=full&price[precio_con_igv][use_tax]=1");

                fr.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contenedor_principal,fr)
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        final MenuItem menuItem = menu.findItem(R.id.action_shop);
        View actionView = menuItem.getActionView();

        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge); // el texto de numero de productos

        // To load the data at a later time
        SharedPreferences prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        Integer idtienda_current = prefs.getInt("id_shop", 0);


        String[] vals = {
                String.valueOf(idtienda_current)
        };
        int mCartItemCount = (int) Carro.count(Carro.class, "idshop = ?", vals);
        if (mCartItemCount < 0){
            mCartItemCount = 0;
        }

        setupBadge(mCartItemCount);

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
    }

    public void setupBadge(int count) {

        if (textCartItemCount != null) {
            textCartItemCount.setText(String.valueOf(Math.min(count, 99)));
            if (textCartItemCount.getVisibility() != View.VISIBLE) {
                textCartItemCount.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shop:
                Bundle args = new Bundle();
                args.putString(FragmentCarrito.ARG_SECTION_TITLE, "Carrito");
                Fragment fragment = FragmentCarrito.newInstance("Carrito");
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                fragmentTransaction.replace(R.id.contenedor_principal, fragment, "FragmentCarrito");
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();


                return true;
        }
        return false;
    }

    //consultar order
    private void jsonOrder(String url) {
        Log.e(TAG, "Llego al json ORDER detalles");
        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            detalleOrders = new ArrayList<>();

//                            Log.e(TAG, response.toString());
                            JSONArray jsonMainNode = response.getJSONArray("orders");

                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            Integer id = jsonChildNode.optInt("id");
                            String current_state = jsonChildNode.optString("current_state");
                            String nro_mesa = jsonChildNode.optString("nro_mesa");
                            String es_deli = jsonChildNode.optString("es_delivery");
                            String codigo = jsonChildNode.optString("codigo_unico_app");
                            String total_paid_tax_incl = jsonChildNode.optString("total_paid_tax_incl");
                            String total_paid_tax_excl = jsonChildNode.optString("total_paid_tax_excl");

                            JSONObject jsonSecondaryNode = jsonChildNode.getJSONObject("associations");
                            JSONArray c = jsonSecondaryNode.getJSONArray("order_rows");

                            String buys="";

                            for(int j = 0; j < c.length(); j++){
                                //Log.e("Arreglo", ""+c.get(j));
                                JSONObject d = c.getJSONObject(j);
                                String id_detalle = d.optString("id");
                                String product_id = d.optString("product_id");
                                String product_quantity = d.optString("product_quantity");
                                String product_name = d.optString("product_name");
                                String product_reference = d.optString("product_reference");
                                String product_price = d.optString("product_price");
                                String unit_price_tax_incl = d.optString("unit_price_tax_incl");
                                String unit_price_tax_excl = d.optString("unit_price_tax_excl");
//                                buys +="*"+ d.optString("product_name")+"\n";
                                //buys+=c.getString(4)+"\n";
                                detalleOrders.add(new DetalleOrder(id_detalle, product_id, "0", product_quantity, product_name, product_reference,
                                        product_price,unit_price_tax_incl,unit_price_tax_excl));
                            }

                            double totalSum = Double.parseDouble(total_paid_tax_incl);
                            double subtotalSum = Double.parseDouble(total_paid_tax_excl);
                            double igvSum = totalSum - subtotalSum;

                            double roundOff = Math.round(totalSum * 100.0) / 100.0;
                            double roundOff2 = Math.round(subtotalSum * 100.0) / 100.0;
                            double roundOff3 = Math.round(igvSum * 100.0) / 100.0;

                            total.setText("S/ "+roundOff);
                            subtotal.setText("S/ "+roundOff2);
                            igv.setText("S/ "+roundOff3);



                            rcAdapter = new PedidoAdapter(getContext(), detalleOrders);
                            //attach adapter to recyclerview
                            recyclerView.setAdapter(rcAdapter);
                            rcAdapter.notifyDataSetChanged();

                            Log.e(TAG, buys);
                            pd.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                pd.dismiss();
//                Toast.makeText(getActivity(),"El servidor ha tardado demasiado tiempo en responder",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });


          /*Se definen las políticas para la petición realizada. Recibe como argumento una instancia de la clase
        DefaultRetryPolicy, que recibe como parámetros de entrada el tiempo inicial de espera para la respuesta,
        el número máximo de intentos, y el multiplicador de retardo de envío por defecto.*/
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /*Se declara e inicializa una variable de tipo RequestQueue, encargada de crear
        una nueva petición en la cola del servicio web.*/
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

}
