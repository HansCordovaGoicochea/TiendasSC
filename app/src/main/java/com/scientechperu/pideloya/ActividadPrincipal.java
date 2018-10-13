package com.scientechperu.pideloya;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.scientechperu.pideloya.Clases.CategoriaTienda;
import com.scientechperu.pideloya.Clases.Tienda;
import com.scientechperu.pideloya.Clases.UrlRaiz;
import com.scientechperu.pideloya.Clases.Usuario;
import com.scientechperu.pideloya.Fragmentos.FragmentEstacionamiento;
import com.scientechperu.pideloya.Fragmentos.FragmentPedido;
import com.scientechperu.pideloya.Fragmentos.FragmentoCarusel;
import com.scientechperu.pideloya.Fragmentos.PlaceholderFragment;
import com.scientechperu.pideloya.SingletonVolley.MySingleton;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class ActividadPrincipal extends AppCompatActivity {

    /*
     * La variable currentView sirve de "singleton" para saber en que vista se
     * habia quedado la pantalla antes de girarla de este modo, cada ves que
     * cambie la pantalla, puede volver a cargar la vista donde se encontraba en
     * lugar de la de default.
     */
    public int currentView;

    public static final String TAG = "caTiendas.WEBSERIVICES";
    private DrawerLayout drawerLayout;
    private int group1Id = 1;
    private ProgressDialog pd;
    NavigationView navigationView;
    Fragment fragment = null;
    FragmentManager fragmentManager;

    /*Se declara e inicializa una variable de tipo List que almacenará objetos de tipo Tienda*/
    List<CategoriaTienda> arrayCaTienda = CategoriaTienda.listAll(CategoriaTienda.class);
    CategoriaTienda pojoCaTienda;

    public String CURRENT_FRAGMENT_TAG;
    private static final int REQUEST_CALL_PHONE = 1;
    private static final int REQUEST_READ_CONTACTS = 2;
    private static final int REQUEST_WRITE_CONTACTS = 3;
    private static boolean isRationale = true;
    private static boolean isFirst = true;


    AlertDialog alertDialog;
    ProgressDialog progressDialog;

    private Button ingresar;
    private EditText dni_usuario;
    private EditText nombres_usuario;
    private EditText apellidos_usuario;
    private EditText email_usuario;
    private EditText celular_usuario;
    private EditText direccion_usuario;
    String _ID_CLIENTE_ = "";

    private SharedPreferences perfilUsuarioshared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);

        agregarToolbar();

        pd = ProgressDialog.show(this, "Cargando...", "Porfavor espere...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askPermissions(true);
        }
//        permisoCallPhone(); //permiso para hacer llamadas
//        permisoREAD_CONTACTS(); //permiso para hacer llamadas
//        permisoWRITE_CONTACTS(); //permiso para hacer llamadas
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView usuario = (TextView) headerView.findViewById(R.id.username);
        TextView email_user = (TextView) headerView.findViewById(R.id.email);
        usuario.setText("");
        email_user.setText("");
        perfilUsuarioshared = getApplicationContext().getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);
        if (perfilUsuarioshared.contains("_nombre")){

            String nombreshared = perfilUsuarioshared.getString("_nombre", null);
            String apellidoshared = perfilUsuarioshared.getString("_apellidos", null);
            String emailshared = perfilUsuarioshared.getString("_email", null);
            String celularshared = perfilUsuarioshared.getString("_celular", null);
            String direccionshared = perfilUsuarioshared.getString("_direccion", null);
            usuario.setText(nombreshared);
            email_user.setText(emailshared);
        }

//        Toast.makeText(getApplicationContext(), usuario.getText() +"---"+ email_user.getText(),Toast.LENGTH_LONG).show();


        //actualizar menu
        final Menu menu = navigationView.getMenu();
        int itemIdMenu = 1;
        menu.add(group1Id, Menu.FIRST, Menu.FIRST, "Inicio").setIcon(R.drawable.inicio);
        for (CategoriaTienda categoria : arrayCaTienda) {
            Integer icon_menu = R.drawable.categorias;
            switch (categoria.getNombre()){
                case "Pollerías":
                    icon_menu = R.drawable.pollerias;
                    break;
                case "Electrodomesticos":
                    icon_menu = R.drawable.electrodomesticos;
                    break;
                case "Tecnología":
                    icon_menu = R.drawable.tecnologia;
                    break;
                case "Estacionamientos":
                    icon_menu = R.drawable.estacionamientos;
                    break;
                default:
                    icon_menu = R.drawable.categorias;
                    break;
            }
            menu.add(group1Id + 1, itemIdMenu + 1, itemIdMenu + 1, categoria.getNombre()).setIcon(icon_menu);
            itemIdMenu++;
        }
        // adding a section and items into it
