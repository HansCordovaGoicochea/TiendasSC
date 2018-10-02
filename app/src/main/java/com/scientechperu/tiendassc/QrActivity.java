package com.scientechperu.tiendassc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.Clases.Usuario;
import com.scientechperu.tiendassc.Clases.Vehiculos;
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class QrActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = QrActivity.class.getSimpleName();
    Vehiculos vehiculos;
    ImageView imageViewQrCode;
    ProgressDialog pd;

    Tienda tienda;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_qr);

        findViewById(R.id.home).setOnClickListener(this);
        imageViewQrCode = findViewById(R.id.imagen_QR);


        Intent intent = getIntent();
        String idvehiculo = intent.getStringExtra("idvehiculo");
//        Toast.makeText(getApplicationContext(), idvehiculo.toString(),Toast.LENGTH_SHORT).show();

        vehiculos = Vehiculos.findById(Vehiculos.class, Integer.valueOf(idvehiculo));

        pd = new ProgressDialog(QrActivity.this);
        pd.setIndeterminate(true);
        pd.setTitle("Cargando QR...");
        pd.setMessage("Porfavor espere...");
        pd.setCancelable(false);

        // To load the data at a later time
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("CargarProductos", Context.MODE_PRIVATE);
        Integer idtienda_current = prefs.getInt("id_shop", 0);
        Integer idcaja_current = prefs.getInt("id_caja", 0);

        String[] argwheretienda = {
                String.valueOf(idtienda_current),
                String.valueOf(idcaja_current),
        };

        List<Tienda> tiendas = Tienda.find(Tienda.class, "idshop = ? and idcaja = ?", argwheretienda);
        tienda = tiendas.get(0);

        List<Usuario> usuarios = Usuario.find(Usuario.class, "idshop = ?","" + idtienda_current);
        usuario = usuarios.get(0);

    }
    @Override
    public void onStart() {
        super.onStart();
        pd.show();
//        generarQR();
    }
    @Override
    protected void onResume()
    {
        super.onResume();

//        jsonVehiculoId(UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/vehiculos/" + UrlRaiz.ws_key + "&output_format=JSON&filter[placa]="+vehiculos.getPlaca()+"&display=full");

        generarQR();


    }

    private void generarQR() {
        String texto_qr = vehiculos.getPlaca()+"|"+vehiculos.getIdcustomer();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {

            //setting parameters for qr code
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); /* default = 4 */
//            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix bitMatrix = multiFormatWriter.encode(texto_qr, BarcodeFormat.PDF_417, 800, 700, hints);
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLUE :  Color.WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageViewQrCode.setImageBitmap(bitmap);

        } catch(Exception e) {

        }

//        try {
//            //setting size of qr code
//            int width =600;
//            int height = 600;
//            int smallestDimension = width < height ? width : height;
//
//            //setting parameters for qr code
//            String charset = "UTF-8";
//            Map<EncodeHintType, ErrorCorrectionLevel> hintMap =new HashMap<>();
//            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//            CreateQRCode(texto_qr, charset, hintMap, smallestDimension, smallestDimension);
//
//        } catch (Exception ex) {
//            Log.e("QrGenerate",ex.getMessage());
//            pd.dismiss();
//        }

        pd.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home:
                finish();
                break;
//            case R.id.fab_share:
//                share(v);
//                break;
        }
    }

    public  void CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth){


        try {
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            //generating qr code.
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hints);

            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //for black and white
                    //pixels[offset + x] = matrix.get(x, y) ? R.color.md_black_1000 : R.color.md_white_1000;
                    //for custom color
//                    pixels[offset + x] = matrix.get(x, y) ?
//                            ResourcesCompat.getColor(getResources(), R.color.md_blue_400, null) : R.color.md_white_1000;
                    pixels[offset + x] = matrix.get(x, y) ?  Color.BLUE :  Color.WHITE;
                }
            }
            //creating bitmap
//            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            //getting the logo
            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            //setting bitmap to image view

            imageViewQrCode.setImageBitmap(mergeBitmaps(overlay,bitmap));

        }catch (Exception er){
            Log.e("QrGenerate",er.getMessage());
        }
    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth  - overlay.getWidth()) /2;
        int centreY = (canvasHeight - overlay.getHeight()) /2 ;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    public String generaXMLCliente(String id_vehiculo, String placa, String modelo, String marca, String tipo_vehiculo, String fecha_adquisicion, String color, String tipo_combustible) {
        String url = UrlRaiz.domain+"/"+tienda.getVirtual_uri()+"api/";

        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "<vehiculos>\n" +
                "\t<id>"+id_vehiculo+"</id>\n" +
                "\t<id_shop>"+tienda.getId_shop()+"</id_shop>\n" +
                "\t<id_customer>"+usuario.getIdcustomer()+"</id_customer>\n" +
                "\t<placa>%s</placa>\n" +
                "\t<modelo>%s</modelo>\n" +
                "\t<marca>%s</marca>\n" +
                "\t<tipo_vehiculo>%s</tipo_vehiculo>\n" +
                "\t<fecha_adquisicion>%s</fecha_adquisicion>\n" +
                "\t<color>%s</color>\n" +
                "\t<tipo_combustible>%s</tipo_combustible>\n" +
                "\t<estado>0</estado>\n" +
                "\t<date_add></date_add>\n" +
                "\t<date_upd></date_upd>\n" +
                "</vehiculos>\n" +
                "</prestashop>";

        return String.format(format, placa, modelo, marca, tipo_vehiculo, fecha_adquisicion, color, tipo_combustible);
    }

    //consultar al cliente
    private void jsonVehiculo(String metodo, final String id_vehiculo, final String placa, final String modelo, final String marca, final String tipo_vehiculo, final String fecha_adquisicion, final String color, final String tipo_combustible) {

            Integer metodo_envio = Request.Method.PUT;

            if (metodo.equals("POST")){
                metodo_envio =  Request.Method.POST;
            }

            String url = UrlRaiz.domain + "/" + tienda.getVirtual_uri() + "api/customers/" + UrlRaiz.ws_key + "&schema=blank";

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

                        String id = idItem.item(0).getChildNodes().item(0).getNodeValue();

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

                    String postData = generaXMLCliente(id_vehiculo, placa, modelo, marca, tipo_vehiculo, fecha_adquisicion, color, tipo_combustible); // TODO get your final output
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
            MySingleton.getInstance(QrActivity.this).addToRequestQueue(strReq);
    }

    //consultar al cliente
    private void jsonVehiculoId(String url) {


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
//                            JSONArray jsonMainNode = response.getJSONArray("customers");
//
//                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
//                            Integer id_customer = jsonChildNode.optInt("id");
//                            String firstname = jsonChildNode.optString("firstname");
//                            String lastname = jsonChildNode.optString("lastname");
//                            String phone = jsonChildNode.optString("phone");
//                            String address = jsonChildNode.optString("address");


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
        MySingleton.getInstance(QrActivity.this).addToRequestQueue(request);
    }

}
