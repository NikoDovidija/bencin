package com.gombiart.bencin;


import java.net.URL;

public class Station {
    private URL urlCrpalke;
    private String naslovCrpalke,urlCenika;

    public Station(URL urlCrpalke){
        this.urlCrpalke = urlCrpalke;
    }


    public Station(URL urlCrpalke, String urlCenika,String naslovCrpalke){
        this.urlCrpalke = urlCrpalke;
        this.urlCenika=urlCenika;
        this.naslovCrpalke=naslovCrpalke;

    }

    /**
     * @return the name
     */
    public String getNaslovCrpalke() {
        return naslovCrpalke;
    }

    public URL getUrlCrpalke(){
        return urlCrpalke;
    }




    /**
     * @param name the name to set
     */
    public void setNaslovCrpalke(String name) {
        this.naslovCrpalke = naslovCrpalke;
    }

    /**
     * @return the url
     */
    public String getUrlCenika() {
        return urlCenika;
    }

    /**
     * @param url the url to set
     */
    public void setUrlCenika(String url) {
        this.urlCenika = url;
    }
}