//        final SubMenu subMenu = menu.addSubMenu("SubMenu Title");
//        for (int i = 1; i <= 2; i++) {
//            subMenu.add("SubMenu Item " + i);
//        }

        menu.setGroupCheckable(1, true, true);
        menu.setGroupCheckable(2, true, true);

        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
            final View child = navigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }

        if (navigationView != null) {
            prepararDrawer(navigationView);
            // Seleccionar item por defecto
            seleccionarItem(navigationView.getMenu().getItem(0));
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActividadPrincipal.this);
        builder
                .setIcon(R.drawable.salir)
                .setTitle("¿Seguro de salir?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        ImageView imagenSalir = navigationView.findViewById(R.id.img_row_salir);
        imagenSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });


    }


    public void permisoCallPhone(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    public void permisoREAD_CONTACTS(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }
    public void permisoWRITE_CONTACTS(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_WRITE_CONTACTS);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    private void agregarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
//            ab.setDisplayShowHomeEnabled(true);
        }

    }


    private void prepararDrawer(NavigationView navigationView) {


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // Crear nuevo fragmento

                        seleccionarItem(menuItem);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    private void seleccionarItem(MenuItem itemDrawer) {

        fragmentManager = getSupportFragmentManager();

        // Marcar item presionado
        itemDrawer.setChecked(true);

//        Toast.makeText(getApplicationContext(), "Error de petición de servicio: " + error.toString(),Toast.LENGTH_LONG).show();

        String title = itemDrawer.getTitle().toString();
        if (title.equals("Inicio")){
            currentView = itemDrawer.getItemId();
//            CURRENT_FRAGMENT_TAG= title;
            fragment = new FragmentoCarusel();
            pd.dismiss();
        }
//        else if (title.equals("Estacionamientos")){
//            currentView = itemDrawer.getItemId();
////            CURRENT_FRAGMENT_TAG= title;
//            fragment = new FragmentEstacionamiento();
//            pd.dismiss();
//        }
        else{

//            CURRENT_FRAGMENT_TAG= title;

            currentView = itemDrawer.getItemId();
            // Enviar título como arguemento del fragmento
            Bundle args = new Bundle();
            args.putString(PlaceholderFragment.ARG_SECTION_TITLE, title);
            fragment = PlaceholderFragment.newInstance(title);
            fragment.setArguments(args);


        }

//        Log.e(TAG, fragment.getClass().getSimpleName());

        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedor_principal, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
        drawerLayout.closeDrawers(); // Cerrar drawer

        setTitle(title); // Setear título actual

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem item = menu.findItem(R.id.action_shop);
//        // Obtener drawable del item
//        LayerDrawable icon = (LayerDrawable) item.getIcon();
//        // Actualizar el contador
//        Utils.setBadgeCount(this, icon, (int) Carro.count(Carro.class));
//
//        return true;
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_shop);

//        View actionView = MenuItemCompat.getActionView(menuItem);


//        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
//        mCartItemCount = (int) Carro.count(Carro.class);
//        setupBadge(mCartItemCount);
//
//        actionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onOptionsItemSelected(menuItem);
//            }
//        });

        menu.add(0, 0, 0, menuIconWithText(getResources().getDrawable(R.drawable.perfil), "Editar Perfil"));
        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.cesta), "Ultimo Pedido"));
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.drawable.salir), "Salir"));

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

// You can hide the state of the menu item here if you call getActivity().supportInvalidateOptionsMenu(); somewhere in your code
        MenuItem pedido = menu.findItem(1);
        pedido.setVisible(false);
        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        FragmentTransaction fragmentTransaction2;

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case 0:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActividadPrincipal.this);
//                alertDialogBuilder.setCancelable(false);
                View login = getLayoutInflater().inflate(R.layout.login, null);
                alertDialogBuilder.setView(login);
                alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.setCanceledOnTouchOutside(false);



                progressDialog = new ProgressDialog(ActividadPrincipal.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Editando...");
                progressDialog.setCancelable(false);

                perfilUsuarioshared = getApplicationContext().getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);

                if (perfilUsuarioshared.contains("_nombre")){
                    //pass
                    alertDialog.show();
                    dni_usuario = login.findViewById(R.id.input_dni);
                    nombres_usuario = login.findViewById(R.id.input_name);
                    apellidos_usuario = login.findViewById(R.id.input_apellidos);
                    email_usuario = login.findViewById(R.id.input_email);
                    celular_usuario = login.findViewById(R.id.input_telefono);
                    direccion_usuario = login.findViewById(R.id.input_direccion);

                    String dnihared = perfilUsuarioshared.getString("_dni", null);
                    String nombreshared = perfilUsuarioshared.getString("_nombre", null);
                    String apellidoshared = perfilUsuarioshared.getString("_apellidos", null);
                    String emailshared = perfilUsuarioshared.getString("_email", null);
                    String celularshared = perfilUsuarioshared.getString("_celular", null);
                    String direccionshared = perfilUsuarioshared.getString("_direccion", null);

                    dni_usuario.setText(dnihared);
                    dni_usuario.setEnabled(false);
                    nombres_usuario.setText(nombreshared);
                    apellidos_usuario.setText(apellidoshared);
                    email_usuario.setText(emailshared);
                    celular_usuario.setText(celularshared);
                    direccion_usuario.setText(direccionshared);


                    ingresar = (Button) login.findViewById(R.id.btn_signup);
                    ingresar.setText("Actualizar");
                    ingresar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            signup();

                        }
                    });
                }

                return true;
            case 1:
                args.putString(FragmentPedido.ARG_SECTION_TITLE, "Ultimo Pedido");
                fragment = FragmentPedido.newInstance("Ultimo Pedido");
                fragment.setArguments(args);

                fragmentTransaction2 = fragmentManager.beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                fragmentTransaction2.replace(R.id.contenedor_principal, fragment, "FragmentPedido");
                fragmentTransaction2.addToBackStack(null);
                fragmentTransaction2.commit();
                setTitle("Ultimo Pedido"); // Setear título actual
