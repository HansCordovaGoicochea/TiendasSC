package com.scientechperu.tiendassc.Fragmentos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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


public class FragmentCarrito extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_SECTION_TITLE = "Carrito";
    private static final String TAG = FragmentCarrito.class.getSimpleName();
    public static final String ARG_SECTION_URL = "productos_url";
    public String URL = "";

    public RecyclerView recyclerView;
    RecyclerAdapterCarrito rcAdapter;
    private List<Carro> CarroList = Carro.listAll(Carro.class);


    public TextView cantidad_productos;
    public TextView total;
    public TextView subtotal;
    public TextView igv;


    private PopupWindow window;

    String id_address = "0";
    String id_customer = "4"; // el cliente 4 es prueba

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


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_activividad_carrito, container, false);

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

        cantidad_productos.setText((int) Carro.count(Carro.class)+" Productos para pedir");

        double totalSum = 0.0;
        double subtotalSum = 0.0;
        double igvSum = 0.0;
        List<Carro> carros = Carro.listAll(Carro.class);
        for (int i = 0; i < carros.size(); i++) {
            totalSum += carros.get(i).getImporte();
        }
        if (totalSum > 0){
            subtotalSum = totalSum / 1.18;
            igvSum =  totalSum - subtotalSum;
        }
        total.setText("S/"+ roundTwoDecimals(totalSum));
        subtotal.setText("S/"+roundTwoDecimals(subtotalSum));
        igv.setText("S/"+roundTwoDecimals(igvSum));


        Button realizar_pedido = (Button) view.findViewById(R.id.btnPasar);
        realizar_pedido.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
//                // Setting Alert Dialog Title
//                alertDialogBuilder.setTitle("Confirmar pedido..!!!");
//                // Icon Of Alert Dialog
//                alertDialogBuilder.setIcon(R.drawable.question);
//                // Setting Alert Dialog Message
//                alertDialogBuilder.setMessage("Are you sure,You want to exit");
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

//                'http://mystore.com/api/customers?schema=blank'
        String url = UrlRaiz.domain+"/pollos-parrillas-elhawaiano/api/carts/"+UrlRaiz.ws_key+"&schema=blank";

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
                String postData = generaXMLCliente(); // TODO get your final output
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

    public String generaXMLCliente() {

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

            xmlSendCart();

            ImageView btncall, btnsound, btncamera, btnvideo,btngallary,btnwrite;

//            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View layout = inflater.inflate(R.layout.popup_after_cart, null);
            View layout = LayoutInflater.from(getActivity()).inflate(R.layout.popup_after_cart, null);
            window = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setOutsideTouchable(false);
            window.showAtLocation(layout, Gravity.CENTER,0, 0);
            //  window.showAtLocation(layout, 17, 100, 100);

            btncall = (ImageView) layout.findViewById(R.id.btncall);
            btnsound = (ImageView) layout.findViewById(R.id.btnsound);
            btncall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, " button call press ");
                    window.dismiss();
                }

            });
            btnsound.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, " sound  touch");
                    window.dismiss();

                }

            });

        }catch (Exception e){

        }
    }

}
