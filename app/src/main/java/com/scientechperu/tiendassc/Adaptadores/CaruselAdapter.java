package com.scientechperu.tiendassc.Adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scientechperu.tiendassc.Clases.Tienda;
import com.scientechperu.tiendassc.Clases.UrlRaiz;
import com.scientechperu.tiendassc.R;

import java.util.List;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class CaruselAdapter extends RecyclerView.Adapter<CaruselAdapter.ViewHolder> {

    private List<Tienda> data;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url_imagen = UrlRaiz.domain + "/api/images/stores/"+data.get(position).getId_store()+""+UrlRaiz.ws_key;

        Glide.with(holder.itemView.getContext())
                .load(url_imagen)
//                .apply(RequestOptions.circleCropTransform())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
