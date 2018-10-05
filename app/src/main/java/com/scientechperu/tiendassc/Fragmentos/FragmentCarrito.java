package com.scientechperu.tiendassc.Fragmentos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.scientechperu.tiendassc.Adaptadores.RecyclerAdapterCarrito;
import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Carroprincipal;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Clases.Usuario;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.scientechperu.tiendassc.Splash.isOnline;
//import static com.scientechperu.tiendassc.Fragmentos.FragmentProductos.;

public class FragmentCarrito extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_SECTION_TITLE = "Carrito";
    private static final String TAG = FragmentCarrito.class.getSimpleName();
    public static final String ARG_SECTION_URL = "productos_url";
    private static Cursor cursor;
    public String URL = "";

    public RecyclerView recyclerView;
    RecyclerAdapterCarrito rcAdapter;

    private List<Carro> CarroList = new ArrayList<>();
//    private List<Carro> CarroList = Carro.listAll(Carro.class);
//    private List<Carro> CarroList = Carro.find(Carro.class, "idshop = ?", vals);


    public TextView cantidad_productos;
    public TextView total;
    public TextView subtotal;
    public TextView igv;
    Tienda tienda;

    String id_address = "0";
    String id_customer = "4"; // el cliente 4 es prueba

    //        viene_de = 1 es App-delivery 2 es App-mesa
//        es_delivery = 1 si 0 no
    String nro_mesa = "0", viene_de = "", es_delivery = "", direccion_delivery = "", codigo_unico_app = "";
    boolean esta_en_local = false;

    AlertDialog alertDialog;

    TextView textCartItemCount;
    int mCartItemCount = 0;

    SharedPreferences prefs;

    private static final int REQUEST_CALL_PHONE = 1;

    private static final int WHATSAPP_NUMBER = 1;

    private GoogleMap gmap;
    private LatLng exact_location;

    LocationManager locationManager;

    double longitudeBest, latitudeBest;

    // A class instance
    private Handler mHandler = new Handler(Looper.getMainLooper());


    private FusedLocationProviderClient mFusedLocationClient;

    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    String _ID_cart = "0";

    String latitud_cliente = "";
    String longitud_cliente = "";

    Usuario usuario;

    Button realizar_pedido;
    Button ir_productos;
    double totalSum = 0.0;
    double subtotalSum = 0.0;
    double igvSum = 0.0;

    Integer idtienda_current;
    Integer idcaja_current;

    public FragmentCarrito() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // To load the data at a later time
        prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        idtienda_current = prefs.getInt("id_shop", 0);
        idcaja_current = prefs.getInt("id_caja", 0);


        String[] argwheretienda = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current),
        };


//        Tienda tienda = Tienda.findById(Tienda.class, idtienda_current);
        List<Tienda> tiendas = Tienda.find(Tienda.class, "idshop = ? and idcaja = ?", argwheretienda);
        tienda = tiendas.get(0);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        final MenuItem menuItem = menu.findItem(R.id.action_shop);
        View actionView = menuItem.getActionView();

        textCartItemCount = actionView.findViewById(R.id.cart_badge); // el texto de numero de productos

        String[] vals = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current)
        };
        mCartItemCount = (int) Carro.count(Carro.class, "idshop = ? and idcaja = ?", vals);
        if (mCartItemCount < 0){
            mCartItemCount = 0;
        }

        setupBadge(mCartItemCount);


    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(ARG_SECTION_TITLE);
        if (totalSum > 0)
            realizar_pedido.setEnabled(true);
        Log.e(TAG, "resumen PANTALLA");
    }

    @Override
    public void onStart() {
        super.onStart();
//      Log.e(TAG, "EMPEZANDO PANTALLA");

        String[] vals = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current)
        };

