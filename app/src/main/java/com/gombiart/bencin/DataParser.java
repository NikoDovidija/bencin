package com.gombiart.bencin;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataParser extends AsyncTask<Void, Void, Void> {

    private ArrayList<Station> stations = new ArrayList<>();

    private ArrayList<String> urlji = new ArrayList<>();
    private MapsActivity mapsActivity;

    public DataParser(MapsActivity mapsActivity){
       this.mapsActivity = mapsActivity;
    }

    private void getFromChildSite(Element link) throws MalformedURLException{

        URL naslov=new URL("http://www.petrol.si/"+link.attr("href"));

        try {
            org.jsoup.nodes.Document dok = Jsoup.connect(naslov.toString()).timeout(10*1000).get();

        } catch (IOException ex) {
            Logger.getLogger(DataParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    @Override
    protected Void doInBackground(Void... params) {
        org.jsoup.nodes.Document doc = null;
        try {
            System.out.println("//Poskušaj vzpostaviti povezavo z strežnikom");
            doc = Jsoup.connect("http://www.petrol.si/bencinski-servisi/seznam").timeout(10 * 1000).get();

            //Vrni samo tiste linke,ki v atributu href vsebujejo določeni string (/bencinski-servisi/podrobno/)
            Elements links = doc.select("a[href~=/bencinski-servisi/podrobno/?]");

            for(Element e : links){
                //Dodajanje k postajam
                //GasStation(URL url, float lat, float lng)
                mapsActivity.addGasStation(new GasStation(new URL("http://www.petrol.si/" + e.attr("href")), new Random().nextInt(180)- 90 * 1f, new Random().nextInt(360) - 180));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mapsActivity.done();
    }
}
