package com.scientechperu.tiendassc.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.scientechperu.tiendassc.Adaptadores.RecyclerAdapterProductos;
import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.Entendiendo.EndlessRecyclerViewScrollListener;
import com.scientechperu.tiendassc.R;
import com.scientechperu.tiendassc.SingletonVolley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.scientechperu.tiendassc.Splash.isOnline;


/**
 * Fragmento para el contenido principal
 */
public class FragmentProductos extends Fragment {
    /**
     * Este argumento del fragmento representa el título de cada
     * sección
     */
    private static final String TAG = FragmentProductos.class.getSimpleName();
    public static final String ARG_SECTION_TITLE = "Productos";
    public static final String ARG_SECTION_URL = "productos_url";

    private List<Productos> ProductsList = new ArrayList<>();

    private ProgressDialog pd;

    RecyclerAdapterProductos rcAdapter;
    public GridLayoutManager gridLayoutManager;
    public RecyclerView rView;

    public String URL = "";
    private EndlessRecyclerViewScrollListener scrollListener;
    public int curSize;
    private boolean isFirstCall = true;

    public TextView textCartItemCount;
    int mCartItemCount = 0;

    Fragment fragment = null;
    FragmentManager fragmentManager;

    private SharedPreferences sharedPref;
    /**
     * Crea una instancia prefabricada de {@link FragmentProductos}
     *
     * @param sectionTitle Título usado en el contenido
     * @return Instancia dle fragmento
     */
    public static FragmentProductos newInstance(String sectionTitle) {
        FragmentProductos fragment = new FragmentProductos();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, sectionTitle);
        fragment.setArguments(args);

        return fragment;
    }


    public FragmentProductos() {
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(ARG_SECTION_TITLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Instanciamos una referencia al Contexto
        Context context = this.getActivity();
        //Instanciamos el objeto SharedPreferences y creamos un fichero Privado bajo el
        //nombre definido con la clave preference_file_key en el fichero string.xml
        sharedPref = context.getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("cargado", false);
        editor.apply();


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
        mCartItemCount = (int) Carro.count(Carro.class, "idshop = ?", vals);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shop:
                Bundle args = new Bundle();
                args.putString(FragmentCarrito.ARG_SECTION_TITLE, "Carrito");
                fragment = FragmentCarrito.newInstance("Carrito");
                fragment.setArguments(args);

                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                fragmentTransaction.replace(R.id.contenedor_principal, fragment);
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();


                return true;
        }
        return false;
    }

    public void setupBadge(int count) {

        if (textCartItemCount != null) {
//            if (count == 0) {
//                textCartItemCount.setText(String.valueOf(Math.min(0, 99)));
//                if (textCartItemCount.getVisibility() != View.GONE) {
//                    textCartItemCount.setVisibility(View.GONE);
//                }
//            } else {
                textCartItemCount.setText(String.valueOf(Math.min(count, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.productos_fragment, container, false);

        // Ubicar argumento en el text view de section_fragment.xml
        String title = getArguments().getString(ARG_SECTION_TITLE);
        URL = getArguments().getString(ARG_SECTION_URL);

//        Toast.makeText(getContext(), URL, Toast.LENGTH_SHORT).show();

        // Obtención del grid view
        rView = (RecyclerView)view.findViewById(R.id.recycler_view);
        rcAdapter = new RecyclerAdapterProductos(getActivity(), ProductsList);
        rView.setAdapter(rcAdapter);
//        gridLayoutManager = new AutoFitGridLayoutManager(getActivity(), 250);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gridLayoutManager);

        // Inicializar el grid view

        if(isOnline(getContext())){
            // To load the data at a later time
            SharedPreferences prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
            Boolean loadedString = prefs.getBoolean("cargado", false);

            // primera peticion al servidor
            if (isFirstCall && !loadedString) {
                pd = ProgressDialog.show(getContext(),"Cargando...","Porfavor espere...");
                isFirstCall = false;
                jsonParse(URL+"&sort=[id_ASC]&limit=0,10");
//                Toast.makeText(getActivity(),"Descargando Productos",Toast.LENGTH_SHORT).show();
            }


            scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    if (!isFirstCall) {

                        jsonParse(URL+"&sort=[id_ASC]&limit="+ProductsList.size()+",10");

                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                // Notify adapter with appropriate notify methods
                                rcAdapter.notifyItemRangeInserted(curSize, ProductsList.size() -1);
                            }
                        });
                    }
                }
            };

            // Adds the scroll listener to RecyclerView
            rView.addOnScrollListener(scrollListener);

        }else{
            Toast.makeText(getActivity(),"No hay conexión a internet",Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private void jsonParse(String url) {


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Productos> moreArticles = new ArrayList<>();

                            JSONArray jsonMainNode = response.getJSONArray("products");

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                JSONArray NameNode = jsonChildNode.optJSONArray("name"); // Parsing the "name" node
                                JSONObject NameObject = NameNode.getJSONObject(1);       // to extract the "value"
                                String name = NameObject.optString("value");            // from the node's element
                                Integer id_product = jsonChildNode.optInt("id");
                                Integer id_image = jsonChildNode.optInt("id_default_image");
                                JSONArray DescriptionNode = jsonChildNode.optJSONArray("description"); // Parsing the "description" node
                                JSONObject DescriptionObject = DescriptionNode.getJSONObject(1);       // to extract the "value"
                                String description_short  = DescriptionObject.optString("value");            // from the node's element
                                //String description_short = jsonChildNode.optString("description_short");
                                //String description = jsonChildNode.optString("description");
                                description_short=description_short.replaceAll("<p>","");
                                description_short=description_short.replaceAll("</p>","");
                                String description= description_short;
                                JSONArray SDescriptionNode = jsonChildNode.optJSONArray("description_short"); // Parsing the "description_short" node
                                JSONObject SDescriptionObject = SDescriptionNode.getJSONObject(1);       // to extract the "value"
                                description_short  = SDescriptionObject.optString("value");            // from the node's element
                                description_short=description_short.replaceAll("<p>","");
                                description_short=description_short.replaceAll("</p>","");
                                if (description_short.isEmpty()) description_short=description;
                                Double price = jsonChildNode.optDouble("price");
                                String id_supplier = jsonChildNode.optString("id_supplier");
                                String reference = jsonChildNode.optString("reference");
                                String activo= ""+jsonChildNode.optString("active");
                                Double precio_con_igv= jsonChildNode.optDouble("precio_con_igv");
                                if(activo.equalsIgnoreCase("1"))
                                    activo="Yes";
                                else
                                    activo="No";
                                String available = jsonChildNode.optString("available_now");

                                String id_shop_default = jsonChildNode.optString("id_shop_default");

                                ProductsList.add(new Productos(id_product, name, id_image, description_short, description, price,
                                        id_supplier,reference,activo,available, precio_con_igv, id_shop_default));
//                                Log.e("Producto", ProductsList.get(i).getName());
                            }
                            //Poblar el gridview
                            curSize = rcAdapter.getItemCount();
                            ProductsList.addAll(moreArticles);
                            rcAdapter.notifyItemRangeInserted(curSize, ProductsList.size() -1);
                            pd.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // remove last loading
                rcAdapter.enableFooter(false);
//                ProductsList.remove(ProductsList.size() -1);
                rcAdapter.notifyItemRemoved(ProductsList.size());

                Log.e(TAG, "Ultima pagina");
                Log.e(TAG, "Error Volley: " + error.toString());
                Toast.makeText(getActivity(),"El servidor ha tardado demasiado tiempo en responder",Toast.LENGTH_SHORT).show();
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