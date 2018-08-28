package com.scientechperu.tiendassc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.scientechperu.tiendassc.Clases.Carro;
import com.scientechperu.tiendassc.Clases.CategoriaTienda;
import com.scientechperu.tiendassc.Entendiendo.Utils;
import com.scientechperu.tiendassc.Fragmentos.FragmentCarrito;
import com.scientechperu.tiendassc.Fragmentos.FragmentoInicio;
import com.scientechperu.tiendassc.Fragmentos.PlaceholderFragment;

import java.util.ArrayList;
import java.util.List;

public class ActividadPrincipal extends AppCompatActivity{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);

        agregarToolbar();

        pd = ProgressDialog.show(this,"Cargando...","Porfavor espere...");

        try{
            Thread.sleep(2000);
        }catch(Exception e){

        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        //actualizar menu
        final Menu menu = navigationView.getMenu();
        int itemIdMenu = 1;
        menu.add(group1Id, Menu.FIRST, Menu.FIRST, "Inicio").setIcon(R.drawable.inicio);
        for (CategoriaTienda categoria : arrayCaTienda) {
            menu.add(group1Id + 1, itemIdMenu + 1, itemIdMenu + 1, categoria.getNombre()).setIcon(R.drawable.categorias);
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


    }

    private void agregarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
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
            fragment = new FragmentoInicio();
            pd.dismiss();
        }
        else{

//            CURRENT_FRAGMENT_TAG= title;

            currentView = itemDrawer.getItemId();
            // Enviar título como arguemento del fragmento
            Bundle args = new Bundle();
            args.putString(PlaceholderFragment.ARG_SECTION_TITLE, title);
            fragment = PlaceholderFragment.newInstance(title);
            fragment.setArguments(args);


        }
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedor_principal, fragment)
                    .commit();
        }
        drawerLayout.closeDrawers(); // Cerrar drawer

        setTitle(title); // Setear título actual

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_shop);
        // Obtener drawable del item

        LayerDrawable icon = (LayerDrawable) item.getIcon();
        // Actualizar el contador
        Utils.setBadgeCount(this, icon, (int) Carro.count(Carro.class));

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_shop:
                Bundle args = new Bundle();
                args.putString(FragmentCarrito.ARG_SECTION_TITLE, "Carrito");
                fragment = FragmentCarrito.newInstance("Carrito");
                fragment.setArguments(args);

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.contenedor_principal, fragment)
                        .commit();
                setTitle("Carrito"); // Setear título actual
//                Toast.makeText(getApplicationContext(), "Carrrioto", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * Muestra una {@link Snackbar} prefabricada
//     *
//     * @param msg Mensaje a proyectar
//     */
//    private void showSnackBar(String msg) {
//        Snackbar.make(findViewById(R.id.), msg, Snackbar.LENGTH_SHORT).show();
//    }




    /**
     * Este metodo es llamado cuando la pantalla gira y/o cambia de
     * tamaño(non-Javadoc) En el manifest se agrego la siguiente linea
     * "android:configChanges="orientation|screenSize" la cual este metodo
     * captura cada ves que la pantalla cambia
     *
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
//
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
////         cambia el view a lo que estaba antes de cambiar la orientación
////        setContentView(currentView);
////         Checks the orientation of the screen
//        if(fragment!=null && fragment.isResumed()){
//
//            //do nothing here if we're showing the fragment
//
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // otherwise lock in portrait
//
//        }
//        super.onConfigurationChanged(newConfig);
//
//    }
//
//    private boolean isCameraFragmentPost() {
//        Fragment fr = getSupportFragmentManager().findFragmentByTag(TAG_PIC_TAKING);
//        return (fr != null && fr.isResumed() && fr.getClass().isInstance(PlaceholderFragment.class));
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        String fragmentTag = fragmentManager.findFragmentById(R.id.contenedor_principal).getTag();
//        outState.putString("Frag", fragmentTag);
////        outState.putInt("Frag", currentView);
//
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        CURRENT_FRAGMENT_TAG = savedInstanceState.getString("Frag");
//        seleccionarItem(navigationView.getMenu().add(CURRENT_FRAGMENT_TAG));
//    }
}