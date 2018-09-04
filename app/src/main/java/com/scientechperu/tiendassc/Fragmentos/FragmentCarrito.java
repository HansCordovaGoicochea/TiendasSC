package com.scientechperu.tiendassc.Fragmentos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.scientechperu.tiendassc.Adaptadores.RecyclerAdapterCarrito;
import com.scientechperu.tiendassc.Adaptadores.RecyclerAdapterProductos;
import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Entendiendo.Utils;
import com.scientechperu.tiendassc.R;
import com.scientechperu.tiendassc.SingletonVolley.MySingleton;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.scientechperu.tiendassc.Splash.isOnline;
//import static com.scientechperu.tiendassc.Fragmentos.FragmentProductos.;

public class FragmentCarrito extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_SECTION_TITLE = "Carrito";
    private static final String TAG = FragmentCarrito.class.getSimpleName();
    public static final String ARG_SECTION_URL = "productos_url";
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

    AlertDialog alertDialog;

    TextView textCartItemCount;
    int mCartItemCount = 0;

    SharedPreferences prefs;
    private static final int REQUEST_CALL_PHONE = 1;

    public FragmentCarrito() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // To load the data at a later time
        prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        Integer idtienda_current = prefs.getInt("id_shop", 0);

//        Tienda tienda = Tienda.findById(Tienda.class, idtienda_current);
        List<Tienda> tiendas = Tienda.find(Tienda.class, "idshop = ?", ""+idtienda_current);
        tienda = tiendas.get(0);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        final MenuItem menuItem = menu.findItem(R.id.action_shop);
        View actionView = menuItem.getActionView();

        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge); // el texto de numero de productos

        // To load the data at a later time
        prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        Integer idtienda_current = prefs.getInt("id_shop", 0);

        String[] vals = {
                String.valueOf(idtienda_current)
        };
        mCartItemCount = (int) Carro.count(Carro.class, "idshop = ?", vals);
        if (mCartItemCount < 0){
            mCartItemCount = 0;
        }

        setupBadge(mCartItemCount);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(ARG_SECTION_TITLE);
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


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_activividad_carrito, container, false);

        // To load the data at a later time
        prefs = getContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        Integer idtienda_current = prefs.getInt("id_shop", 0);

        String[] vals = {
                String.valueOf(idtienda_current)
        };
        List<Carro> CarroList = Carro.find(Carro.class, "idshop = ?", vals);


//

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("cargado", true);
        editor.apply();

        // Ubicar argumento en el text view de section_fragment.xml
        String title = getArguments().getString(ARG_SECTION_TITLE);
        URL = getArguments().getString(ARG_SECTION_URL);

        //initialize recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.reciclerview_productos);
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

        cantidad_productos.setText((int) Carro.count(Carro.class, "idshop = ?", vals)+" Productos para pedir");

        Button realizar_pedido = (Button) view.findViewById(R.id.btnPasar);

        double totalSum = 0.0;
        double subtotalSum = 0.0;
        double igvSum = 0.0;

        for (int i = 0; i < CarroList.size(); i++) {
            totalSum += CarroList.get(i).getImporte();
        }
        if (totalSum > 0){
            subtotalSum = totalSum / 1.18;
            igvSum =  totalSum - subtotalSum;
            realizar_pedido.setEnabled(true);
        }else{
            realizar_pedido.setEnabled(false);
        }
        total.setText("S/"+ roundTwoDecimals(totalSum));
        subtotal.setText("S/"+roundTwoDecimals(subtotalSum));
        igv.setText("S/"+roundTwoDecimals(igvSum));


        realizar_pedido.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(isOnline(getContext())){
                    ShowPopupWindow();
                }else{
                    Toast.makeText(getActivity(),"No hay conexión a internet",Toast.LENGTH_SHORT).show();
                }


            }
        });



        return view;
    }


    public void xmlSendCart(){


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
                Log.e(TAG, response.toString());
                Toast.makeText(getContext(), "Pedido realizado", Toast.LENGTH_SHORT).show();
                pDialog.hide();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/xml; charset=" + getParamsEncoding();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
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
        return Double.valueOf(twoDForm.format(d));
    }

    public String generaXMLCarro() {

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
                "<associations>\n"+
                "<cart_rows>\n";
        String l = "";
        for(int j = 0; j < CarroList.size(); j++)
            l +=
                    "<cart_row>\n" +
                            "<id_product>"+CarroList.get(j).getId_producto()+"</id_product>\n" +
                            "<id_product_attribute>null</id_product_attribute>\n" +
                            "<id_address_delivery>"+id_address+"</id_address_delivery>\n" +
                            "<quantity>"+CarroList.get(j).getCantidad()+"</quantity>\n" +
                            "</cart_row>\n";
        format += l + "</cart_rows>\n"+
                "</associations>\n" +
                "</cart>\n" +
                "</prestashop>";

        return format;
    }

    private void ShowPopupWindow(){
        try {

//            xmlSendCart(); // descomentar para enviar a la web los datos del carrito

            ImageView btncall, btnsound, btncamera, btnvideo,btngallary,btnwrite;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setCancelable(false);
            View dialog_call = getLayoutInflater().inflate(R.layout.popup_after_cart, null);
            alertDialogBuilder.setView(dialog_call);
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            // window.showAtLocation(layout, 17, 100, 100);

            btncall = (ImageView) dialog_call.findViewById(R.id.btncall);
            btnsound = (ImageView) dialog_call.findViewById(R.id.btnsound);
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
                    alertDialog.dismiss();

                }

            });

        }catch (Exception e){

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

}
