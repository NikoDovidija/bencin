package com.gombiart.bencin;



public class Kordinate {
    private float x;
    private float y;

    public Kordinate(float x,float y){
        this.x=x;
        this.y=y;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }


    @Override
    public String toString(){
        return "Kordianta x:"+this.x+" Kordinata y:"+this.y;
    }
}
