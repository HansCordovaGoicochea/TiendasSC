package com.scientechperu.pideloya;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

    private String latestVersion;
    private String currentVersion;
    private Context context;
    public ForceUpdateAsync(String currentVersion, Context context){
        this.currentVersion = currentVersion;
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        try {
//            Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=es")
            latestVersion = Jsoup.connect("http://scientechperu.com")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("span#versionAPP")
//                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();
//            Document doc = Jsoup.connect("http://testsc.corporacioncatto.com").get();
//            latestVersion = doc.getElementsByClass("app-gratis").get(6).text();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(latestVersion!=null){
            if(!currentVersion.equalsIgnoreCase(latestVersion)){
                // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
                if(!(context instanceof Splash)) {
                    if(!((Activity)context).isFinishing()){
                        showForceUpdateDialog();
                    }
                }
            }
        }
        super.onPostExecute(jsonObject);
    }

    public void showForceUpdateDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setCancelable(false);
        View view_dialog = View.inflate(context, R.layout.actualizacion_disponible, null);

        alertDialogBuilder.setView(view_dialog);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button actualizar = (Button) view_dialog.findViewById(R.id.buton_actualizar);

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
//                dialog.cancel();
            }
        });

        alertDialog.show();

    }
}