//      HACER VERIFICACION SI LA ORDEN YA ESTA PAGADA CON EL CODIGO UNICO PARA PODER BORRAR
        List<Carroprincipal> carroprincipals = Carroprincipal.find(Carroprincipal.class, "idshop = ? and idcaja = ?", vals);
        if (carroprincipals.size() > 0 && Integer.valueOf(carroprincipals.get(0).getNro_mesa()) > 0) {
            Carroprincipal carroprincipal = carroprincipals.get(0);
//            Log.e(TAG, "empieza el start "+carroprincipal.getCodigounico());
            jsonOrder(UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/orders/" + UrlRaiz.ws_key + "&output_format=JSON&filter[codigo_unico_app]="+carroprincipal.getCodigounico()+"&display=full", true);
        }


    }

    private void showAlert() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Activar Localización")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

// You can hide the state of the menu item here if you call getActivity().supportInvalidateOptionsMenu(); somewhere in your code
        MenuItem buscar = menu.findItem(R.id.action_buscar);
        buscar.setVisible(false);

        MenuItem pedido = menu.findItem(1);
        pedido.setVisible(true);
    }

    public static float round(double d, int decimalPlace)
    {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_activividad_carrito, container, false);


        final String[] vals = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current)
        };
        CarroList = Carro.find(Carro.class, "idshop = ? and idcaja = ?", vals);


//

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("cargado", true);
        editor.apply();

        // Ubicar argumento en el text view de section_fragment.xml
        String title = getArguments().getString(ARG_SECTION_TITLE);
        URL = getArguments().getString(ARG_SECTION_URL);

        //initialize recyclerview
        recyclerView = view.findViewById(R.id.reciclerview_productos);
//fixed size of recyclerview layout
        recyclerView.setHasFixedSize(true);
//initialize linear layout manager horizontally
        LinearLayoutManager linearHorizontal = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
//attach linear layout to recyclerview
        recyclerView.setLayoutManager(linearHorizontal);
//initialize adapter
        rcAdapter = new RecyclerAdapterCarrito(CarroList);
//attach adapter to recyclerview
        recyclerView.setAdapter(rcAdapter);

        cantidad_productos = view.findViewById(R.id.txtCantidadProd);
        total = view.findViewById(R.id.txtTotal);
        subtotal = view.findViewById(R.id.txtSubtotal);
        igv = view.findViewById(R.id.txtIgv);


        realizar_pedido = view.findViewById(R.id.btnPasar);

        ir_productos = view.findViewById(R.id.btnProductos);

        Integer cantidadP = 0;

        for (int i = 0; i < CarroList.size(); i++) {
            totalSum += CarroList.get(i).getImporte();
            cantidadP += CarroList.get(i).getCantidad();
        }
        if (totalSum > 0){
            subtotalSum = totalSum / 1.18;
            igvSum =  totalSum - subtotalSum;
            realizar_pedido.setEnabled(true);
        }else{
            realizar_pedido.setEnabled(false);
        }

        cantidad_productos.setText("(" + cantidadP + ") Productos en el detalle para pedir");
//        cantidad_productos.setText((int) Carro.count(Carro.class, "idshop = ?", vals)+" Productos para pedir");
//        Log.e(TAG, "S/"+roundTwoDecimals(totalSum));
//        Log.e(TAG, "S/"+roundTwoDecimals(subtotalSum));
//        Log.e(TAG, "S/"+roundTwoDecimals(igvSum));
//        Log.e(TAG, "--------------");
//        Log.e(TAG, "S/"+totalSum);
//        Log.e(TAG, "S/"+subtotalSum);
//        Log.e(TAG, "S/"+igvSum);
        double roundOff = Math.round(totalSum * 100.0) / 100.0;
        double roundOff2 = Math.round(subtotalSum * 100.0) / 100.0;
        double roundOff3 = Math.round(igvSum * 100.0) / 100.0;
//
//        total.setText("S/"+ roundTwoDecimals(totalSum));
//        subtotal.setText("S/"+roundTwoDecimals(subtotalSum));
//        igv.setText("S/"+roundTwoDecimals(igvSum));
        total.setText("S/"+ roundOff);
        subtotal.setText("S/"+roundOff2);
        igv.setText("S/"+roundOff3);


        realizar_pedido.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final boolean es_celular = isTelephone(getActivity());
