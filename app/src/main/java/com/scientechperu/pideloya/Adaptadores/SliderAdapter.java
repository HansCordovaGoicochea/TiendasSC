package com.scientechperu.pideloya.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scientechperu.pideloya.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;
    ImageView imagen;
    TextView texto;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    //Arrays

    public int[] slide_imagenes = {
            R.drawable.welcome,
            R.drawable.relagos,
            R.drawable.sopresas
    };
    public String[] slide_texto = {
            "¡Bienvenido! a ¡Pídelo Ya!",
            "Obtén Cupones de Regalo por tus Compras",
            "Y Muchas Sorpresas Más..."
    };

    @Override
    public int getCount() {
        return slide_texto.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return  view == (FrameLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
         layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view = layoutInflater.inflate(R.layout.slide_onboarding, container, false);

        imagen = view.findViewById(R.id.img);
        texto = view.findViewById(R.id.txtTexto);

        imagen.setImageResource(slide_imagenes[position]);
        texto.setText(slide_texto[position]);

        container.addView(view);

        ViewCompat.animate(imagen)
                .translationY(250)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        ViewCompat.animate(texto)
                .translationY(50).alpha(1)
                .setStartDelay((ITEM_DELAY) + 500)
                .setDuration(1000).setInterpolator(
                new DecelerateInterpolator()).start();

//        for (int i = 0; i < container.getChildCount(); i++) {
//            View v = container.getChildAt(i);
//            ViewPropertyAnimatorCompat viewAnimator = null;
//
//            if (!(v instanceof Button)) {
//                viewAnimator = ViewCompat.animate(v)
//                        .translationY(50).alpha(1)
//                        .setStartDelay((ITEM_DELAY * i) + 500)
//                        .setDuration(1000);
//            } else {
//                viewAnimator = ViewCompat.animate(v)
//                        .scaleY(1).scaleX(1)
//                        .setStartDelay((ITEM_DELAY * i) + 500)
//                        .setDuration(500);
//            }
//            if (viewAnimator != null){
//                viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
//            }
//
//
//        }

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);

    }


}
