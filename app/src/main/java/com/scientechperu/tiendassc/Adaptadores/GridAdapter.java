package com.scientechperu.tiendassc.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.scientechperu.tiendassc.ActividadPrincipal;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Clases.Usuario;
import com.scientechperu.tiendassc.Fragmentos.FragmentEstacionamiento;
import com.scientechperu.tiendassc.Fragmentos.FragmentProductos;
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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * {@link BaseAdapter} personalizado para el gridview
 */
public class GridAdapter extends BaseAdapter {

    private static final String TAG = GridAdapter.class.getSimpleName();
    private final Context mContext;
    private final List<Tienda> items;
    ImageView image;
    RequestOptions options ;


    private SharedPreferences sharedPref;
    private SharedPreferences perfilUsuarioshared;
    AppCompatActivity activity;
    Tienda item;


    private String dnishared;
    private String nombreshared;
    private String apellidoshared;
    private String emailshared;
    private String celularshared;
    private String direccionshared;

    public GridAdapter(Context c, List<Tienda> items) {
        mContext = c;
        this.items = items;
    }




    @Override
    public int getCount() {
        // Decremento en 1, para no contar el header view
        return items.size();
    }

    @Override
    public Tienda getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
        }

        item = getItem(position);

        // Seteando Imagen
        image = (ImageView) view.findViewById(R.id.imagen);

        // load image from the internet using Glide
        String url_imagen = UrlRaiz.domain + "/api/images/stores/"+item.getId_store()+""+UrlRaiz.ws_key;


        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.homeprogress);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE); // because file name is always same
//                .placeholder(R.drawable.noimage);

        Glide.with(view.getContext())
                .load(url_imagen)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);

//
//        // Seteando Nombre
        TextView name = (TextView) view.findViewById(R.id.nombre);
        name.setText(item.getNombre());
//
//        // Seteando Direccion
        TextView direccion = (TextView) view.findViewById(R.id.direccion);
        direccion.setText(item.getDireccion());
//
        // Seteando Precio
        TextView telefono = (TextView) view.findViewById(R.id.telefono);
        telefono.setText(item.getTelefono());

