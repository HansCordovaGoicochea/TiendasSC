package com.scientechperu.pideloya.Adaptadores;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.scientechperu.pideloya.Clases.Tienda;
import com.scientechperu.pideloya.Clases.UrlRaiz;
import com.scientechperu.pideloya.LlegarUbicacionTienda;
import com.scientechperu.pideloya.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class CaruselAdapter extends RecyclerView.Adapter<CaruselAdapter.ViewHolder>{

    private List<Tienda> data;


    // LogCat tag
    private static final String TAG = CaruselAdapter.class.getSimpleName();

   private LocationManager locationManager;
    double longitudeBest, latitudeBest;

    private FusedLocationProviderClient mFusedLocationClient;

    public CaruselAdapter(List<Tienda> data) {
        this.data = data;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_carusel_card, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String url_imagen = UrlRaiz.domain + "/api/images/stores/"+data.get(position).getId_store()+""+UrlRaiz.ws_key;

        Glide.with(holder.itemView.getContext())
                .load(url_imagen)
//                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);

//        Glide.with(holder.itemView.getContext())
////                .load(R.drawable.noimage)
//////                .apply(RequestOptions.circleCropTransform())
////                .into(holder.image_ubi);
        locationManager = (LocationManager) holder.itemView.getContext().getSystemService(Context.LOCATION_SERVICE);

        holder.image_ubi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationManager = (LocationManager) holder.itemView.getContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                locationManager.removeUpdates(locationListenerGPS);

                if (ActivityCompat.checkSelfPermission(holder.itemView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(holder.itemView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "ERROR DE PERMISOS");
                }

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(holder.itemView.getContext());


                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
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
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener((Activity) holder.itemView.getContext(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
//                                    // Logic to handle location object
                                    Log.e(TAG, location.getLatitude() + "");
                                    Log.e(TAG, location.getLongitude() + "");

//                Toast.makeText(holder.itemView.getContext(), lati+" ++++++ " + longi, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                            Uri.parse("http://maps.google.com/maps?saddr="+location.getLatitude()+","+location.getLongitude()+"&daddr="+data.get(position).getLatitud()+","+data.get(position).getLongitud()+""));
//                                            Uri.parse("http://maps.google.com/maps?saddr=-7.1542349,-78.5123291&daddr="+data.get(position).getLatitud()+","+data.get(position).getLongitud()+""));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addCategory(Intent.CATEGORY_LAUNCHER );
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    holder.itemView.getContext().startActivity(intent);
//                 Intent intent =new Intent(holder.itemView.getContext(), LlegarUbicacionTienda.class);
////                intent.putExtra("idvehiculo", vehiculos.get(getAdapterPosition()).getId().toString());
//                holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });


            }
        });


    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private final android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
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


    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ImageView image_ubi;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            image_ubi = (ImageView) itemView.findViewById(R.id.image_ubi);
        }
    }
}
