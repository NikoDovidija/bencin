package com.gombiart.bencin;


import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.json.JSONObject;

public class DataParserV2 extends AsyncTask<Void, Void, Void>{
    MapsActivity mainMap;
    String naslov;
    float kordx,kordy;
    URL link;
    ArrayList<Station> stations = new ArrayList<>();

    public DataParserV2(MapsActivity mainMap, String naslov){
        this.mainMap = mainMap;
        this.naslov = naslov;

    }

    private String toName(String string){
        Pattern pattern = Pattern.compile("<h2>(.+?)</h2");
        Matcher matcher = pattern.matcher(string);
        matcher.find();
        String niz =matcher.group(1);
        pattern=Pattern.compile(">(.+?)</a>");
        matcher = pattern.matcher(niz);
        matcher.find();
        return matcher.group(1);

    }

    private float[] toKordinata(String str){

        String [] kord = str.split("\\),\\(|\\)|\\(");
        String[] kordinati = kord[2].split(",");
        kordx=Float.parseFloat(kordinati[0]);
        kordy=Float.parseFloat(kordinati[1]);
        return new float[] {kordx,kordy};
    }

    private String toChildLink(String pot){
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher m = p.matcher(pot);
        String url = null;
        if (m.find()) {
            return this.link+m.group(1);
        }
        return "";
    }
    private String toNaslov(String string){
        final Pattern pattern = Pattern.compile("<div class=\"field-content\">(.+?)</div>");
        final Matcher matcher = pattern.matcher(string);
        matcher.find();
        return matcher.group(1);
    }

    private String toNumber(String string){
        final Pattern pattern = Pattern.compile("<li>T: <strong>(.+?)</strong></li>");
        final Matcher matcher = pattern.matcher(string);
        matcher.find();
        return matcher.group(1);

    }

    private String toStatus(String str){
        final Pattern pattern = Pattern.compile("<span class=\"opened\">(.+?)</span>|<span class=\"closed\">(.+?)</span>");
        final Matcher matcher = pattern.matcher(str);
        matcher.find();
        return matcher.group(1);

    }

//   private void write() throws IOException {
//        JSONObject obj = new JSONObject();
//        for(int i = 0;i<this.stations.size();i++){
//            String addres = this.stations.get(i).getName();
//            String number = this.stations.get(i).getNumber();
//            String link = this.stations.get(i).getUrl();
//            String status = this.stations.get(i).getStatus();
//            Kordinate kordinati= this.stations.get(i).getCordinates();
//            obj.put("address",addres);
//            obj.put("phone number", number);
//            obj.put("url", link);
//            obj.put("status", status);
//            obj.put("x", kordinati.getX());
//            obj.put("y", kordinati.getY());
//            try (FileWriter file = new FileWriter("/Users/nikod/Documents/file1.txt",true)) {
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                JsonParser jp = new JsonParser();
//                JsonElement je = jp.parse(obj.toJSONString());
//
//                String prettyJsonString = gson.toJson(je);
//                file.write(prettyJsonString);
//            }
//            obj.clear();
//        }
//    }








    @Override
    protected Void doInBackground(Void... params) {
        try {
            this.link=new URL(naslov);

            Document doc = Jsoup.connect(link.toString()+"/bencinski-servisi/zemljevid").timeout(10*1000).get();
            Elements hrefAnchors = doc.select("a[href]");
            Elements cordin = doc.select("script[type~=text/javascript]");
            //createMarker()....
            String[] podatki = cordin.toString().split("\n");

            for(String s:podatki){
                //System.out.println(s);
                String str = s.trim();
                if(str.contains("createMarker")){
                    GasStation gs = new GasStation(new URL(toChildLink(str)), toName(str), toKordinata(str)[0], toKordinata(str)[1], toNaslov(str), toNumber(str), toStatus(str));
                    mainMap.addGasStation(gs);
                    //Station stat = new Station(toChildLink(str),toNaslov(str),toKordinata(str),toNumber(str),toStatus(str));
                    //stations.add(stat);
                    //System.out.println(stat);
                }
            }
          //write();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mainMap.done();
    }
}



