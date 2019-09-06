package com.nds.dlp;

public class GetCell {

    /**
     * radio : UMTS
     * mcc : 460
     * net : 1
     * area : 47872
     * cell : 201338294
     * lon : 108.88206481934
     * lat : 34.190139770508
     * tagno : 17
     */

    private String radio;
    private int mcc;
    private int net;
    private int area;
    private int cell;
    private double lon;
    private double lat;
    private int tagno;

    public GetCell(){
        super();
    }

    public GetCell(String radio,
                   int mcc,
                   int net,
                   int area,
                   int cell,
                   double lon,
                   double lat,
                   int tagno
    )
    {
        super();
        this.radio = radio;
        this.mcc = mcc;
        this.net = net;
        this.area = area;
        this.cell = cell;
        this.lon = lon;
        this.lat = lat;
        this.tagno = tagno;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getNet() {
        return net;
    }

    public void setNet(int net) {
        this.net = net;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getTagno() {
        return tagno;
    }

    public void setTagno(int tagno) {
        this.tagno = tagno;
    }

    @Override
    public String toString() {
        return "Student [radio = " + radio + ", mcc = " + mcc + ", net = " + net
                + ", area = " + area + ", cell = " + cell + ", long = " + lon
                + ", lat = " + lat + ", tagno = " + tagno + "]";
    }

}