//        // Seteando Rating
//        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);
//        ratingBar.setRating(item.getRating());

        final CardView cardView = (CardView) view.findViewById(R.id.cardTienda);



        cardView.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                cardView.setCardBackgroundColor(R.color.color_light);

                item = getItem(position);

                // Enviar URL como argumento del fragmento

                activity = (AppCompatActivity) v.getContext();


                // To load the data at a later time
                SharedPreferences prefs = activity.getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("id_shop", item.getId_shop());
                editor.putInt("id_caja", item.getId_caja());
                editor.apply();


                Bundle args = new Bundle();

                if (activity.getTitle().equals("Estacionamientos")){
                    Fragment fragmentEstacionamiento;

                    args.putString(FragmentEstacionamiento.ARG_SECTION_URL, UrlRaiz.domain+"/"+item.getVirtual_uri()+"api/products"+UrlRaiz.ws_key+"&output_format=JSON&filter[active]=1&filter[id_caja]="+item.getId_caja()+"&display=full&price[precio_con_igv][use_tax]=1");
                    fragmentEstacionamiento = FragmentEstacionamiento.newInstance("Mis Vehículos");
                    fragmentEstacionamiento.setArguments(args);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, fragmentEstacionamiento, "FragmentEstacionamiento").addToBackStack(null).commit();
                    activity.setTitle(FragmentEstacionamiento.ARG_SECTION_TITLE);



                    perfilUsuarioshared = activity.getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);
                    dnishared = perfilUsuarioshared.getString("_dni", null);
                    nombreshared = perfilUsuarioshared.getString("_nombre", null);
                    apellidoshared = perfilUsuarioshared.getString("_apellidos", null);
                    emailshared = perfilUsuarioshared.getString("_email", null);
                    celularshared = perfilUsuarioshared.getString("_celular", null);
                    direccionshared = perfilUsuarioshared.getString("_direccion", null);

                    String[] vals = {
                            String.valueOf(item.getId_shop())
                    };
                    if((int) Usuario.count(Usuario.class, "idshop = ?", vals) == 0){
                        //guardar datos en la web
                        jsonCliente(UrlRaiz.domain +"/"+item.getVirtual_uri()+"/api/customers/" + UrlRaiz.ws_key + "&output_format=JSON&filter[num_document]="+dnishared+"&filter[id_shop]="+item.getId_shop()+"&display=full");
                    }

                }else{
                    Fragment fragmentProductos;
                    args.putString(FragmentProductos.ARG_SECTION_URL, UrlRaiz.domain+"/"+item.getVirtual_uri()+"api/products"+UrlRaiz.ws_key+"&output_format=JSON&filter[active]=1&filter[id_caja]="+item.getId_caja()+"&display=full&price[precio_con_igv][use_tax]=1");
                    fragmentProductos = FragmentProductos.newInstance("Productos");
                    fragmentProductos.setArguments(args);

                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, fragmentProductos).addToBackStack(null).commit();
                    activity.setTitle(FragmentProductos.ARG_SECTION_TITLE);
                }



            }
        });

        return view;
    }


    public void xmlSendCliente(final String metodo,final String idcustomer,final String dnishared, final String nombres, final String apellidos, final String email, final String celular, final String direccion){



            Integer metodo_envio = Request.Method.PUT;

            if (metodo.equals("POST")){
                metodo_envio =  Request.Method.POST;
            }

            String[] argwheretienda = {
                    String.valueOf(item.getId_shop()),
                    String.valueOf(item.getId_caja()),
            };


            String url = UrlRaiz.domain + "/" + item.getVirtual_uri() + "api/customers/" + UrlRaiz.ws_key + "&schema=blank";

            StringRequest strReq = new StringRequest(metodo_envio,
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
                        NodeList num_document = root.getElementsByTagName("num_document");
                        NodeList nombres = root.getElementsByTagName("firstname");
                        NodeList apellidos = root.getElementsByTagName("lastname");
                        NodeList email = root.getElementsByTagName("email");
                        NodeList address = root.getElementsByTagName("address");
                        NodeList phone = root.getElementsByTagName("phone");
                        NodeList id_shop = root.getElementsByTagName("id_shop");

                        String id = idItem.item(0).getChildNodes().item(0).getNodeValue();
                        String num_document_db = num_document.item(0).getChildNodes().item(0).getNodeValue();
                        String nombres_db = nombres.item(0).getChildNodes().item(0).getNodeValue();
                        String apellidos_db = apellidos.item(0).getChildNodes().item(0).getNodeValue();
                        String email_db = email.item(0).getChildNodes().item(0).getNodeValue();
                        String phone_db = phone.item(0).getChildNodes().item(0).getNodeValue();
                        String address_db = address.item(0).getChildNodes().item(0).getNodeValue();
                        String id_shop_db = id_shop.item(0).getChildNodes().item(0).getNodeValue();


                        Usuario usuario = new Usuario(id, num_document_db, nombres_db, apellidos_db, email_db, phone_db, address_db, id_shop_db);
                        usuario.save();
//                    SharedPreferences.Editor editor = perfilUsuarioshared.edit();
//                    editor.putString("_idcustomer", id);
//                    editor.apply();

//                    Toast.makeText(getActivity().getBaseContext(), id, Toast.LENGTH_LONG).show();

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e(TAG, "Error: " + error.getMessage());
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/xml; charset=" + getParamsEncoding();
                }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    String postData = generaXMLCliente(idcustomer, dnishared, nombres, apellidos, email, celular, direccion); // TODO get your final output
//                    Log.e(TAG, postData);
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
            MySingleton.getInstance(activity).addToRequestQueue(strReq);

    }

    public String generaXMLCliente(String id_cliente, String dnishared, String firstname, String lastname, String email, String celular, String direccion) {
        String url = UrlRaiz.domain+"/"+item.getVirtual_uri()+"api/";

        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<customer>\n" +
                "\t<id>"+id_cliente+"</id>\n" +
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
                "\t<optin>1</optin>\n" +
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
                "\t<id_shop>"+item.getId_shop()+"</id_shop>\n" +
                "\t<id_shop_group></id_shop_group>\n" +
                "\t<date_add></date_add>\n" +
                "\t<date_upd></date_upd>\n" +
                "\t<phone>%s</phone>" +
                "\t<address>%s</address>\n" +
                "\t<id_document>1</id_document>\n" +
                "\t<num_document>"+dnishared+"</num_document>\n" +
                "<associations></associations>\n" +
                "</customer>\n" +
                "</prestashop>";

        return String.format(format, firstname, lastname, email, celular, direccion);
    }

    //consultar al cliente
    private void jsonCliente(String url) {

        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray jsonMainNode = response.getJSONArray("customers");

//                            for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                            Integer id_customer = jsonChildNode.optInt("id");
                            String email = jsonChildNode.optString("email");
                            String doc_number = jsonChildNode.optString("num_document");
                            String firstname = jsonChildNode.optString("firstname");
                            String lastname = jsonChildNode.optString("lastname");
                            String phone = jsonChildNode.optString("phone");
                            String address = jsonChildNode.optString("address");
//                            }

                            xmlSendCliente("PUT", ""+id_customer, doc_number,firstname, lastname, email, phone, address);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(),"El servidor ha tardado demasiado tiempo en responder",Toast.LENGTH_SHORT).show();
                xmlSendCliente("POST", "", dnishared,nombreshared, apellidoshared, emailshared, celularshared, direccionshared);

                VolleyLog.e(TAG, "Error: " + error.getMessage());
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
        MySingleton.getInstance(activity).addToRequestQueue(request);
    }

}