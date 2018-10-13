package com.scientechperu.pideloya.Adaptadores;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.scientechperu.pideloya.Clases.CuponesClientes;
import com.scientechperu.pideloya.Clases.Tienda;
import com.scientechperu.pideloya.Clases.UrlRaiz;
import com.scientechperu.pideloya.Clases.Usuario;
import com.scientechperu.pideloya.R;
import com.scientechperu.pideloya.SingletonVolley.MySingleton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CuponClienteAdapter extends RecyclerView.Adapter<CuponClienteAdapter.ViewHolder>{

    private static final String TAG = CuponClienteAdapter.class.getSimpleName();
    private ArrayList<CuponesClientes> nameList;
    private LocationManager locationManager;
    private List<Tienda> tiendas;
    private Tienda tienda;
    private double longitudeBest, latitudeBest;
    private FusedLocationProviderClient mFusedLocationClient;
    private String latitud_cliente = "";
    private String longitud_cliente = "";
    private String nro_mesa = "0", viene_de = "", es_delivery = "", direccion_delivery = "", codigo_unico_app = "";
    private GoogleMap gmap;
    private LatLng exact_location;
    private Usuario usuario;

    private Integer posicion_cupon;

    private String id_address = "0";
    private String id_customer = "4"; // el cliente 4 es prueba

    public CuponClienteAdapter(ArrayList<CuponesClientes> nameList) {
        this.nameList = nameList;

//        Log.e("namelist", nameList.toString());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        ImageView pedir;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemTextView);
            image = itemView.findViewById(R.id.imagen_producto);
            pedir = itemView.findViewById(R.id.boton_pedir);
        }
    }

    @NonNull
    @Override
    public CuponClienteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cupon_item, parent, false);

        locationManager = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final CuponClienteAdapter.ViewHolder holder, final int position) {

        locationManager.removeUpdates(locationListenerGPS);

        String texto = "Tienes "+nameList.get(position).getNombre_producto()+" gratis por tus compras acumuladas";

        holder.name.setText(texto);

        String url_imagen = UrlRaiz.domain_api + "images/products/" + nameList.get(position).getId_producto() + "/" + nameList.get(position).getId_imagen_default() + "" + UrlRaiz.ws_key;
        Log.e("imagenes", url_imagen);

        Glide.with(holder.itemView.getContext())
                .load(url_imagen)
//                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);

        holder.pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.pedir.setEnabled(false); //inhabilitar el boton

                final String[] filtro_idshop = {
                        String.valueOf(nameList.get(position).getId_shop()),
                        String.valueOf(nameList.get(position).getId_caja())
                };


                tiendas = Tienda.find(Tienda.class, "idshop = ? and idcaja = ?", filtro_idshop);
                tienda = tiendas.get(0);
                final Context context_holder = holder.itemView.getContext();

                posicion_cupon = position;

                if(isOnline()){
                    Log.e(TAG, "--------------");
                    if (!checkLocation(holder.itemView.getContext())){
                        holder.pedir.setEnabled(true); //habilitar el boton

                        return;
                    }
//                    Toast.makeText(holder.itemView.getContext(), "Hay internet", Toast.LENGTH_SHORT).show();


                    if (ActivityCompat.checkSelfPermission(context_holder, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context_holder, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }


                    locationManager = (LocationManager) context_holder.getSystemService(Context.LOCATION_SERVICE);

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


                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context_holder);
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        Log.e(TAG, location.getLatitude() + "");
                                        Log.e(TAG, location.getLongitude() + "");

                                        latitud_cliente = location.getLatitude() + "";
                                        longitud_cliente = location.getLongitude() + "";

//                                        latitud_cliente = -7.15422400 + "";
//                                        longitud_cliente = -78.51252800 + "";

                                        codigo_unico_app = UUID.randomUUID().toString();
                                        //ubicacion del centro del punto
                                        exact_location = new LatLng(Double.parseDouble(tienda.getLatitud()), Double.parseDouble(tienda.getLongitud()));
//                                        exact_location = new LatLng(-7.15433, -78.5123167);

                                        double lat = exact_location.latitude; //getLatitude
                                        double lng = exact_location.longitude;//getLongitude

                                        // aqui guardamos las distancias en tre los puntos
                                        float[] disResultado = new float[2];

                                        Location.distanceBetween(
                                                lat,
                                                lng,
                                                location.getLatitude(),
                                                location.getLongitude(),
                                                disResultado);

                                        Log.e("disresultado", Arrays.toString(disResultado));
                                        if((double)disResultado[0] > (double)99) {
//                                           Log Fuera
                                            Log.e(TAG, "Fuera");
                                            Toast.makeText(holder.itemView.getContext(), "Tienes que estar en el local ¡Gracias!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Log.e(TAG, "Dentro");
                                            final SweetAlertDialog confirmarPedido = new SweetAlertDialog(context_holder, SweetAlertDialog.NORMAL_TYPE);
                                            confirmarPedido.setCancelable(true);
                                            confirmarPedido.setCanceledOnTouchOutside(false);
                                            confirmarPedido.setTitleText("¿Seguro de realizar el pedido?");

                                            confirmarPedido.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    confirmarPedido.dismissWithAnimation();
                                                }
                                            });

                                            confirmarPedido.setConfirmButton("Si!", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {



                                                    final SweetAlertDialog dialogConfirmarShop = new SweetAlertDialog(context_holder, SweetAlertDialog.NORMAL_TYPE);
                                                    dialogConfirmarShop.setCancelable(true);
                                                    dialogConfirmarShop.setCanceledOnTouchOutside(false);
                                                    dialogConfirmarShop.setTitleText("Estas en el Local");

                                                    dialogConfirmarShop.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            dialogConfirmarShop.dismissWithAnimation();
                                                        }
                                                    });

                                                    dialogConfirmarShop.setConfirmButton("Si", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                            xmlSendCart(context_holder); // descomentar para enviar a la web los datos del carrito

                                                            dialogConfirmarShop.dismissWithAnimation();
                                                        }
                                                    });
                                                    dialogConfirmarShop.show();

                                                    confirmarPedido.dismissWithAnimation();
                                                }
                                            });
                                            confirmarPedido.show();

                                        }
                                    }
                                }
                            });

                }else{
                    Toast.makeText(holder.itemView.getContext(), "No Hay internet", Toast.LENGTH_SHORT).show();
                    holder.pedir.setEnabled(true); //habilitar el boton
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


    //Verifica que haya conexion a internet

    private static boolean isOnline() {

        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            return (val == 0);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkLocation(Context context) {
        if (!isLocationEnabled())
            showAlert(context);
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        boolean is_l = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.e(TAG, is_l+"");
        return is_l;
    }

    private void showAlert(final Context context) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Activar Localización")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
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


    public void xmlSendCart(final Context context){

        String[] vals = {
                String.valueOf(tienda.getId_shop()),
                String.valueOf(tienda.getId_caja())
        };
//        CarroList = Carro.find(Carro.class, "idshop = ? and idcaja = ?", vals);


        String url = UrlRaiz.domain+"/"+tienda.getVirtual_uri()+"api/carts/"+UrlRaiz.ws_key+"&schema=blank";

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Enviando...");
        pDialog.setProgressDrawable(context.getWallpaper());
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
                } catch (ParserConfigurationException | SAXException | IOException e) {
                }
                if (dom != null) {
                    Element root = dom.getDocumentElement();
                    NodeList idItem = root.getElementsByTagName("id");

                    String _ID_cart = idItem.item(0).getChildNodes().item(0).getNodeValue();


                    xmlSendCupon(context, pDialog);

//                    nameList.remove(posicion_cupon);
//                    notifyDataSetChanged();

                    // para poder actualizar el fragment debemos de crear el fragmen con u TAG sino vas a estar sufriendo con esto
                    Fragment fm = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("FragmentoCarusel");
                    final FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.detach(fm);
                    ft.attach(fm);
                    ft.commit();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
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
        MySingleton.getInstance(context).addToRequestQueue(strReq);
    }


    public String generaXMLCarro() {

        String[] argwheretienda = {
                String.valueOf(tienda.getId_shop()),
                String.valueOf(tienda.getId_caja()),
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
        viene_de = "3"; //2 es App-promocion
        es_delivery = "0";
        direccion_delivery = "";


//Log.e(TAG, ""+CarroList.size());
        String format= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<cart>\n" +
                "<id/>\n" +
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
                "<cart_rows>\n"+

                    "<cart_row>\n" +
                            "<id_product>"+nameList.get(0).getId_producto()+"</id_product>\n" +
                            "<id_product_attribute>null</id_product_attribute>\n" +
                            "<id_address_delivery>"+id_address+"</id_address_delivery>\n" +
                            "<quantity>1</quantity>\n" +
                    "</cart_row>\n" +
                "</cart_rows>\n"+
                "</associations>\n" +
                "</cart>\n" +
                "</prestashop>";

        return format;
    }



    public void xmlSendCupon(final Context context, final ProgressDialog pDialog){

        String[] vals = {
                String.valueOf(tienda.getId_shop()),
                String.valueOf(tienda.getId_caja())
        };
//        CarroList = Carro.find(Carro.class, "idshop = ? and idcaja = ?", vals);


        String url = UrlRaiz.domain+"/"+tienda.getVirtual_uri()+"api/cuponesclientes/"+UrlRaiz.ws_key+"&schema=blank";


        StringRequest strReq = new StringRequest(Request.Method.PUT,
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

                    String _ID_cart = idItem.item(0).getChildNodes().item(0).getNodeValue();


                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Exito!")
                            .setContentText("Tu pedido fue realizado :)")
                            .show();

                    pDialog.dismiss();

//                    // para poder actualizar el fragment debemos de crear el fragmen con u TAG sino vas a estar sufriendo con esto
//                    Fragment fm = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("FragmentoCarusel");
//                    final FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
//                    ft.detach(fm);
//                    ft.attach(fm);
//                    ft.commit();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/xml; charset=" + getParamsEncoding();
            }

            @Override
            public byte[] getBody() {
                String postData = generaXMLCupon(nameList.get(posicion_cupon).getId_cupon_cliente()); // TODO get your final output
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
        MySingleton.getInstance(context).addToRequestQueue(strReq);
    }


    public String generaXMLCupon(String id_cupon) {

        String[] argwheretienda = {
                String.valueOf(tienda.getId_shop()),
                String.valueOf(tienda.getId_caja()),
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
        viene_de = "3"; //2 es App-promocion
        es_delivery = "0";
        direccion_delivery = "";


//Log.e(TAG, ""+CarroList.size());
        String format= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<cupon_cliente>\n" +
                "<id>"+id_cupon+"</id>\n" +
                "<id_shop>"+tienda.getId_shop()+"</id_shop>\n" +
                "<id_cliente>"+usuario.getIdcustomer()+"</id_cliente>\n" +
                "<id_producto>"+nameList.get(posicion_cupon).getId_producto()+"</id_producto>\n" +
                "<acumulado>"+nameList.get(posicion_cupon).getAcumulado()+"</acumulado>\n" +
                "<terminos_condiciones>"+nameList.get(posicion_cupon).getTerminos_condiciones()+"</terminos_condiciones>\n" +
                "<nombre_producto>"+nameList.get(posicion_cupon).getNombre_producto()+"</nombre_producto>\n" +
                "<id_imagen_producto>"+nameList.get(posicion_cupon).getId_imagen_default()+"</id_imagen_producto>\n" +
                "<active>0</active>\n" +
                "</cupon_cliente>\n" +
                "</prestashop>";

        return format;
    }
}
