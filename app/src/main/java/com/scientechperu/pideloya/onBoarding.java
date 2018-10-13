package com.scientechperu.pideloya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scientechperu.pideloya.Adaptadores.SliderAdapter;

public class onBoarding extends AppCompatActivity {

    private ViewPager mSliderPager;
    private LinearLayout mDotLayout;

    SliderAdapter slideAdapter;

    TextView[] dots;

    Button siguiente, saltar;

    int mCurrentPage;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_on_boarding);

        mSliderPager = findViewById(R.id.sliderViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);

        siguiente = findViewById(R.id.nextBoton);
        saltar = findViewById(R.id.saltarBoton);




        slideAdapter = new SliderAdapter(this);

        mSliderPager.setAdapter(slideAdapter);

        addDotsInicializar(0);

        mSliderPager.addOnPageChangeListener(vListener);


        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mSliderPager.setCurrentItem(mCurrentPage +1);

//                Toast.makeText(onBoarding.this, mCurrentPage+"", Toast.LENGTH_SHORT).show();
                if(mSliderPager.getCurrentItem() == 2) {
                    // The last screen
                    finishOnboarding();
                    //
                } else {
                    mSliderPager.setCurrentItem(mCurrentPage + 1, true);
                }

            }
        });

        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finishOnboarding();
            }
        });


    }

    public void addDotsInicializar(int position) {
        dots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.md_white_1000));

            mDotLayout.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.md_blue_400));
        }
    }


    ViewPager.OnPageChangeListener vListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsInicializar(position);
            mCurrentPage = position;

            if (position == 0){
                siguiente.setEnabled(true);
                saltar.setEnabled(false);

                saltar.setVisibility(View.INVISIBLE);

                siguiente.setText("Siguiente");
                saltar.setText("");

            } else if (position == dots.length -1){
                siguiente.setEnabled(true);
                saltar.setEnabled(true);

                saltar.setVisibility(View.VISIBLE);

                siguiente.setText("Salir");
                saltar.setText("Saltar");

            }else{
                siguiente.setEnabled(true);
                saltar.setEnabled(true);

                saltar.setVisibility(View.VISIBLE);

                siguiente.setText("Siguiente");
                saltar.setText("Saltar");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



    private void finishOnboarding() {
        // Get the shared preferences
        SharedPreferences preferences = getSharedPreferences("preference_onboarding", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit()
                .putBoolean("onboarding_complete",true).apply();

        // Launch the main Activity, called MainActivity
        Intent main = new Intent(this, ActividadPrincipal.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("focus", hasFocus+"");
        super.onWindowFocusChanged(hasFocus);
    }
}
