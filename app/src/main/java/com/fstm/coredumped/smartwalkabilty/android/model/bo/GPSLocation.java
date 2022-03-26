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

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class GPSLocation {
    public static Context myContext;

    public static void initLocation(Context context) {
        myContext = context;
    }

    public static GeoPoint getCurrentLocation() {
        try {
            LocationManager locationManager = (LocationManager) myContext.getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                DemandeTurnOnGPS();
            }
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
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
