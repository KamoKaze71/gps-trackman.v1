//package com.pekam;
//
//
//
//import java.io.Serializable;
//
//
//
//import java.sql.Timestamp;
//
//
///**
// * The persistent class for the tblGps database table.
// *
// */
//
//public class TblGps implements Serializable {
//    private static final long serialVersionUID = 1L;
//    private long id;
//    private Timestamp date ;
//    private String descr;
//    private double lat;
//    private double lon;
//    private String provider;
//    private String deviceID;
//    private int userid;
//
//
//    @SuppressWarnings("deprecation")
//    public TblGps() {
//
//
//        date= new Timestamp(0,0,0,0,0, 0, 0);
//    }
//
//    public long getId() {
//        return this.id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//
//
//    public Timestamp getDate() {
//        return this.date;
//    }
//
//    public void setDate(Timestamp date) {
//        this.date = date;
//    }
//
//
//    public String getDescr() {
//        return this.descr;
//    }
//
//    public void setDescr(String descr) {
//        this.descr = descr;
//    }
//
//    public String getDeviceID() {
//        return this.deviceID;
//    }
//
//    public void setDeviceID(String deviceID) {
//        this.deviceID = deviceID;
//    }
//
//
//    public String getProvider() {
//        return this.provider;
//    }
//
//    public void setProvider(String provider) {
//        this.provider = provider;
//    }
//
//
//
//    public double getLat() {
//        return this.lat;
//    }
//
//    public void setLat(double lat) {
//        this.lat =lat;
//    }
//
//
//
//    public double getLon() {
//        return this.lon;
//    }
//
//    public void setLon(double longi ) {
//        this.lon = longi;
//    }
//
//
//    public int getUserid() {
//        return this.userid;
//    }
//
//    public void setUserid(int userid) {
//        this.userid = userid;
//    }
//
//}