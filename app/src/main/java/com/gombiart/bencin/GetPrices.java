package com.gombiart.bencin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Handler;
import java.util.logging.LogRecord;


public class GetPrices extends AsyncTask<String, String, String> {

    URL url;
    TextView v;
    String linkCena;
    MapsActivity c;
    GasStation gs;
    Bitmap b;


    public GetPrices(URL url, TextView v, MapsActivity c, GasStation gs){
        this.url = url;
        this.v = v;
        this.c = c;
        this.gs = gs;
    }


    @Override
    protected String doInBackground(String... params) {

        try {


            org.jsoup.nodes.Document dok = Jsoup.connect(url.toString()).timeout(10*1000).get();

            linkCena = "http://www.petrol.si"+dok.select("img[src~=/sites/www.petrol.si/files/bencinskiServisi/?]").attr("src");
            System.out.println(linkCena);
            InputStream in = new java.net.URL(linkCena).openStream();
            b = BitmapFactory.decodeStream(in);



        } catch (IOException ie){
            ie.printStackTrace();
        }

        return linkCena;
    }

    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        try {
            gs.setPricesImg(s);
            gs.setPrices(b);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

}
