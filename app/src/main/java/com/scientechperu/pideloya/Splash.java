package com.scientechperu.pideloya;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.scientechperu.pideloya.Clases.CategoriaTienda;
import com.scientechperu.pideloya.Clases.Tienda;
import com.scientechperu.pideloya.Clases.UrlRaiz;
import com.scientechperu.pideloya.SingletonVolley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by User on 02/02/2017.
 */

public class Splash extends Activity {

    private Timer timer;
    private ProgressBar progressBar;
    private int i=0;
    TextView textView;

    /*Se declara e inicializa una variable de tipo List que almacenará objetos de tipo Tienda*/
    List<CategoriaTienda> arrayCaTienda = CategoriaTienda.listAll(CategoriaTienda.class);
    CategoriaTienda pojoCaTienda;

    /*Se declara e inicializa una variable de tipo List que almacenará objetos de tipo Tienda*/
    List<Tienda> arrayTiendas = Tienda.listAll(Tienda.class);
    Tienda pojoTienda;

    long initialCount;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(!isConnected(Splash.this)) buildDialog(Splash.this).show();
        else {
            Toast.makeText(Splash.this,"¡BIENVENIDO!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.splash);


            progressBar=(ProgressBar)findViewById(R.id.progressBar);
            progressBar.setProgress(0);
            textView=(TextView)findViewById(R.id.textView);
            textView.setText("");


    //        if (arrayCaTienda.isEmpty()) {
                if(isOnline(this)){
    //                Toast.makeText(getApplicationContext(),"Conectado",Toast.LENGTH_SHORT).show();
                    //pedir las categorias para el menu
                    peticionServicioCaTiendas(UrlRaiz.domain + "/api/catiendas" + UrlRaiz.ws_key + "&output_format=JSON&display=full&filter[ids_tiendas]=![]");
                    runSplash(2);
                }else{
                    Toast.makeText(getApplicationContext(),"No hay conexión a internet",Toast.LENGTH_SHORT).show();
                    finish();
                }
    //        }else{
    //            Toast.makeText(getApplicationContext(),"ararayyyy",Toast.LENGTH_SHORT).show();
    //            arrayCaTienda = CategoriaTienda.listAll(CategoriaTienda.class);
    //            runSplash(5);
    //        }
            }
    }