//                Toast.makeText(getApplicationContext(), "Carrrioto", Toast.LENGTH_SHORT).show();
                return true;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setIcon(R.drawable.salir)
                        .setTitle("¿Seguro de salir?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
//                Toast.makeText(getApplicationContext(), "Saliste de la APP", Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.action_shop:
//                Bundle args = new Bundle();
//                args.putString(FragmentCarrito.ARG_SECTION_TITLE, "Carrito");
//                fragment = FragmentCarrito.newInstance("Carrito");
//                fragment.setArguments(args);
//
//                fragmentManager
//                        .beginTransaction()
//                        .replace(R.id.contenedor_principal, fragment)
//                        .commit();
//                setTitle("Carrito"); // Setear título actual
////                Toast.makeText(getApplicationContext(), "Carrrioto", Toast.LENGTH_SHORT).show();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void askPermissions(boolean isForOpen) {
        isRationale = false;
        List permissionsRequired = new ArrayList();

        final List<String> permissionsList = new ArrayList<String>();
//        if (!checkPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            permissionsRequired.add("Write External Storage");
        if (!checkPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsRequired.add("Call phone");
        if (!checkPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsRequired.add("Read phone state");
        if (!checkPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsRequired.add("Read Contacts");
//        if (!checkPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
//            permissionsRequired.add("Receive SMS");
//        if (!checkPermission(permissionsList, Manifest.permission.GET_ACCOUNTS))
//            permissionsRequired.add("Get Accounts");
        if (!checkPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsRequired.add("Location");
        if (!checkPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsRequired.add("Location");

        if (permissionsList.size() > 0 && !isRationale) {
            if (permissionsRequired.size() > 0) {

            }
            if (isForOpen) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                            11);
                }
            }

        } else if (isRationale) {
            if (isForOpen) {

                new android.support.v7.app.AlertDialog.Builder(this, R.style.Theme_TiendasSC)
                        .setTitle("Permission Alert")
                        .setMessage("You need to grant permissions manually. Go to permission and grant all permissions.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 123);
                            }
                        })
                        .show();
            }
        }
//        else {
//            startActivity(new Intent(PermissionsActivity.this, SplashActivity.class));
//            finish();
//        }
    }

    private boolean checkPermission(List permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!isFirst) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        isRationale = true;
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 11:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
//                    startActivity(new Intent(PermissionsActivity.this, SplashActivity.class));
//                    finish();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied.", Toast.LENGTH_SHORT)
                            .show();
                    isFirst = false;
                    askPermissions(true);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActividadPrincipal.this);
        alertDialogBuilder.setCancelable(false);
        View login = getLayoutInflater().inflate(R.layout.login, null);
        alertDialogBuilder.setView(login);
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.setCanceledOnTouchOutside(false);

        progressDialog = new ProgressDialog(ActividadPrincipal.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registrando...");
        progressDialog.setCancelable(false);

        perfilUsuarioshared = getApplicationContext().getSharedPreferences("PerfilUsuario", Context.MODE_PRIVATE);

        if (perfilUsuarioshared.contains("_nombre")){
            //pass
        }
        else{
            alertDialog.show();
            dni_usuario = login.findViewById(R.id.input_dni);
            nombres_usuario = login.findViewById(R.id.input_name);
            apellidos_usuario = login.findViewById(R.id.input_apellidos);
            email_usuario = login.findViewById(R.id.input_email);
            celular_usuario = login.findViewById(R.id.input_telefono);
            direccion_usuario = login.findViewById(R.id.input_direccion);


            dni_usuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // code to execute when EditText SALE focus
                        String _dni = dni_usuario.getText().toString();
                        if (!_dni.isEmpty()) {
                            Log.e(TAG, "ESTAS CONSULTANDO AL CLIENTE");
                            jsonCliente(UrlRaiz.domain + "/api/customers/" + UrlRaiz.ws_key + "&output_format=JSON&filter[num_document]="+dni_usuario.getText()+"&display=full");
                        }
                    }
                }
            });


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

        String _dni = dni_usuario.getText().toString();
        String _nombre = nombres_usuario.getText().toString();
        String _apellidos = apellidos_usuario.getText().toString();
        String _email = email_usuario.getText().toString();
        String _celular = celular_usuario.getText().toString();
        String _direccion = direccion_usuario.getText().toString();

        // TODO: Implement your own signup logic here.


        editor.putString("_dni", _dni);
        editor.putString("_nombre", _nombre);
        editor.putString("_apellidos", _apellidos);
        editor.putString("_email", _email);
        editor.putString("_celular", _celular);
        editor.putString("_direccion", _direccion);
        editor.apply();

