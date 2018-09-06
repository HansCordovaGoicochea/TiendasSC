package com.scientechperu.tiendassc.Fragmentos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.scientechperu.tiendassc.Adaptadores.RecyclerAdapterProductos;
import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Productos;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Clases.Usuario;
import com.scientechperu.tiendassc.Entendiendo.EndlessRecyclerViewScrollListener;
import com.scientechperu.tiendassc.R;
import com.scientechperu.tiendassc.SingletonVolley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.scientechperu.tiendassc.Splash.isOnline;


/**
 * Fragmento para el contenido principal
 */
public class FragmentProductos extends Fragment{
    /**
     * Este argumento del fragmento representa el título de cada
     * sección
     */
    private static final String TAG = FragmentProductos.class.getSimpleName();
    public static final String ARG_SECTION_TITLE = "Productos";
    public static final String ARG_SECTION_URL = "productos_url";

    private List<Productos> ProductsList = new ArrayList<>();
    Tienda tienda;
    Usuario usuario;

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
    private SharedPreferences perfilUsuarioshared;
    /**
     * Crea una instancia prefabricada de {@link FragmentProductos}
     *
     * @param sectionTitle Título usado en el contenido
     * @return Instancia dle fragmento
     */
    Integer idtienda_current;

    AlertDialog alertDialog;

