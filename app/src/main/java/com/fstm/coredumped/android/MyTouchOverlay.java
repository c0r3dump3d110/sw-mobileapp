package com.fstm.coredumped.android;

import android.content.Context;
import android.view.MotionEvent;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

public class MyTouchOverlay extends Overlay
{
    GeoPoint depart;
    GeoPoint Arrive;
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        String longitude = Double.toString(((double)loc.getLongitude()));
        String latitude = Double.toString(((double)loc.getLatitude()));
        if(depart==null)depart=loc;
        else if(Arrive==null)Arrive=loc;
        else {
            Arrive=null;
            depart=loc;
        }
        if(depart!=null&&Arrive!=null)BeginRouting();
        return true;
    }
    private void BeginRouting(){
        System.out.println("Depart  Longitude: "+depart.getLongitude()+" latitude : "+depart.getLatitude());
        System.out.println("Arriver  Longitude: "+Arrive.getLongitude()+" latitude : "+Arrive.getLatitude());
    }
}