//
        //guardar datos en la web
//        if (_ID_CLIENTE_.equals("")){
//            xmlSendCliente("POST", "", _nombre, _apellidos, _email, _celular, _direccion);
//        }else{
//            xmlSendCliente("PUT", _ID_CLIENTE_, _nombre, _apellidos, _email, _celular, _direccion);
//        }
        alertDialog.dismiss();
        progressDialog.dismiss();


        // para poder actualizar el fragment debemos de crear el fragmen con u TAG sino vas a estar sufriendo con esto
        Fragment fm = getSupportFragmentManager().findFragmentByTag("FragmentoCarusel");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(fm);
        ft.attach(fm);
        ft.commit();
    }



    public void onSignupFailed() {
        Toast.makeText(getApplicationContext(), "Llene los datos correctamente", Toast.LENGTH_LONG).show();
        ingresar.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String _dni = dni_usuario.getText().toString();
        String _nombre = nombres_usuario.getText().toString();
        String _apellidos = apellidos_usuario.getText().toString();
        String _email = email_usuario.getText().toString();
        String _celular = celular_usuario.getText().toString();
        String _direccion = direccion_usuario.getText().toString();

        if (_dni.isEmpty() || _dni.length() < 8) {
            dni_usuario.setError("Al menos 8 caracteres");
            valid = false;
        } else {
            dni_usuario.setError(null);
        }

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
    public void xmlSendCliente(final String metodo,final String idcustomer, final String nombres, final String apellidos, final String email, final String celular, final String direccion){



            Integer metodo_envio = Request.Method.PUT;

            if (metodo.equals("POST")){
                metodo_envio =  Request.Method.POST;
            }


            String url = UrlRaiz.domain + "/api/customers/" + UrlRaiz.ws_key + "&schema=blank";

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
                        String dni_db = num_document.item(0).getChildNodes().item(0).getNodeValue();
                        String nombres_db = nombres.item(0).getChildNodes().item(0).getNodeValue();
                        String apellidos_db = apellidos.item(0).getChildNodes().item(0).getNodeValue();
                        String email_db = email.item(0).getChildNodes().item(0).getNodeValue();
                        String phone_db = phone.item(0).getChildNodes().item(0).getNodeValue();
                        String address_db = address.item(0).getChildNodes().item(0).getNodeValue();
                        String id_shop_db = id_shop.item(0).getChildNodes().item(0).getNodeValue();


                        Usuario usuario = new Usuario(id, dni_db, nombres_db, apellidos_db, email_db, phone_db, address_db, id_shop_db);
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

                    String postData = generaXMLCliente(idcustomer, nombres, apellidos, email, celular, direccion); // TODO get your final output
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
            MySingleton.getInstance(ActividadPrincipal.this).addToRequestQueue(strReq);

    }

    public String generaXMLCliente(String id_cliente, String firstname, String lastname, String email, String celular, String direccion) {
        String url = UrlRaiz.domain+"/api/";

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
                "\t<id_shop>1</id_shop>\n" +
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

    //consultar al cliente
    private void jsonCliente(String url) {


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
                            String firstname = jsonChildNode.optString("firstname");
                            String lastname = jsonChildNode.optString("lastname");
                            String phone = jsonChildNode.optString("phone");
                            String address = jsonChildNode.optString("address");
//                            }
                            pd.dismiss();

//                            nombres_usuario.setText(id_customer);
                            _ID_CLIENTE_ = String.valueOf(id_customer);
                            email_usuario.setText(email);
                            nombres_usuario.setText(firstname);
                            apellidos_usuario.setText(lastname);
                            celular_usuario.setText(phone);
                            direccion_usuario.setText(address);

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
        MySingleton.getInstance(ActividadPrincipal.this).addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        forceUpdate();
    }

    // check version on play store and force update
    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion, ActividadPrincipal.this).execute();
    }
}