package app.capstone.assem.com.capstone.Models;

import java.io.Serializable;

public class PlaceBookmarkModel implements Serializable {

    private String uid;
    private double lat;
    private double lon;
    private String title;

    public PlaceBookmarkModel() {
    }

    public PlaceBookmarkModel(String uid, double lat, double lon, String title) {
        this.uid = uid;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
