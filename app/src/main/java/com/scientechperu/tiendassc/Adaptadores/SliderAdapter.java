package com.scientechperu.tiendassc.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scientechperu.tiendassc.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    //Arrays

    public int[] slide_imagenes = {
            R.drawable.confeti,
            R.drawable.relagos,
            R.drawable.sopresas
    };
    public String[] slide_texto = {
            "¡BIENVENIDO!",
            "Cupones de Regalo",
            "Y Muchas Sorpresas Más..."
    };

    @Override
    public int getCount() {
        return slide_texto.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return  view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
         layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view = layoutInflater.inflate(R.layout.slide_onboarding, container, false);

        ImageView imagen = view.findViewById(R.id.img);
        TextView texto = view.findViewById(R.id.txtTexto);

        imagen.setImageResource(slide_imagenes[position]);
        texto.setText(slide_texto[position]);

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
