package com.scientechperu.tiendassc.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.scientechperu.tiendassc.Clases.CuponesClientes;
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


public class CuponesAdapter extends RecyclerView.Adapter<CuponesAdapter.ViewHolder>{

    private static final String TAG = CuponesAdapter.class.getSimpleName();
    //    ArrayList<String> nameList = new ArrayList<String>();
    private List<Tienda> nameList;

    ArrayList<String> image = new ArrayList<String>();
    private ArrayList<Integer> counter = new ArrayList<Integer>();
    private ArrayList<CuponesClientes> detalleOrders;
    private Context context;

    private Usuario usuario;
    Tienda tienda;


    public CuponesAdapter(Context context,
                          List<Tienda> nameList) {

        this.nameList = nameList;
//        this.itemNameList = itemNameList;
        this.context = context;

        Log.e("namelist", nameList.toString());

        for (int i = 0; i < nameList.size(); i++) {
            counter.add(0);
        }
    }

    // seinicializa los inputs y otros elementos de la vista
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, direccion;
        ImageButton dropBtn;
        ImageView image;
        RecyclerView cardRecyclerView;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryTitle);
            direccion = itemView.findViewById(R.id.categoryDescription);
//            dropBtn = itemView.findViewById(R.id.categoryExpandBtn);
            cardRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
            cardView = itemView.findViewById(R.id.cardView);
            image = itemView.findViewById(R.id.imagen_tienda);

        }

        @Override
        public void onClick(View view) {

        }
    }

    @NonNull
    @Override
    public CuponesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tienda_cupon_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CuponesAdapter.ViewHolder holder, final int position) {

        holder.name.setText(nameList.get(position).getNombre());
        holder.direccion.setText(nameList.get(position).getDireccion());

        String url_imagen = UrlRaiz.domain + "/api/images/stores/"+nameList.get(position).getId_store()+""+UrlRaiz.ws_key;
        Log.e("imagenes", url_imagen);

        Glide.with(holder.itemView.getContext())
                .load(url_imagen)
//                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);


//        CuponClienteAdapter cuponClienteAdapter = new CuponClienteAdapter(itemNameList.get(position));


        holder.cardRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//        holder.cardRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        String[] argusu = {
                String.valueOf(nameList.get(position).getId_shop())
        };

        final List<Usuario> usuarios = Usuario.find(Usuario.class, "idshop = ?", argusu);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tienda = nameList.get(position);
                if (usuarios.size() > 0) {
                    usuario = usuarios.get(0);

                    jsonCupones(UrlRaiz.domain + "/" + nameList.get(position).getVirtual_uri() + "api/cuponesclientes/" + UrlRaiz.ws_key + "&output_format=JSON&filter[id_cliente]=" + usuario.getIdcustomer() + "&filter[acumulado]=1.00&filter[active]=1&display=full&sort=[id_DESC]", holder);
                }else{
                    Toast.makeText(context, "No has ingresado a esta tienda", Toast.LENGTH_SHORT).show();
                }

                if (counter.get(position) % 2 == 0) {
                    holder.cardRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    holder.cardRecyclerView.setVisibility(View.GONE);
                }
//
                counter.set(position, counter.get(position) + 1);


            }
        });

//
//        holder.cardRecyclerView.setAdapter(cuponClienteAdapter);

    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    //consultar order
    private void jsonCupones(String url, final ViewHolder holder) {
        Log.e(TAG, "Llego al json Cupones detalles");
        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            detalleOrders = new ArrayList<>();

//                            Log.e(TAG, response.toString());
                            JSONArray jsonMainNode = response.getJSONArray("cupon_clientes");
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                String id= jsonChildNode.optString("id");
                                String id_shop= jsonChildNode.optString("id_shop");
                                String id_cliente= jsonChildNode.optString("id_cliente");
                                String id_producto= jsonChildNode.optString("id_producto");
                                String acumulado= jsonChildNode.optString("acumulado");
                                String terminos_condiciones= jsonChildNode.optString("terminos_condiciones");
                                String nombre_producto= jsonChildNode.optString("nombre_producto");
                                String id_imagen_default= jsonChildNode.optString("id_imagen_producto");
                                String date_add= jsonChildNode.optString("date_add");
                                String date_upd = jsonChildNode.optString("date_upd");


                                detalleOrders.add(new CuponesClientes(id, id_shop, id_cliente, id_producto, acumulado, terminos_condiciones,nombre_producto, id_imagen_default,
                                        date_add,date_upd, tienda.getId_caja().toString()));
                            }

                            CuponClienteAdapter cuponClienteAdapter = new CuponClienteAdapter(detalleOrders);
                            holder.cardRecyclerView.setAdapter(cuponClienteAdapter);

//                            holder.cardRecyclerView.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());

                Toast.makeText(context,"No tienes ningun cupon en esta tienda Gracias!",Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
