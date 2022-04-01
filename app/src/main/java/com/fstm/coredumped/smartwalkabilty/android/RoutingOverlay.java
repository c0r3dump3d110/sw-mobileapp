package com.fstm.coredumped.smartwalkabilty.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.android.deamon.RoutingHelper;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

public class RoutingOverlay extends Overlay
{
    public static final int METHOD_TWO_POINTS=2;
    public static final int METHOD_ONE_POINTS=1;
    private com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint depart,Arrive;
    private Marker DepartMark,ArriveMark;
    private Context pContext;
    private MapView mapView ;
    private int method =METHOD_TWO_POINTS;
    private RoutingHelper helper;

    public void setDepartMark(Marker departMark) {
        DepartMark = departMark;
    }

    public Marker getArriveMark() {
        return ArriveMark;
    }

    public void setArriveMark(Marker arriveMark) {
        ArriveMark = arriveMark;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public RoutingHelper getHelper() {
        return helper;
    }

    public void setHelper(RoutingHelper helper) {
        this.helper = helper;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint getDepart() {
        return depart;
    }

    public void setDepart(com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint depart) {
        this.depart = depart;
    }

    public com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint getArrive() {
        return Arrive;
    }

    public void setArrive(com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint arrive) {
        Arrive = arrive;
    }

    public Marker getDepartMark() {
        return DepartMark;
    }

    public RoutingOverlay(Context context){
        pContext=context;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if(method==METHOD_TWO_POINTS) return MethodNotLocation(e,mapView);
        else if(method==METHOD_ONE_POINTS) return MethodLocation(e,mapView);

        return true;
    }


    private boolean MethodLocation(MotionEvent e,MapView mapView){
        this.mapView=mapView;
        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        double longitude = GeoMethods.tronque( loc.getLongitude(),7);
        double latitude = GeoMethods.tronque(loc.getLatitude(),7);
        com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint geoPoint=new com.fstm.coredumped.smartwalkabilty.common.model.bo.GeoPoint(latitude,longitude);
        depart= UserInfos.getInstance().getCurrentLocation();
        ReshowMarkerDep(GeoMethods.turnGEOOSM(depart));
        DepartMark.setTitle("Depart");
        if(Arrive==null){
            Arrive=geoPoint;
            ReshowMarkerArr(loc);
            ArriveMark.setTitle("Arrive");
        }
        else {
            Arrive=geoPoint;
            ArriveMark.setVisible(false);
            ReshowMarkerArr(loc);
            if(helper!=null)helper.stopMe();
        }
        BeginRouting();
        return true;
    }

    private boolean MethodNotLocation(MotionEvent e,MapView mapView){
        this.mapView=mapView;
        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
        double longitude = GeoMethods.tronque( loc.getLongitude(),7);
        double latitude = GeoMethods.tronque(loc.getLatitude(),7);
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
            if(helper!=null)helper.justClear();
        }
        if(Arrive!=null)BeginRouting();
        return true;
    }
    private Marker CreateMarker(GeoPoint loc,boolean is_person)
    {
        Drawable marker;
        if(is_person) marker=pContext.getDrawable(R.drawable.person);
        else marker=pContext.getDrawable(R.drawable.marker_default);
        Marker marker1=new Marker(mapView);
        marker1.setPosition(loc);
        marker1.setIcon(marker);
        mapView.getOverlays().add(marker1);
        return marker1;
    }
    private void ReshowMarkerDep(GeoPoint loc){
        if(DepartMark==null)
        {
            DepartMark=CreateMarker(loc,true);
            DepartMark.setTitle("Depart");
        }
        else{
            DepartMark.setPosition(loc);
            DepartMark.setVisible(true);
        }
    }
    private void ReshowMarkerArr(GeoPoint loc){
        if(ArriveMark==null){
            ArriveMark=CreateMarker(loc,false);
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


}