    private void runSplash(final Integer num) {
        final long period = 100;
        timer=new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i<100){
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            textView.setText(String.valueOf(i)+"%");
                        }
                    });
                    progressBar.setProgress(i);
                    i = i + num;
                }else{
                    //closing the timer
                    timer.cancel();

                    // Get the shared preferences
                    SharedPreferences preferences = getSharedPreferences("preference_onboarding", MODE_PRIVATE);

                    // Check if onboarding_complete is false
                    if(!preferences.getBoolean("onboarding_complete",false)) {
                        // Start the onboarding Activity
                        Intent onboarding = new Intent(Splash.this, onBoarding.class);
                        startActivity(onboarding);

                    }else{
                        Intent intent =new Intent(Splash.this, ActividadPrincipal.class);
                        startActivity(intent);
                    }

                    // close this activity
                    finish();
                }
            }
        }, 0, period);
    }

    /*Método que devolverá una colección de objetos CategoriaTienda, y que recibe como parámetro de entrada
la URI para realizar la petición al servicio web.*/
    public void peticionServicioCaTiendas(String uri)
    {
//        Log.e("error_12223", UrlRaiz.domain + "catiendas" + UrlRaiz.ws_key + "&output_format=JSON&display=full");

        /*Se declara e inicializa un objeto de tipo JsonObjectRequest, que permite
        recuperar un JSONObject a partir de la URL que recibe. El constructor de la clase JsonObjectRequest
        recibe como argumentos de entrada el método para que el cliente realice operaciones sobre el servidor web, la uri
        para el acceso al recurso, y la interfaz Response.Listener, encargada de devolver la respuesta parseada a la petición
        del cliente, y la interfaz Response.ErrorListener encargada de entregar una respuesta errónea desde el servicio web.*/
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CategoriaTienda.deleteAll(CategoriaTienda.class);
                            Tienda.deleteAll(Tienda.class);

                            JSONArray jsonMainNode = response.getJSONArray("catiendas");

                            /*Se construye un bucle for() para recorrer la respuesta parseada y
                            construir un nuevo objeto Tienda por cada registro de la base de datos MySQL.*/
                            for (int i = 0; i < jsonMainNode.length(); i++)
                            {

                                JSONObject jsObjectCaTiendas = (JSONObject) jsonMainNode.get(i);
                                Integer id_categoria_tienda = jsObjectCaTiendas.getInt("id");
                                String nombre = jsObjectCaTiendas.getString("nombre_categoria");
                                String tiendas = jsObjectCaTiendas.getString("ids_tiendas");
                                pojoCaTienda = new CategoriaTienda(id_categoria_tienda,nombre,tiendas);
                                //Se añade el objeto creado a la colección de tipo List<Tienda>.
                                pojoCaTienda.save(); //guardar en la DB
//                                CategoriaTienda note = CategoriaTienda.findById(CategoriaTienda.class, id_categoria_tienda);
//                                Log.e("tag_pojoCaTienda", note.getNombre());
                                arrayCaTienda.add(pojoCaTienda);


                                peticionServicioTiendas(UrlRaiz.domain + "/api/stores" + UrlRaiz.ws_key + "&output_format=JSON&filter[active]=1&filter[id_shop]=["+tiendas+"]&display=full", nombre);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try
                            {
                                Toast.makeText(getApplicationContext(), "Error de petición de servicio: " + error.toString(),Toast.LENGTH_LONG).show();
                            } catch (Exception e)
                            {

                                e.printStackTrace();
                            }
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
        MySingleton.getInstance(this).addToRequestQueue(request);

    }



    public void peticionServicioTiendas(String uri, String nombre_categoria)
    {
//        Log.e("error_12223", uri);
        final String categoria = nombre_categoria;
        /*Se declara e inicializa un objeto de tipo JsonObjectRequest, que permite
        recuperar un JSONObject a partir de la URL que recibe. El constructor de la clase JsonObjectRequest
        recibe como argumentos de entrada el método para que el cliente realice operaciones sobre el servidor web, la uri
        para el acceso al recurso, y la interfaz Response.Listener, encargada de devolver la respuesta parseada a la petición
        del cliente, y la interfaz Response.ErrorListener encargada de entregar una respuesta errónea desde el servicio web.*/
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jsonMainNode = response.getJSONArray("stores");

                            /*Se construye un bucle for() para recorrer la respuesta parseada y
                            construir un nuevo objeto Tienda por cada registro de la base de datos MySQL.*/
                            for (int i = 0; i < jsonMainNode.length(); i++)
                            {

                                JSONObject jsObjectTiendas = (JSONObject) jsonMainNode.get(i);
                                Integer id_store = jsObjectTiendas.getInt("id");
                                Integer id_shop = jsObjectTiendas.getInt("id_shop");
                                String nombre = jsObjectTiendas.getString("name");
                                String razon_social = jsObjectTiendas.getString("razon_social");
                                String ruc = jsObjectTiendas.getString("ruc_tienda");
                                String virtual_uri = jsObjectTiendas.getString("virtual_uri");
                                String domain = jsObjectTiendas.getString("domain");
                                String latitud = jsObjectTiendas.getString("latitude");
                                String longitud = jsObjectTiendas.getString("longitude");
                                Integer id_imagen = jsObjectTiendas.getInt("id_shop");
                                String direccion = jsObjectTiendas.getString("address1");
                                String horarios  = jsObjectTiendas.getString("hours");
                                Integer id_caja  = jsObjectTiendas.getInt("id_apertura_caja");
                                String telefono  = jsObjectTiendas.getString("phone");
                                String nombre_categoria  = categoria;
                                String celular  = jsObjectTiendas.getString("fax");

                                pojoTienda = new Tienda(id_store, id_shop, nombre, razon_social, ruc, virtual_uri, domain, latitud, longitud, id_imagen, direccion, horarios, id_caja, telefono, categoria, celular);
                                //Se añade el objeto creado a la colección de tipo List<Tienda>.
                                pojoTienda.save(); //guardar en la DB
//                                Tienda note2 = Tienda.findById(Tienda.class, id_shop);
//                                Log.e("tag_pojosTiendas", note2.getRazon_social());
                                arrayTiendas.add(pojoTienda);
                            }

                        } catch (JSONException e) {
                            CategoriaTienda.deleteAll(CategoriaTienda.class);
                            Tienda.deleteAll(CategoriaTienda.class);
                            e.printStackTrace();
                            finish();
                            System.exit(0);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try
                {
                    CategoriaTienda.deleteAll(CategoriaTienda.class);
                    Tienda.deleteAll(CategoriaTienda.class);
                    Log.e("ERROR DE VOLLEY TIENDAS", "Error de petición de servicio: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Error de petición de servicio: " + error.toString(),Toast.LENGTH_LONG).show();
                    finish();
                    System.exit(0);
                } catch (Exception e)
                {
                    CategoriaTienda.deleteAll(CategoriaTienda.class);
                    Tienda.deleteAll(CategoriaTienda.class);
                    e.printStackTrace();
                    finish();
                    System.exit(0);
                }
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
        MySingleton.getInstance(this).addToRequestQueue(request);

    }

    //Verifica que haya conexion a internet

    public static boolean isOnline(Context context) {

        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No hay conexión a internet");
        builder.setMessage("Necesitas tener acceso a datos o wifi en tu Celular. Presiona en OK para salir");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }
}