//                Log.e(TAG, es_celular+"");

                realizar_pedido.setEnabled(false); //deshabilitar el boton
                if(isOnline(getContext())){
                    locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    boolean gps_enabled = false;
                    boolean network_enabled = false;

                    try {
                        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    } catch(Exception ex) {}

                    try {
                        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    } catch(Exception ex) {}

                    if(!gps_enabled && !network_enabled) {
                        showAlert();
                        realizar_pedido.setEnabled(true); //habilitar el boton
                        return;
                    }
//                    Log.e(TAG, "--------------");
//                    if (!checkLocation()){
//                        realizar_pedido.setEnabled(true); //habilitar el boton
//                        Log.e(TAG, "ERROR DE PERMsssssssISOS");
//                        return;
//                    }

                    final String[] filtro_idshop = {
                            String.valueOf(tienda.getId_shop()),
                            String.valueOf(tienda.getId_caja())
                    };


                    locationManager.removeUpdates(locationListenerGPS);
//                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    }
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "ERROR DE PERMISOS");
                    }

                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            location.getLatitude();
                            location.getLongitude();
//                            Log.e(TAG, "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });

                    Log.e(TAG, "Eassssssssssssss");

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    Log.e(TAG, "Eassssssssssssss 2");
                                    if (location != null) {
                                        // Logic to handle location object
                                        Log.e(TAG, round(location.getLatitude(), 4) + "");
                                        Log.e(TAG, round(location.getLongitude(), 5) + "");

                                        latitud_cliente = location.getLatitude() + "";
                                        longitud_cliente = location.getLongitude() + "";

                                        String[] vals = {
                                                String.valueOf(idtienda_current),
                                                String.valueOf(idcaja_current)
                                        };

                                        List<Carroprincipal> carroprincipals = Carroprincipal.find(Carroprincipal.class, "idshop = ? and idcaja = ?", vals);
                                        if (carroprincipals.size() > 0 && Integer.valueOf(carroprincipals.get(0).getNro_mesa()) > 0) {
                                            Carroprincipal carroprincipal = carroprincipals.get(0);
                                            codigo_unico_app = carroprincipal.getCodigounico();
                                            nro_mesa = carroprincipal.getNro_mesa();
                                            esta_en_local = true;

//                                            //verificar la mesa para poder registrar
//                                            jsonOrder(UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/orders/" + UrlRaiz.ws_key + "&output_format=JSON&filter[codigo_unico_app]="+carroprincipal.getCodigounico()+"&display=full");
                                            xmlSendCart();

                                        }else{
                                            codigo_unico_app = UUID.randomUUID().toString();

                                        //ubicacion del centro del punto
                                        exact_location = new LatLng(Double.parseDouble(tienda.getLatitud()), Double.parseDouble(tienda.getLongitud()));
//                                        exact_location = new LatLng(-7.182138, -78.501823);
//                                        exact_location = new LatLng(-7.182149, -78.501844);
                                        double lat = round(exact_location.latitude, 4); //getLatitude
                                        double lng = round(exact_location.longitude, 5);//getLongitude

                                        double lat_current = round(location.getLatitude(), 4); //getLatitude
                                        double lng_current = round(location.getLongitude(), 5);//getLongitude

                                        // aqui guardamos las distancias en tre los puntos
                                        float[] disResultado = new float[2];

                                        Location.distanceBetween(
                                                lat,
                                                lng,
                                                lat_current,
                                                lng_current,
                                                disResultado);

                                        Log.e("disresultado", Arrays.toString(disResultado));

                                        //verificar si estamos dentro o fuera del radio de ubicacion
                                        if((double)disResultado[0] > (double)20){
//                                           Log Fuera
                                            Log.e(TAG, "Fuera");
                                            //

                                            final SweetAlertDialog confirmarPedido = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
                                            confirmarPedido.setCancelable(true);
                                            confirmarPedido.setCanceledOnTouchOutside(false);
                                            confirmarPedido.setTitleText("¿Seguro de realizar el pedido?");

                                            confirmarPedido.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    realizar_pedido.setEnabled(true); //habilitar el boton
                                                    confirmarPedido.dismissWithAnimation();
                                                }
                                            });

                                            confirmarPedido.setConfirmButton("Si!", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    if (es_celular) {
                                                        ShowPopupWindow();// mostrar las pciones de llamada
                                                    }else{
                                                        Toast.makeText(getContext(), "No puedes realizar llamadas", Toast.LENGTH_SHORT).show();
                                                    }
                                                    confirmarPedido.dismissWithAnimation();
                                                }
                                            });

                                            confirmarPedido.show();
                                        } else {
//                                            Log Dentro
                                            Log.e(TAG, "Dentro");
                                            realizar_pedido.setEnabled(false); //deshabilitar el boton

                                            final SweetAlertDialog dialogConfirmarShop = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
                                            dialogConfirmarShop.setCancelable(true);
                                            dialogConfirmarShop.setCanceledOnTouchOutside(false);
                                            dialogConfirmarShop.setTitleText("Estas en el Local");

                                            dialogConfirmarShop.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    realizar_pedido.setEnabled(true); //habilitar el boton
                                                    if (es_celular) {
                                                        ShowPopupWindow();// mostrar las pciones de llamada
                                                    }else{
                                                        Toast.makeText(getContext(), "No puedes realizar llamadas", Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialogConfirmarShop.dismissWithAnimation();
                                                }
                                            });

                                            dialogConfirmarShop.setConfirmButton("Si!", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    final EditText numero_mesa = new EditText(getContext());


                                                    if (SweetAlertDialog.DARK_STYLE) {
                                                        numero_mesa.setTextColor(Color.WHITE);
                                                    }
                                                    // Show soft keyboard automatically and request focus to field
                                                    numero_mesa.requestFocus();
                                                    numero_mesa.setInputType(InputType.TYPE_CLASS_NUMBER);
//                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                        numero_mesa.setShowSoftInputOnFocus(true);
//                                                    }

                                                    LinearLayout linearLayout = new LinearLayout(getContext());
                                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                    linearLayout.addView(numero_mesa);
//                                            linearLayout.addView(checkBox);

                                                    final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
                                                    dialog.setCancelable(true);
                                                    dialog.setCanceledOnTouchOutside(false);
                                                    dialog.setTitleText("Codigo de Mesa");
//                                            dialog.hideConfirmButton();

                                                    dialog.setCancelButton("Cancelar", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            realizar_pedido.setEnabled(true); //habilitar el boton
                                                            dialog.dismissWithAnimation();
                                                        }
                                                    });

                                                    dialog.setConfirmButton("Pedir!", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            String _numero_mesa = numero_mesa.getText().toString();
                                                            if (_numero_mesa.isEmpty()) {
                                                                numero_mesa.setError("Debe escribir el codigo de la mesa");
                                                            } else {
                                                                //enviar la orden a la web
//                                                                true si esta en el local
                                                                nro_mesa = _numero_mesa;
                                                                esta_en_local = true;


                                                                if((int)Carroprincipal.count(Carroprincipal.class, "idshop = ? and idcaja = ?", filtro_idshop) > 0){
                                                                    //verificar si hay orden y verificar la mesa
                                                                    List<Carroprincipal> carroprincipal = Carroprincipal.find(Carroprincipal.class, "idshop = ? and idcaja = ?", filtro_idshop);
                                                                    jsonOrder(UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/orders/" + UrlRaiz.ws_key + "&output_format=JSON&filter[codigo_unico_app]="+carroprincipal.get(0).getCodigounico()+"&display=full", false);
                                                                }
                                                                else{
                                                                    xmlSendCart(); // descomentar para enviar a la web los datos del carrito
                                                                }


                                                                dialog.dismissWithAnimation();
                                                            }
                                                        }
                                                    });
                                                    dialog.setCustomView(linearLayout);
                                                    dialog.show();
                                                    dialogConfirmarShop.dismissWithAnimation();
                                                }
                                            });

                                            dialogConfirmarShop.show();
                                        }
                                    }
                                    }//fin del else que comprueba la mesa
                                    else{
                                        Toast.makeText(getContext(),"Location Not found",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                }else{
                    realizar_pedido.setEnabled(true); //deshabilitar el boton
                    Toast.makeText(getActivity(),"No hay conexión a internet",Toast.LENGTH_SHORT).show();
                }
            }
        });


        ir_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });


        return view;
    }

    private boolean estaInstaladaAplicacion(String nombrePaquete, Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(nombrePaquete, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isTelephone(Activity activity){
        //pseudocode, don't have the copy and paste here right know and can't remember every line
        Intent i = new Intent(Intent.ACTION_DIAL);
        return i.resolveActivity(activity.getPackageManager()) != null;
    }


    public void xmlSendCart(){

        String[] vals = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current)
        };
        CarroList = Carro.find(Carro.class, "idshop = ? and idcaja = ?", vals);


        String url = UrlRaiz.domain+"/"+tienda.getVirtual_uri()+"api/carts/"+UrlRaiz.ws_key+"&schema=blank";

        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Enviando...");
        pDialog.setProgressDrawable(getContext().getWallpaper());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

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

                    _ID_cart = idItem.item(0).getChildNodes().item(0).getNodeValue();
                    Carroprincipal carroprincipal = new Carroprincipal();
                    carroprincipal.setId_cart(_ID_cart);
                    carroprincipal.setId_shop(idtienda_current);
                    carroprincipal.setCodigounico(codigo_unico_app);
                    carroprincipal.setId_cliente(Integer.valueOf(usuario.getIdcustomer()));
                    carroprincipal.setNro_mesa(nro_mesa);
                    carroprincipal.setId_caja(tienda.getId_caja());
                    carroprincipal.save();


                    //borrar el carrito con los productos de la tienda especifica
                    if (CarroList.size() > 0) {
                        for (int i = 0; i < CarroList.size(); i++) {
                            Carro carro = CarroList.get(i);
                            carro.delete();
                        }
                    }

                    if (!esta_en_local){
                        List<Carroprincipal> carroprincipal2 = Carroprincipal.find(Carroprincipal.class, "codigounico = ?", codigo_unico_app);

                        if (carroprincipal2.size() > 0) {
                            for (int i = 0; i < carroprincipal2.size(); i++) {
                                Carroprincipal carroprincipal1 = carroprincipal2.get(i);
                                carroprincipal1.delete();
                            }
                        }

                    }

                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Exito!")
                            .setContentText("Tu pedido fue realizado :)")
                            .show();



                    getFragmentManager().popBackStackImmediate();
                    pDialog.dismiss();
//                    alertDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error!")
                        .setContentText("Tu pedido NO fue realizado")
                        .hideConfirmButton()
                        .setCancelText("OK")
                        .show();
                pDialog.dismiss();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/xml; charset=" + getParamsEncoding();
            }

            @Override
            public byte[] getBody() {
                String postData = generaXMLCarro(); // TODO get your final output
//                        Log.e(TAG, postData);
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

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.parseDouble(twoDForm.format(d));
    }

    public String generaXMLCarro() {

        String[] argwheretienda = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current),
        };

