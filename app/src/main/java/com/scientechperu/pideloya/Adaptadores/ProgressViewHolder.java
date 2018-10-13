package com.scientechperu.pideloya.Adaptadores;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.scientechperu.pideloya.R;


public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public ProgressViewHolder(View v2) {
        super(v2);
        progressBar = (ProgressBar) v2.findViewById(R.id.progressBar);
    }
}
