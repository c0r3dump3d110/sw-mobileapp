package com.fstm.coredumped.smartwalkabilty.android.model.bo;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class UserInfos {
    private Context myContext;
    private static UserInfos userInfos;
    private double radius=15;
    private boolean routing=false;
    private Location curentlocation;

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
       userInfos.DemandLocationOnGPS();
    }
    public static UserInfos getInstance()
    {
        return userInfos;
    }
    public GeoPoint getCurrentLocation() {
        if(curentlocation!=null)return new GeoPoint(curentlocation.getLatitude(),curentlocation.getLongitude());
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
    @SuppressLint("MissingPermission")
    private void DemandLocationOnGPS()
    {
        locatlist loc=new locatlist();
        LocationManager locationManager = (LocationManager) myContext.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, loc);
    }
    class locatlist implements LocationListener {
        final int HALF_MINUTE = 1000 * 30;
        @Override
        public void onLocationChanged(Location locat) {

            if(curentlocation == null){
                curentlocation =locat;
            }
            makeUseOfNewLocation(locat);
        }
        void makeUseOfNewLocation(Location location) {
            if ( isBetterLocation(location, curentlocation) ) {
                curentlocation = location;
            }
        }
        private boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                return true;
            }
            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > HALF_MINUTE;
            boolean isSignificantlyOlder = timeDelta < -HALF_MINUTE;
            boolean isNewer = timeDelta > 0;
            if (isSignificantlyNewer) {
                return true;
            } else if (isSignificantlyOlder) {
                return false;
            }
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;
            boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
            if (isMoreAccurate) {
                return true;
            } else if (isNewer && !isLessAccurate) {
                return true;
            } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
        }
        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }

    }
}
