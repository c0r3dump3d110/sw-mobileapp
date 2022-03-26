package com.fstm.coredumped.smartwalkabilty.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Chemin;
import com.fstm.coredumped.smartwalkabilty.core.routing.model.bo.Vertex;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class MyTouchOverlay extends Overlay
{
    com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint depart,Arrive;
    Marker DepartMark,ArriveMark;
    Context pContext;
    MapView mapView ;
    public MyTouchOverlay(Context context){
        pContext=context;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
       this.mapView=mapView;
        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        double longitude = Geo.tronque( loc.getLongitude(),7);
        double latitude = Geo.tronque(loc.getLatitude(),7);
        com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint=new com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint(latitude,longitude);

        if(depart==null){
            depart=geoPoint;
            ReshowMarkerDep(loc);
            DepartMark.setTitle("Depart");
        }
        else if(Arrive==null){
            Arrive=geoPoint;
            ReshowMarkerArr(loc);
            ArriveMark.setTitle("Arrive");
        }
        else {
            Arrive=null;
            ArriveMark.setVisible(false);
            depart=geoPoint;
            ReshowMarkerDep(loc);
        }
        if(Arrive!=null)BeginRouting();
        return true;
    }
    private Marker CreateMarker(GeoPoint loc)
    {
        Drawable marker=pContext.getDrawable(R.drawable.marker_default);
        Marker marker1=new Marker(mapView);
        marker1.setPosition(loc);
        marker1.setIcon(marker);
        mapView.getOverlays().add(marker1);
        return marker1;
    }
    private void ReshowMarkerDep(GeoPoint loc){
        if(DepartMark==null)
        {
            DepartMark=CreateMarker(loc);
            DepartMark.setTitle("Depart");
        }
        else{
            DepartMark.setPosition(loc);
            DepartMark.setVisible(true);
        }
    }
    private void ReshowMarkerArr(GeoPoint loc){
        if(ArriveMark==null){
            ArriveMark=CreateMarker(loc);
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
        Set<GeoPoint> points = new HashSet<>();
        if(chemin.getPriority()==1 || chemin.getPriority()==-1)
        for (Vertex v: chemin.getVertices()) {
           List<GeoPoint> list=new ArrayList<>();
            list.add(Geo.turnGEOOSM(v.getArrive()));
            list.add(Geo.turnGEOOSM(v.getDepart()));
            Polyline line = new Polyline();
            line.setPoints(list);
           if(chemin.getPriority()==1)  line.getOutlinePaint().setColor(Color.BLUE);
           else line.getOutlinePaint().setColor(Color.GREEN);
            line.setGeodesic(true);
            line.setVisible(true);
            mapView.getOverlays().add(line);
            mapView.invalidate();

        }

       /* for (GeoPoint g:
             points) {
            CreateMarker(g);
        }*/
        //List<GeoPoint> list=new ArrayList<>();
        //list.addAll(points);


        //line.setEnabled(true);

    }

}
class Geo{

    public static  GeoPoint turnGEOOSM(com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint){
        GeoPoint point = new org.osmdroid.util.GeoPoint(geoPoint.getLaltittude(),geoPoint.getLongtitude());
        return point;
    }
    public static double tronque(double x,int nbElem)
    {
        String str,prefix,suffix;
        StringTokenizer strs=new StringTokenizer(Double.toString(x),".");
        prefix=strs.nextToken();
        if(!strs.hasMoreTokens()||nbElem==0){
            return Double.parseDouble(prefix);
        }
        str=prefix;
        suffix=strs.nextToken();
        if(suffix.length()>nbElem)suffix=suffix.substring(0,nbElem);
        str+="."+suffix;
        return Double.parseDouble(str);
    }
}