package com.fstm.coredumped.smartwalkabilty.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.routing.model.bo.Chemin;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

public class MyTouchOverlay extends Overlay
{
    com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint depart;
    com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint Arrive;
    Context pContext;
    public MyTouchOverlay(Context context){
        pContext=context;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

        Drawable marker=pContext.getDrawable(R.drawable.marker_default);
        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint=new com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint(latitude,longitude);
        Marker marker1=new Marker(mapView);
        marker1.setPosition(loc);
        marker1.setIcon(marker);
        mapView.getOverlays().add(marker1);
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