//        Tienda tienda = Tienda.findById(Tienda.class, idtienda_current);
        List<Tienda> tiendas = Tienda.find(Tienda.class, "idshop = ? and idcaja = ?", argwheretienda);
        tienda = tiendas.get(0);

        String[] vals = {
                String.valueOf(tienda.getId_shop())
        };


        List<Usuario> usuarios = Usuario.find(Usuario.class, "idshop = ?", vals);
        String direccion = "";
        if (usuarios.size() > 0){
            usuario = usuarios.get(0);
            id_customer = usuario.getIdcustomer();
            direccion = usuario.getDireccion();
        }
        if (esta_en_local){
            viene_de = "2"; //2 es App-mesa
            es_delivery = "0";
            direccion_delivery = "";

        }else{
            //Delivery
            viene_de = "1"; //1 es App-delivery
            es_delivery = "1";
            direccion_delivery = direccion;
        }

//Log.e(TAG, ""+CarroList.size());
        String format= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<cart>\n" +
                //"<id/>\n" +
                "<id_address_delivery>"+id_address+"</id_address_delivery>\n" +
                "<id_address_invoice>"+id_address+"</id_address_invoice>\n" +
                "<id_currency>1</id_currency>\n" +
                "<id_customer>"+id_customer+"</id_customer>\n" +
                "<id_guest/>\n" +
                "<id_lang>2</id_lang>\n" +
                "<id_shop_group/>\n" +
                "<id_shop/>\n" +
                "<id_carrier/>\n" +
                "<recyclable/>\n" +
                "<gift/>\n" +
                "<gift_message/>\n" +
                "<mobile_theme/>\n" +
                "<delivery_option/>\n" +
                "<secure_key/>\n" +
                "<allow_seperated_package/>\n" +
                "<date_add/>\n" +
                "<date_upd/>\n" +
                // agregados para la app////////
                "<id_caja>"+tienda.getId_caja()+"</id_caja>\n" +
                "<cliente_empresa>Cliente</cliente_empresa>\n" +
                "<nro_mesa>"+nro_mesa+"</nro_mesa>\n" +
                "<viene_de>"+viene_de+"</viene_de>\n" +
                "<es_delivery>"+es_delivery+"</es_delivery>\n" +
                "<direccion_delivery>"+direccion_delivery+"</direccion_delivery>\n" +
                "<codigo_unico_app>"+codigo_unico_app+"</codigo_unico_app>\n" +
                "<latitud_cliente>"+latitud_cliente+"</latitud_cliente>\n" +
                "<longitud_cliente>"+longitud_cliente+"</longitud_cliente>\n" +
                ////////////////////////////
                "<associations>\n"+
                "<cart_rows>\n";
                String l = "";
                    for(int j = 0; j < CarroList.size(); j++){
                        l +=
                                "<cart_row>\n" +
                                        "<id_product>"+CarroList.get(j).getId_producto()+"</id_product>\n" +
                                        "<id_product_attribute>null</id_product_attribute>\n" +
                                        "<id_address_delivery>"+id_address+"</id_address_delivery>\n" +
                                        "<quantity>"+CarroList.get(j).getCantidad()+"</quantity>\n" +
                                "</cart_row>\n";
                    }

