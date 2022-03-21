package com.fstm.coredumped.smartwalkabilty.android;

import android.view.MotionEvent;

import com.fstm.coredumped.smartwalkabilty.routing.model.bo.Chemin;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

public class MyTouchOverlay extends Overlay
{
    com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint depart;
    com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint Arrive;
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint=new com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint(latitude,longitude);
        if(depart==null){
            depart=geoPoint;
        }
        else if(Arrive==null)Arrive=geoPoint;
        else {
            Arrive=null;
            depart=geoPoint;
        }
        if(Arrive!=null)BeginRouting();
        return true;
    }
    private void BeginRouting()
    {
        System.out.println("Depart  Longitude: "+depart.getLongtitude()+" latitude : "+depart.getLaltittude());
        System.out.println("Arriver  Longitude: "+Arrive.getLongtitude()+" latitude : "+Arrive.getLaltittude());
        new ClientSocket().SendRoutingReq(this,depart,Arrive);
    }
    public void VisualiseChemin(Chemin chemin)
    {

    }
}
