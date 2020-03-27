package me.cmpt276.restaurantinspector.GoogleMaps;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private BitmapDescriptor mColor;

    public MyItem(LatLng lat, String title, String snippet, BitmapDescriptor color) {
        mPosition = lat;
        mTitle = title;
        mSnippet = snippet;
        mColor = color;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public BitmapDescriptor getColor(){return mColor;}

}