format += l + "</cart_rows>\n"+
                "</associations>\n" +
                "</cart>\n" +
                "</prestashop>";

        return format;
    }

    public String generaXMLOrder() {
        String[] vals = {
                String.valueOf(tienda.getId_shop())
        };
        List<Usuario> usuarios = Usuario.find(Usuario.class, "idshop = ?", vals);
        if (usuarios.size() > 0){
            Usuario usuario = usuarios.get(0);
            id_customer = usuario.getIdcustomer();
        }


        String format= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<order>\n" +
                "<id_address_delivery>"+id_address+"</id_address_delivery>\n" +
                "<id_address_invoice>"+id_address+"</id_address_invoice>\n" +
                "<id_cart>"+_ID_cart+"</id_cart>\n" +
                "<id_currency>1</id_currency>\n" +
                "<id_lang>1</id_lang>\n" +
                "<id_customer>"+id_customer+"</id_customer>\n" +
                "<id_carrier>8</id_carrier>\n" +
                "<current_state>3</current_state>\n" +
                "<module>ps_checkpayment</module>\n" +
                "<invoice_number/>\n" +
                "<invoice_date/>\n" +
                "<delivery_number/>\n" +
                "<delivery_date/>\n" +
                "<valid/>\n" +
                "<date_add/>\n" +
                "<date_upd/>\n" +
                "<shipping_number/>\n" +
                "<id_shop_group/>\n" +
                "<id_shop/>\n" +
                "<secure_key/>\n" +
                "<payment>Pago en Efectivo</payment>\n" +
                "<recyclable/>\n" +
                "<gift/>\n" +
                "<gift_message/>\n" +
                "<mobile_theme/>\n" +
                "<total_discounts/>\n" +
                "<total_discounts_tax_incl/>\n" +
                "<total_discounts_tax_excl/>\n" +
                "<total_paid>0.000000</total_paid>\n" +
                "<total_paid_tax_incl/>\n" +
                "<total_paid_tax_excl/>\n" +
                "<total_paid_real>0.000000</total_paid_real>\n" +
                "<total_products>0.000000</total_products>\n" +
                "<total_products_wt>0.000000</total_products_wt>\n" +
                "<total_shipping/>\n" +
                "<total_shipping_tax_incl/>\n" +
                "<total_shipping_tax_excl/>\n" +
                "<carrier_tax_rate/>\n" +
                "<total_wrapping/>\n" +
                "<total_wrapping_tax_incl/>\n" +
                "<total_wrapping_tax_excl/>\n" +
                "<round_mode/>\n" +
                "<round_type/>\n" +
                "<conversion_rate>1.000000</conversion_rate>\n" +
                "<reference></reference>\n" +
                "<associations>\n" +
                    "<order_rows>"+
                        "<order_row>\n" +
                            "<id/>\n" +
                            "<product_id/>\n" +
                            "<product_attribute_id/>\n" +
                            "<product_quantity/>\n" +
                            "<product_name/>\n" +
                            "<product_reference/>\n" +
                            "<product_ean13/>\n" +
                            "<product_upc/>\n" +
                            "<product_price/>\n" +
                            "<unit_price_tax_incl/>\n" +
                            "<unit_price_tax_excl/>\n" +
                        "</order_row>\n" +

                    "</order_rows>\n" +
                "</associations>\n" +
                "</order>\n" +
                "</prestashop>";

        return format;
    }

    private void ShowPopupWindow(){
        try {

            ImageView btncall, btnsound;
            TextView txtclose;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setCancelable(false);
            View dialog_call = getLayoutInflater().inflate(R.layout.popup_after_cart, null);
            alertDialogBuilder.setView(dialog_call);
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            // window.showAtLocation(layout, 17, 100, 100);

            btncall = dialog_call.findViewById(R.id.btncall);
            btnsound = dialog_call.findViewById(R.id.btnsound);
            txtclose = dialog_call.findViewById(R.id.txtclose);

            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    realizar_pedido.setEnabled(true);
                }
            });


            btncall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e(TAG, tienda.getTelefono());