    private Button ingresar;
    private EditText nombres_usuario;
    private EditText apellidos_usuario;
    private EditText email_usuario;
    private EditText celular_usuario;
    private EditText direccion_usuario;
    ProgressDialog progressDialog;
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
    public void onStart() {
        super.onStart();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setCancelable(false);
        View login = getLayoutInflater().inflate(R.layout.login, null);
        alertDialogBuilder.setView(login);
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.setCanceledOnTouchOutside(false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registrando...");
        progressDialog.setCancelable(false);

        sharedPref = getActivity().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        idtienda_current = sharedPref.getInt("id_shop", 0);

        perfilUsuarioshared = getActivity().getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);


        if (perfilUsuarioshared.contains("_nombre")){
            String[] vals = {
                    String.valueOf(idtienda_current)
            };
            if((int)Usuario.count(Usuario.class, "idshop = ?", vals) == 0){
                //guardar datos en la web
                String nombreshared = perfilUsuarioshared.getString("_nombre", null);
                String apellidoshared = perfilUsuarioshared.getString("_apellidos", null);
                String emailshared = perfilUsuarioshared.getString("_email", null);
                String celularshared = perfilUsuarioshared.getString("_celular", null);
                String direccionshared = perfilUsuarioshared.getString("_direccion", null);

                xmlSendCliente(nombreshared, apellidoshared, emailshared, celularshared, direccionshared);
            }
        }else{
            alertDialog.show();
            nombres_usuario = login.findViewById(R.id.input_name);
            apellidos_usuario = login.findViewById(R.id.input_apellidos);
            email_usuario = login.findViewById(R.id.input_email);
            celular_usuario = login.findViewById(R.id.input_telefono);
            direccion_usuario = login.findViewById(R.id.input_direccion);

//            nombres_usuario.setText("Hans");
//            apellidos_usuario.setText("Cordova g");
//            email_usuario.setText("sadsa@asdsa.com");
//            celular_usuario.setText("112321321");
//            direccion_usuario.setText("ghgfhgfh 5656");

            ingresar = (Button) login.findViewById(R.id.btn_signup);
            ingresar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    signup();

                }
            });
        }
    }



    public void signup() {
        Log.d(TAG, "Signup");
        SharedPreferences.Editor editor = perfilUsuarioshared.edit();

        if (!validate()) {
            onSignupFailed();
            return;
        }

        ingresar.setEnabled(false);

        progressDialog.show();

        String _nombre = nombres_usuario.getText().toString();
        String _apellidos = apellidos_usuario.getText().toString();
        String _email = email_usuario.getText().toString();
        String _celular = celular_usuario.getText().toString();
        String _direccion = direccion_usuario.getText().toString();

        // TODO: Implement your own signup logic here.


        editor.putString("_nombre", _nombre);
        editor.putString("_apellidos", _apellidos);
        editor.putString("_email", _email);
        editor.putString("_celular", _celular);
        editor.putString("_direccion", _direccion);
        editor.apply();

//
        //guardar datos en la web
        xmlSendCliente(_nombre, _apellidos, _email, _celular, _direccion);
        alertDialog.dismiss();
    }



    public void onSignupFailed() {
        Toast.makeText(getActivity().getBaseContext(), "Llene los datos correctamente", Toast.LENGTH_LONG).show();
        ingresar.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String _nombre = nombres_usuario.getText().toString();
        String _apellidos = apellidos_usuario.getText().toString();
        String _email = email_usuario.getText().toString();
        String _celular = celular_usuario.getText().toString();
        String _direccion = direccion_usuario.getText().toString();

        if (_nombre.isEmpty() || _nombre.length() < 2) {
            nombres_usuario.setError("Al menos 2 caracteres");
            valid = false;
        } else {
            nombres_usuario.setError(null);
        }

        if (_apellidos.isEmpty() || _apellidos.length() < 3) {
            apellidos_usuario.setError("Al menos 5 caracteres");
            valid = false;
        } else {
            apellidos_usuario.setError(null);
        }

        if (_email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email_usuario.setError("ingrese un email valido");
            valid = false;
        } else {
            email_usuario.setError(null);
        }
        if (_celular.isEmpty() || !Patterns.PHONE.matcher(_celular).matches() || _celular.length() < 8 || _celular.length() > 10) {
            celular_usuario.setError("Numero de celular invalido");
            valid = false;
        } else {
            celular_usuario.setError(null);
        }

        if (_direccion.isEmpty() || _direccion.length() < 6) {
            direccion_usuario.setError("Al menos 6 caracteres");
            valid = false;
        } else {
            direccion_usuario.setError(null);
        }


        return valid;
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
                fragmentTransaction.replace(R.id.contenedor_principal, fragment, "FragmentCarrito");
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


    public void xmlSendCliente(final String nombres, final String apellidos, final String email, final String celular, final String direccion){



        if (sharedPref.contains("id_shop")) {


            List<Tienda> tiendas = Tienda.find(Tienda.class, "idshop = ?", ""+idtienda_current);
            tienda = tiendas.get(0);

            String url = UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/customers/" + UrlRaiz.ws_key + "&schema=blank";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
//                Log.e(TAG, response.toString());
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    Document dom = null;
                    try {
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        StringReader reader = new StringReader(response);
                        InputSource source = new InputSource(reader);
                        dom = builder.parse(source);
                    } catch (ParserConfigurationException e) {
                    } catch (SAXException e) {
                    } catch (IOException e) {
                    }
                    if (dom != null) {
                        Element root = dom.getDocumentElement();
                        NodeList idItem = root.getElementsByTagName("id");
                        NodeList nombres = root.getElementsByTagName("firstname");
                        NodeList apellidos = root.getElementsByTagName("lastname");
                        NodeList email = root.getElementsByTagName("email");
                        NodeList address = root.getElementsByTagName("address");
                        NodeList phone = root.getElementsByTagName("phone");
                        NodeList id_shop = root.getElementsByTagName("id_shop");

                        String id = idItem.item(0).getChildNodes().item(0).getNodeValue();
                        String nombres_db = nombres.item(0).getChildNodes().item(0).getNodeValue();
                        String apellidos_db = apellidos.item(0).getChildNodes().item(0).getNodeValue();
                        String email_db = email.item(0).getChildNodes().item(0).getNodeValue();
                        String phone_db = phone.item(0).getChildNodes().item(0).getNodeValue();
                        String address_db = address.item(0).getChildNodes().item(0).getNodeValue();
                        String id_shop_db = id_shop.item(0).getChildNodes().item(0).getNodeValue();


                        Usuario usuario = new Usuario(id, nombres_db, apellidos_db, email_db, phone_db, address_db, id_shop_db);
                        usuario.save();
//                    SharedPreferences.Editor editor = perfilUsuarioshared.edit();
//                    editor.putString("_idcustomer", id);
//                    editor.apply();

//                    Toast.makeText(getActivity().getBaseContext(), id, Toast.LENGTH_LONG).show();

                    }


                    progressDialog.dismiss();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(TAG, "Error: " + error.getMessage());
                    progressDialog.dismiss();
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/xml; charset=" + getParamsEncoding();
                }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    String postData = generaXMLCliente(nombres, apellidos, email, celular, direccion); // TODO get your final output
                    Log.e(TAG, postData);
                    try {
                        return postData == null ? null :
                                postData.getBytes(getParamsEncoding());
                    } catch (UnsupportedEncodingException uee) {
                        // TODO consider if some other action should be taken
                        return null;
                    }
                }
            };

                   /*Se definen las políticas para la petición realizada. Recibe como argumento una instancia de la clase
        DefaultRetryPolicy, que recibe como parámetros de entrada el tiempo inicial de espera para la respuesta,
        el número máximo de intentos, y el multiplicador de retardo de envío por defecto.*/
            strReq.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /*Se declara e inicializa una variable de tipo RequestQueue, encargada de crear
        una nueva petición en la cola del servicio web.*/
        MySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
        }
    }

    public String generaXMLCliente(String firstname, String lastname, String email, String celular, String direccion) {
        String url = UrlRaiz.domain+"/"+tienda.getVirtual_uri()+"api/";

        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<customer>\n" +
                "\t<id></id>\n" +
                "\t<id_default_group xlink:href=\""+url+"groups/3\">3</id_default_group>\n" +
                "\t<id_lang xlink:href=\""+url+"languages/2\">2</id_lang>\n" +
                "\t<newsletter_date_add></newsletter_date_add>\n" +
                "\t<ip_registration_newsletter></ip_registration_newsletter>\n" +
                "\t<last_passwd_gen></last_passwd_gen>\n" +
                "\t<secure_key>a298fc1739f88a4679a4eac1a16aab43</secure_key>\n" +
                "\t<deleted>0</deleted>\n" +
                "\t<passwd>12345678</passwd>\n" +
                "\t<firstname>%s</firstname>\n" +
                "\t<lastname>%s</lastname>\n" +
                "\t<email>%s</email>\n" +
                "\t<id_gender>1</id_gender>\n" +
                "\t<birthday></birthday>\n" +
                "\t<newsletter>0</newsletter>\n" +
                "\t<optin>0</optin>\n" +
                "\t<website></website>\n" +
                "\t<company></company>\n" +
                "\t<siret></siret>\n" +
                "\t<ape></ape>\n" +
                "\t<outstanding_allow_amount>0.000000</outstanding_allow_amount>\n" +
                "\t<show_public_prices>0</show_public_prices>\n" +
                "\t<id_risk>0</id_risk>\n" +
                "\t<max_payment_days>0</max_payment_days>\n" +
                "\t<active>1</active>\n" +
                "\t<note></note>\n" +
                "\t<is_guest>0</is_guest>\n" +
                "\t<id_shop></id_shop>\n" +
                "\t<id_shop_group></id_shop_group>\n" +
                "\t<date_add></date_add>\n" +
                "\t<date_upd></date_upd>\n" +
                "\t<phone>%s</phone>" +
                "\t<address>%s</address>\n" +
                "<associations></associations>\n" +
                "</customer>\n" +
                "</prestashop>";

        return String.format(format, firstname, lastname, email, celular, direccion);
    }
}