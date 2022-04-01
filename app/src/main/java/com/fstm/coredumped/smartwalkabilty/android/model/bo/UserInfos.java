package com.fstm.coredumped.smartwalkabilty.android.model.bo;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class UserInfos {
    private Context myContext;
    private static UserInfos userInfos;
    private double radius=15;
    private boolean routing=false;
    private List<Integer> cats=new ArrayList<>();

    public List<Integer> getCats() {
        return cats;
    }

    public void setCats(List<Integer> cats) {
        this.cats = cats;
    }

    public synchronized boolean isRouting() {
        return routing;
    }

    public synchronized void setRouting(boolean routing) {
        this.routing = routing;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    private UserInfos(Context myContext) {
        this.myContext = myContext;
        cats.add(1);
        cats.add(2);
    }

    public static void initUserInfosObject(Context context)
    {
       userInfos=new UserInfos(context);
    }
    public static UserInfos getInstance()
    {
        return userInfos;
    }
    public GeoPoint getCurrentLocation() {
        try {
            LocationManager locationManager = (LocationManager) myContext.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("Impossible");
                return new GeoPoint();
            }
            Location location = getLastKnownLocation(locationManager);
            return new GeoPoint(location.getLatitude(),location.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
            return new GeoPoint();
        }
    }
    public static void DemandeTurnOnGPS(){

    }
    private static Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