//                    dialContactPhone(tienda.getTelefono());
                    MakePhoneCall(tienda.getTelefono());

                }

            });
            btnsound.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " sound  touch");
                    if(estaInstaladaAplicacion("com.whatsapp", getContext())){
//esta instalada.
//                        callWhatsApp("928550245","Tienda pruebaaaa", (Activity) getContext());
                        callWhatsApp(tienda.getCelular_whatsapp(),tienda.getNombre(), getActivity());
//                    alertDialog.dismiss();

                    }else{
//no esta instalada.
                        Toast.makeText(getContext(), "No tienes WhatsApp instalado", Toast.LENGTH_SHORT).show();
                    }
                }

            });

        }catch (Exception e){

        }
    }

    public void callWhatsApp(String phoneNumber, String name, Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = getUriFromPhoneNumber(phoneNumber, activity.getApplicationContext(), name, activity);
        intent.setDataAndType(uri, "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
        intent.setPackage("com.whatsapp");
        activity.startActivity(intent);
    }

    public Uri getUriFromPhoneNumber(String phoneNumber, Context context, String name, Activity activity) {
        Uri uri = null;
        String contactId = getContactIdByPhoneNumber(phoneNumber, context);
        // check for exist phone number
        if (!TextUtils.isEmpty(contactId)) {
            ContentResolver resolver = activity.getContentResolver();
//            Log.e(TAG, "contactId_newcontactId_new contactId ->   "+ contactId);
            cursor = resolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data._ID},
                    ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.CONTACT_ID + " = ?",
                    new String[]{"vnd.android.cursor.item/vnd.com.whatsapp.voip.call", contactId}, null);


//            Log.e(TAG, "cursor.getCount() ->   "+ cursor.getCount());

            if (cursor != null) {
                // here in the first time equals false and not will work, need fix
//                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID));
                    if (!TextUtils.isEmpty(id)) {
                        uri = Uri.parse(ContactsContract.Data.CONTENT_URI + "/" + id);
//                        Log.e(TAG, "uriuri ->   "+uri);
                        xmlSendCart(); // descomentar para enviar a la web los datos del carrito
                        alertDialog.dismiss();
                        break;
                    }
                }
                cursor.close();
            }
        } else {
//            Log.e(TAG, "Agregar nuevo contacto");
            addToContactsNewNumber(phoneNumber, name, activity);
        }
        return uri;
    }

    // find phone number in contacts
    public static String getContactIdByPhoneNumber(String phoneNumber, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        return contactId;
    }

    public void addToContactsNewNumber(String number, String name, Activity activity) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build()); // Name of the person

        ops.add(ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build()); // Type of mobile number

        try {

            final ContentProviderResult[] results = activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            final String[] projection = new String[] { ContactsContract.RawContacts.CONTACT_ID };
            final Cursor cursor = activity.getContentResolver().query(results[0].uri, projection, null, null, null);
            cursor.moveToNext();
            long contactId = cursor.getLong(0);
//            Log.e(TAG, "Contacto agregado ID ->   "+contactId);
            cursor.close();
            callWhatsApp(number, name, activity);
//            Log.e(TAG, "numbernumbernumbernumber ->   "+number);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNumber, null)));
//        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));

    }
    public void MakePhoneCall(final String phoneNumber){
        String number = ("tel:" + phoneNumber);
        Intent mIntent = new Intent(Intent.ACTION_CALL);
        mIntent.setData(Uri.parse(number));
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //You already have permission
            try {

                startActivity(mIntent);
                xmlSendCart(); // descomentar para enviar a la web los datos del carrito
                alertDialog.dismiss();

            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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


    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

//
//            Log.e(TAG, latitudeBest + "");
//            Log.e(TAG, longitudeBest + "");
//            Toast.makeText(getActivity(), "GPS Provider update", Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };


    //consultar order
    private void jsonOrder(String url, final boolean vieneStart) {
                Log.e(TAG, "Llego al json ORDER");
                Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

//                            Log.e(TAG, response.toString());
                            JSONArray jsonMainNode = response.getJSONArray("orders");

                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                            Integer id = jsonChildNode.optInt("id");
                            String current_state = jsonChildNode.optString("current_state");
                            String nro_mesa = jsonChildNode.optString("nro_mesa");
                            String es_deli = jsonChildNode.optString("es_delivery");
                            String codigo = jsonChildNode.optString("codigo_unico_app");
//                            Log.e(TAG, es_deli);
//                            1 si 2 no para delivery
                            List<Carroprincipal> carroprincipal = Carroprincipal.find(Carroprincipal.class, "codigounico = ?", codigo);

                            if (vieneStart && es_deli.equals("2") && current_state.equals("2") && carroprincipal.size() > 0){
//                                codigo_unico_app = UUID.randomUUID().toString();

                                for (int i = 0; i < carroprincipal.size(); i++) {
                                    Carroprincipal carroprincipal1 = carroprincipal.get(i);
                                    carroprincipal1.delete();
                                }

                            }else if (vieneStart && es_deli.equals("2") && current_state.equals("3") && carroprincipal.size() > 0){
                                Log.e(TAG, "Pedido en preparación");
                            }else{

                                if (es_deli.equals("2")) {
                                    if (current_state.equals("2")){

                                        codigo_unico_app = UUID.randomUUID().toString();

                                        if (carroprincipal.size() > 0) {
                                            for (int i = 0; i < carroprincipal.size(); i++) {
                                                Carroprincipal carroprincipal1 = carroprincipal.get(i);
                                                carroprincipal1.delete();
                                            }
                                        }

                                        xmlSendCart();
                                    }else{
//                                    Log.e(TAG, codigo_unico_app);
                                        if (codigo.equals(codigo_unico_app)){
                                            xmlSendCart();
                                        }else{
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error!")
                                                    .setContentText("¡Tu pedido NO fue realizado! \nLa mesa esta ocupada Gracias")
                                                    .hideConfirmButton()
                                                    .setCancelText("OK")
                                                    .show();
                                            realizar_pedido.setEnabled(true);
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


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
