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
    com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint depart,Arrive;
    Marker DepartMark,ArriveMark;
    Context pContext;
    public MyTouchOverlay(Context context){
        pContext=context;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint=new com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint(latitude,longitude);

        if(depart==null){
            depart=geoPoint;
            ReshowMarkerDep(loc,mapView);
            DepartMark.setTitle("Depart");
        }
        else if(Arrive==null){
            Arrive=geoPoint;
            ReshowMarkerArr(loc,mapView);
            ArriveMark.setTitle("Arrive");
        }
        else {
            Arrive=null;
            ArriveMark.setVisible(false);
            depart=geoPoint;
            ReshowMarkerDep(loc,mapView);
        }
        if(Arrive!=null)BeginRouting();
        return true;
    }
    private Marker CreateMarker(GeoPoint loc,MapView mapView)
    {
        Drawable marker=pContext.getDrawable(R.drawable.marker_default);
        Marker marker1=new Marker(mapView);
        marker1.setPosition(loc);
        marker1.setIcon(marker);
        mapView.getOverlays().add(marker1);
        return marker1;
    }
    private void ReshowMarkerDep(GeoPoint loc,MapView mapView){
        if(DepartMark==null)
        {
            DepartMark=CreateMarker(loc,mapView);
            DepartMark.setTitle("Depart");
        }
        else{
            DepartMark.setPosition(loc);
            DepartMark.setVisible(true);
        }
    }
    private void ReshowMarkerArr(GeoPoint loc,MapView mapView){
        if(ArriveMark==null){
            ArriveMark=CreateMarker(loc,mapView);
        ArriveMark.setTitle("Arrive");
        }
        else{
            ArriveMark.setPosition(loc);
            ArriveMark.setVisible(true);

        }
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
